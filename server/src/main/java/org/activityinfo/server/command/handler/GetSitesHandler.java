package org.activityinfo.server.command.handler;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.server.command.handler.adapter.SiteFormQuery;
import org.activityinfo.server.command.handler.adapter.SiteQueryAdapter;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreReader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class GetSitesHandler implements CommandHandler<GetSites> {

    private static final Logger LOGGER = Logger.getLogger(GetSitesHandler.class.getName());

    private final ResourceStore store;

    @Inject
    public GetSitesHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(GetSites cmd, User userEntity) throws CommandException {

        AuthenticatedUser user = new AuthenticatedUser(userEntity.getId());
        try(StoreReader storeReader = store.openReader(user)) {

            // We can operate only per-form class here.
            List<SiteFormQuery> forms = getForms(storeReader, cmd);

            // Normalize messy SortInfo
            Optional<SortInfo> sort = isSorted(cmd);

            // Start reading from table store
            boolean sorted = sort.isPresent();
            boolean paged = isPaged(cmd);
            boolean singleForm = (forms.size() == 1);

            if (singleForm) {
                // simplest case, just perform any sorting/page on the single form
                return forms.get(0).executeQuery(cmd.getOffset(), cmd.getLimit(), sort);

            } else if (paged && !sorted) {

                // if we're not sorting, we can apply the slicing in page at a time
                int offset = cmd.getOffset();
                int limit = cmd.getLimit();
                int totalCount = 0;
                List<SiteDTO> sites = Lists.newArrayList();
                for (SiteFormQuery form : forms) {
                    SiteResult formResult = form.executeQuery(offset, limit, sort);
                    sites.addAll(formResult.getData());

                    int numRows = formResult.getData().size();
                    totalCount += formResult.getTotalLength();
                    offset += numRows;
                    limit = Math.min(0, limit - numRows);
                }
                return new SiteResult(sites, cmd.getOffset(), totalCount);

            } else {

                // if we need to sort, then copy everything into dtos first, then sort and page
                List<SiteDTO> sites = Lists.newArrayList();
                for (SiteFormQuery form : forms) {
                    SiteResult formResult = form.executeQuery(0, -1, Optional.<SortInfo>absent());
                    sites.addAll(formResult.getData());
                }
                int totalCount = sites.size();

                if (sorted) {
                    sort(sites, cmd.getSortInfo());
                }
                int start = Math.max(0, cmd.getOffset());
                int end = totalCount;
                if (cmd.getLimit() > 0) {
                    end = Math.min(totalCount, start + cmd.getLimit());
                }

                return new SiteResult(sites.subList(start, end), 0, totalCount);
            }
        }
    }

    private List<SiteFormQuery> getForms(StoreReader storeReader, GetSites cmd) {
        List<SiteFormQuery> forms = Lists.newArrayList();
        for(ResourceId classId : SiteQueryAdapter.getFormClasses(storeReader, cmd.getFilter())) {
            forms.add(new SiteFormQuery(cmd, storeReader, classId));
        }
        return forms;
    }

    private void sort(List<SiteDTO> sites, SortInfo sortInfo) {

        final String fieldName = sortInfo.getSortField();
        Preconditions.checkArgument(fieldName != null, "fieldName is null");
        Ordering<SiteDTO> ordering = Ordering.natural().onResultOf(new Function<SiteDTO, Comparable>() {
            @Nullable
            @Override
            public Comparable apply(SiteDTO input) {
                return input.get(fieldName);
            }
        });

        if(sortInfo.getSortDir() == Style.SortDir.DESC) {
            ordering = ordering.reverse();
        }

        Collections.sort(sites, ordering);
    }

    private boolean isPaged(GetSites cmd) {
        return cmd.getOffset() > 0 || cmd.getLimit() > 0;
    }

    private Optional<SortInfo> isSorted(GetSites query) {
        SortInfo info = query.getSortInfo();
        if(info == null) {
            return Optional.absent();
        }
        if(Strings.isNullOrEmpty(info.getSortField())) {
            return Optional.absent();
        }
        if(info.getSortDir() == Style.SortDir.NONE) {
            return Optional.absent();
        }
        return Optional.of(info);
    }
}
