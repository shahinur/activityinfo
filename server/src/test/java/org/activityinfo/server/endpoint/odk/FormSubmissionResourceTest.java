package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.NarrativeValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.time.LocalDate;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.TestBlobFieldStorageService;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

import static com.google.common.io.Resources.asByteSource;
import static com.google.common.io.Resources.getResource;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.fromStatusCode;
import static org.activityinfo.model.legacy.CuidAdapter.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FormSubmissionResourceTest {
    private FormSubmissionResource resource;
    private TestResourceStore store;

    public static final ResourceId CLASS_ID = activityFormClass(1081);

    @Before
    public void setUp() throws IOException {
        // TODO Create form as part of test to avoid problems with id migrations
        store = new TestResourceStore().load("/dbunit/formSubmissionResourceTest.json");
        OdkFieldValueParserFactory factory = new OdkFieldValueParserFactory(store);
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
        assertEquals(CLASS_ID.asString(), map.get("classId"));
        assertEquals(new ReferenceValue(partnerInstanceId(507, 562)).asRecord(), map.get(fieldName(PARTNER_FIELD)));
        assertEquals(new LocalDate(2005, 8, 31).asRecord(), map.get(fieldName(END_DATE_FIELD)));
        assertEquals("09/06/06", map.get(CODE_FIELD));
        assertEquals(new ReferenceValue(entity(141796)).asRecord(), map.get("a1081f11"));
        assertNull(map.get("i5346"));
        assertEquals(new NarrativeValue("Awesome.").asRecord(), map.get("a1081f14"));
        assertNotNull(map.get("backupBlobId"));
    }

    private String fieldName(int fieldIndex) {
        return field(CLASS_ID, fieldIndex).asString();
    }
}
