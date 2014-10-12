package org.activityinfo.server.command.handler;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.api.client.util.Lists;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.server.command.handler.table.QueryBuilder;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.ResourceStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GetSitesHandler implements CommandHandler<GetSites> {

    private final ResourceStore store;

    @Inject
    public GetSitesHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(GetSites cmd, User userEntity) throws CommandException {

        AuthenticatedUser user = new AuthenticatedUser(userEntity.getId());

        // We can operate only per-form class here.
        List<ResourceId> formClassIds = queryFormClassesFromFilter(user, cmd.getFilter());

        // Start reading from table store

        List<SiteDTO> results = Lists.newArrayList();
        for(ResourceId formClass : formClassIds) {
            results.addAll(queryForm(cmd, user, formClass));
        }
        return new SiteResult(results);
    }

    private List<ResourceId> queryFormClassesFromFilter(AuthenticatedUser user, Filter filter) {
        List<ResourceId> formClasses = Lists.newArrayList();
        if(filter.isRestricted(DimensionType.Activity)) {
            for(Integer activityId : filter.getRestrictions(DimensionType.Activity)) {
                formClasses.add(CuidAdapter.activityFormClass(activityId));
            }
        } else if(filter.isRestricted(DimensionType.Database)) {
            for(Integer databaseId : filter.getRestrictions(DimensionType.Database)) {
                findForms(user, CuidAdapter.databaseId(databaseId), formClasses);
            }
        } else {
            for (ResourceNode resourceNode : store.getOwnedOrSharedWorkspaces(user)) {
                findForms(user, resourceNode, formClasses);
            }
            throw new UnsupportedOperationException("filter = " + filter);
        }
        return formClasses;
    }

    private void findForms(AuthenticatedUser user, ResourceId parent, List<ResourceId> formClasses) {
        FolderProjection result = store.queryTree(user, new FolderRequest(parent));
        findForms(user, result.getRootNode(), formClasses);
    }

    private void findForms(AuthenticatedUser user, ResourceNode parent, List<ResourceId> formClasses) {
        for (ResourceNode resourceNode : parent.getChildren()) {
            if(resourceNode.getClassId().equals(FormClass.CLASS_ID)) {
                formClasses.add(resourceNode.getClassId());
            } else if(resourceNode.getClassId().equals(FolderClass.CLASS_ID)) {
                findForms(user, resourceNode.getId(), formClasses);
            }
        }
    }

    private Collection<SiteDTO> queryForm(GetSites cmd, AuthenticatedUser user, ResourceId formClassId) {
        FormClass formClass = FormClass.fromResource(store.get(user, formClassId).getResource());
        TableModel tableModel = QueryBuilder.build(formClass);
        TableData tableData = store.queryTable(user, tableModel);

        int numColumns = tableData.getColumns().size();
        String columnIds[] = new String[numColumns];
        ColumnView views[] = new ColumnView[numColumns];

        int j = 0;
        for (Map.Entry<String, ColumnView> entry : tableData.getColumns().entrySet()) {
            columnIds[j] = entry.getKey();
            views[j] = entry.getValue();
            j++;
        }

        int[] order = computeOrder(cmd.getSortInfo(), tableData);

        List<SiteDTO> sites = Lists.newArrayList();
        for (int i = 0; i != tableData.getNumRows(); ++i) {
            SiteDTO dto = new SiteDTO();
            int ii;
            if(order == null) {
                ii = i;
            } else {
                ii = order[i];
            }
            for (j = 0; j < numColumns; ++j) {
                dto.set(columnIds[j], views[j].get(ii));
            }
            sites.add(dto);
        }
        return sites;
    }

    private int[] computeOrder(SortInfo sortInfo, TableData tableData) {
        if(Strings.isNullOrEmpty(sortInfo.getSortField()) || sortInfo.getSortDir() == Style.SortDir.NONE) {
            return null;
        } else {
            ColumnView columnView = tableData.getColumnView(sortInfo.getSortField());
            if (columnView == null) {
                throw new UnsupportedOperationException("Unknown sort key [" + sortInfo.getSortField() + "] " +
                    "columns: " + Joiner.on(", ").join(tableData.getColumns().keySet()));
            }

            int[] order = new int[tableData.getNumRows()];
            for (int i = 0; i != order.length; ++i) {
                order[i] = i;
            }

            Ordering ordering = Ordering.natural();
            if(sortInfo.getSortDir() == Style.SortDir.DESC) {
                ordering = ordering.reverse();
            }

            for (int i = 0; i < order.length; i++) {
                for (int j = i; j > 0
                        && ordering.compare(columnView.get(order[j - 1]), columnView.get(order[j])) > 0; j--) {
                    final int b = j - 1;
                    final int t = order[j];
                    order[j] = order[b];
                    order[b] = t;
                }
            }
            return order;
        }
    }
}
