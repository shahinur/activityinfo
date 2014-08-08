package org.activityinfo.server.endpoint.odk;

import com.google.common.io.ByteStreams;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceStore;
import org.activityinfo.model.table.TableService;
import org.activityinfo.model.type.NarrativeValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.time.LocalDate;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.service.tables.TableServiceImpl;
import org.activityinfo.ui.client.service.TestResourceStore;
import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.fromStatusCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(InjectionSupport.class)
public class FormSubmissionResourceTest extends CommandTestCase2 {
    private FormSubmissionResource resource;
    private ResourceStore store;

    @Before
    public void setUp() throws IOException {
        store = new TestResourceStore().load("/dbunit/formSubmissionResourceTest.json");
        TableService table = new TableServiceImpl(store);
        OdkFieldValueParserFactory factory = new OdkFieldValueParserFactory(table);
        resource = new FormSubmissionResource(factory, store);
    }

    @Test
    public void parse() throws Exception {
        try (InputStream inputStream = FormSubmissionResourceTest.class.getResourceAsStream("form.xml")) {
            StringBuffer stringBuffer = new StringBuffer();

            try (StringBufferOutputStream stringBufferOutputStream = new StringBufferOutputStream(stringBuffer)) {
                ByteStreams.copy(inputStream, stringBufferOutputStream);
            }

            Response response = resource.submit(stringBuffer.toString());
            assertEquals(CREATED, fromStatusCode(response.getStatus()));
        }

        Iterator<Resource> iterator = store.openCursor(CuidAdapter.activityFormClass(1081));
        assertTrue(iterator.hasNext());

        Map<String, Object> map = iterator.next().getProperties();
        assertFalse(iterator.hasNext());

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
