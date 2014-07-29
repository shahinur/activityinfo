package org.activityinfo.server.endpoint.odk;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.inject.Inject;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/lcca3.db.xml")
public class FormSubmissionResourceTest extends CommandTestCase2 {

    @Inject
    private FormSubmissionResource resource;


    @Before
    public void setupAdapters() {

        GWTMockUtilities.disarm();
    }

    @Test
    public void parse() throws IOException {

        FormParser formParser = new FormParser();
        SiteFormData data = formParser.parse(getTestXml());
        assertThat(data.getIndicator(36199).getDoubleValue(), equalTo(1.0));
        assertThat(data.getIndicator(36187).getDoubleValue(), equalTo(0.0));
    }


    @Test
    public void testSubmit() throws Exception {

        setUser(4374);

        Response response = resource.submit(getTestXml());
        assertThat(response.getStatus(), equalTo(201));

        SiteResult sites = execute(GetSites.byActivity(6464));
        assertThat(sites.getData(), Matchers.hasSize(1));

        SiteDTO site = sites.getData().get(0);

        assertThat(site.getPartner().getId(), equalTo(1612));
        assertThat(site.getDate1(), equalTo(new LocalDate(2014, 7, 25)));
        assertThat(site.getDate2(), equalTo(new LocalDate(2014, 7, 25)));
        assertThat(site.getDate2(), equalTo(new LocalDate(2014, 7, 25)));
       
        assertThat(site.getIndicatorValue(36185), equalTo((Object)"01"));
        
        assertThat(site.getIndicatorValue(36185), equalTo((Object)"01"));
        assertThat(site.getIndicatorValue(36090), equalTo((Object)"00010"));
        assertThat(site.getIndicatorValue(36106), equalTo((Object)"0010"));
        assertThat(site.getIndicatorDoubleValue(36199), equalTo(1.0));
        assertThat(site.getIndicatorDoubleValue(36201), equalTo(2.0));
        assertThat(site.getIndicatorDoubleValue(36099), equalTo(4.0));
        assertThat(site.getIndicatorDoubleValue(36100), equalTo(6.0));
        assertThat(site.getIndicatorDoubleValue(36104), equalTo(3.0));
        assertThat(site.getIndicatorDoubleValue(36187), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(36188), equalTo(3.0));
        assertThat(site.getIndicatorDoubleValue(36189), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(36190), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(36345), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(36191), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(36192), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(36347), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(36348), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(37047), equalTo(0.0));
        assertThat(site.getIndicatorDoubleValue(37048), equalTo(0.0));
    }


    @Test
    public void testSubmitAutoIdentify() throws Exception {

        setUser(AuthenticatedUser.getAnonymous().getUserId());

        Response response = resource.submit(getTestXml());
        assertThat(response.getStatus(), equalTo(201));

        SiteResult sites = execute(GetSites.byActivity(6464));
        assertThat(sites.getData(), Matchers.hasSize(1));

    }


    private String getTestXml() throws IOException {
        return Resources.toString(getResource(FormSubmissionResourceTest.class, "lcca-instance.xml"), Charsets.UTF_8);
    }
}
