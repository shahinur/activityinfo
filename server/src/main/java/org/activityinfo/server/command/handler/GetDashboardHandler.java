package org.activityinfo.server.command.handler;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.api.client.util.Lists;
import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.activityinfo.legacy.shared.command.GetDashboard;
import org.activityinfo.legacy.shared.command.GetReportModel;
import org.activityinfo.legacy.shared.command.GetReports;
import org.activityinfo.legacy.shared.command.result.DashboardResult;
import org.activityinfo.legacy.shared.command.result.ReportsResult;
import org.activityinfo.legacy.shared.impl.CommandHandlerAsync;
import org.activityinfo.legacy.shared.impl.ExecutionContext;
import org.activityinfo.legacy.shared.model.ReportDTO;
import org.activityinfo.legacy.shared.model.ReportMetadataDTO;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.endpoint.gwtrpc.RemoteExecutionContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author yuriyz on 10/14/2014.
 */
public class GetDashboardHandler implements CommandHandlerAsync<GetDashboard, DashboardResult> {

    private final GetReportsHandler reportsHandler;
    private final GetReportModelHandler reportModelHandler;
    private final Injector injector;

    @Inject
    public GetDashboardHandler(Injector injector, GetReportsHandler reportsHandler, GetReportModelHandler reportModelHandler) {
        this.injector = injector;
        this.reportsHandler = reportsHandler;
        this.reportModelHandler = reportModelHandler;
    }

    @Override
    public void execute(GetDashboard command, final ExecutionContext context, final AsyncCallback<DashboardResult> callback) {
        final DashboardResult dashboardResult = new DashboardResult();
        final RemoteExecutionContext newContext = new RemoteExecutionContext(injector);

        final Promise<ReportsResult> promise = new Promise<>();
        promise.then(new FilterByDashboardFlag())
                .then(new FetchReportDtoAndFillResults(dashboardResult, newContext))
                .then(new Function<Object, Object>() {
                    @Nullable
                    @Override
                    public Object apply(@Nullable Object input) {
                        callback.onSuccess(dashboardResult);
                        return null;
                    }
                });
        reportsHandler.execute(new GetReports(), context, promise);

    }

    private class FetchReportDtoAndFillResults implements Function<List<ReportMetadataDTO>, Promise<Void>> {

        private DashboardResult dashboardResult;
        private ExecutionContext executionContext;


        private FetchReportDtoAndFillResults(DashboardResult dashboardResult, ExecutionContext executionContext) {
            this.dashboardResult = dashboardResult;
            this.executionContext = executionContext;
        }

        @Override
        public Promise<Void> apply(List<ReportMetadataDTO> input) {
            List<Promise<ReportDTO>> reportModelPromises = Lists.newArrayList();
            for (final ReportMetadataDTO metadata : input) {
                Promise<ReportDTO> newReportPromise = new Promise<ReportDTO>();
                newReportPromise.then(new Function<ReportDTO, ReportDTO>() {
                    @Nullable
                    @Override
                    public ReportDTO apply(ReportDTO input) {
                        input.setReportMetadataDTO(metadata);
                        dashboardResult.getReportList().add(input);
                        return null;
                    }
                });
                reportModelPromises.add(newReportPromise);

                reportModelHandler.execute(new GetReportModel(metadata.getId()), executionContext, newReportPromise);
            }

            return Promise.waitAll(reportModelPromises);
        }
    }

    private static class FilterByDashboardFlag implements Function<ReportsResult, List<ReportMetadataDTO>> {
        @Override
        public List<ReportMetadataDTO> apply(ReportsResult input) {
            final List<ReportMetadataDTO> result = Lists.newArrayList();
            for (ReportMetadataDTO dto : input.getData()) {
                if (dto.isDashboard()) {
                    result.add(dto);
                }
            }
            return result;
        }
    }
}
