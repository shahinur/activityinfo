package org.activityinfo.server.endpoint.odk;

import com.google.common.base.Charsets;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.type.NarrativeValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.time.LocalDate;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.ui.client.service.TestResourceStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.fromStatusCode;
import static org.junit.Assert.assertEquals;

public class FormSubmissionResourceTest {
    private FormSubmissionResource resource;
    private TestResourceStore store;

    @Before
    public void setUp() throws IOException {
        store = new TestResourceStore().load("/dbunit/formSubmissionResourceTest.json");
        OdkFieldValueParserFactory factory = new OdkFieldValueParserFactory();
        AuthenticationTokenService authenticationTokenService = new TestAuthenticationTokenService();
        resource = new FormSubmissionResource(factory, store, authenticationTokenService);
    }

    @Test
    public void parse() throws IOException {

        String xml = asCharSource(getResource(FormSubmissionResourceTest.class, "form.xml"), Charsets.UTF_8).read();

        Response response = resource.submit(xml);
        assertEquals(CREATED, fromStatusCode(response.getStatus()));

        Map<String, Object> map = store.getLastUpdated().getProperties();

        assertEquals(7, map.size());
        assertEquals("a1081", map.get("classId"));
        assertEquals(new ReferenceValue(CuidAdapter.partnerInstanceId(507, 562)).asRecord(), map.get("a1081f7"));
        assertEquals(new LocalDate(2005, 8, 31).asRecord(), map.get("a1081f12"));
        assertEquals(new LocalDate(2006, 9, 6).asRecord(), map.get("a1081f13"));
        assertEquals(new ReferenceValue(CuidAdapter.entity(141796)).asRecord(), map.get("a1081f11"));
        assertEquals(new Quantity(42.0, "%").asRecord(), map.get("i5346"));
        assertEquals(new NarrativeValue("Awesome.").asRecord(), map.get("a1081f14"));
    }
}
