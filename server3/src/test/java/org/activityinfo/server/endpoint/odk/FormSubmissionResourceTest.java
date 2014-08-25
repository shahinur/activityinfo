package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.type.NarrativeValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.time.LocalDate;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.TestBlobFieldStorageService;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

import static com.google.common.io.Resources.asByteSource;
import static com.google.common.io.Resources.getResource;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.fromStatusCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FormSubmissionResourceTest {
    private FormSubmissionResource resource;
    private TestResourceStore store;

    @Before
    public void setUp() throws IOException {
        store = new TestResourceStore().load("/dbunit/formSubmissionResourceTest.json");
        OdkFieldValueParserFactory factory = new OdkFieldValueParserFactory();
        AuthenticationTokenService authenticationTokenService = new TestAuthenticationTokenService();
        BlobFieldStorageService blobFieldStorageService = new TestBlobFieldStorageService();
        resource = new FormSubmissionResource(
                factory, store, authenticationTokenService, blobFieldStorageService, null);
    }

    @Test
    public void parse() throws IOException {
        byte bytes[] = asByteSource(getResource(FormSubmissionResourceTest.class, "form.mime")).read();

        Response response = resource.submit(bytes);
        assertEquals(CREATED, fromStatusCode(response.getStatus()));

        Map<String, Object> map = store.getLastUpdated().getProperties();

        assertEquals(7, map.size());
        assertEquals("a1081", map.get("classId"));
        assertEquals(new ReferenceValue(CuidAdapter.partnerInstanceId(507, 562)).asRecord(), map.get("a1081f7"));
        assertEquals(new LocalDate(2005, 8, 31).asRecord(), map.get("a1081f12"));
        assertEquals("09/06/06", map.get("a1081f13"));
        assertEquals(new ReferenceValue(CuidAdapter.entity(141796)).asRecord(), map.get("a1081f11"));
        assertNull(map.get("i5346"));
        assertEquals(new NarrativeValue("Awesome.").asRecord(), map.get("a1081f14"));
        assertNotNull(map.get("backupBlobId"));
    }
}
