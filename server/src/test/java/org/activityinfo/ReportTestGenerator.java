package org.activityinfo;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.activityinfo.fixtures.MockHibernateModule;
import org.activityinfo.legacy.shared.command.GetReportModel;
import org.activityinfo.legacy.shared.exception.UnexpectedCommandException;
import org.activityinfo.legacy.shared.reports.model.PivotReportElement;
import org.activityinfo.legacy.shared.reports.model.Report;
import org.activityinfo.legacy.shared.reports.model.ReportElement;
import org.activityinfo.server.authentication.AuthenticationModuleStub;
import org.activityinfo.server.database.TestDatabaseModule;
import org.activityinfo.server.database.hibernate.entity.ReportDefinition;
import org.activityinfo.server.endpoint.gwtrpc.GwtRpcModule;
import org.activityinfo.server.report.ReportParserJaxb;
import org.activityinfo.server.util.TemplateModule;
import org.activityinfo.server.util.blob.BlobServiceModuleStub;
import org.activityinfo.server.util.config.ConfigModuleStub;
import org.activityinfo.ui.client.page.report.ReportElementModel;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;
import java.util.List;

public class ReportTestGenerator {

    public void main(String[] args) {

        Injector injector = Guice.createInjector(
                new TestDatabaseModule(),
                new MockHibernateModule(),
                new TemplateModule(),
                new GwtRpcModule(),
                new AuthenticationModuleStub(),
                new BlobServiceModuleStub(),
                new ConfigModuleStub());

        EntityManager em = injector.getInstance(EntityManager.class);
        List<ReportDefinition> resultList = em.createQuery(
                "select r from ReportDefinition r where r.dateDeleted is null and " + "r.title is not null",
                ReportDefinition.class).getResultList();

        for(ReportDefinition def : resultList) {
            Report report = parseReportModel(def);
            for(ReportElement element : report.getElements()) {
                if(element instanceof PivotReportElement) {

                }
            }
        }

    }

    private Report parseReportModel(ReportDefinition def) {

        try {
            return ReportParserJaxb.parseXml(def.getXml());

        } catch (JAXBException e) {
            throw new UnexpectedCommandException(e);
        }

    }

}
