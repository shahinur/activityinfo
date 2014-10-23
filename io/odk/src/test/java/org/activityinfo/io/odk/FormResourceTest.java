package org.activityinfo.io.odk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import org.activityinfo.io.odk.xform.Html;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class FormResourceTest {

    private ObjectMapper objectMapper = ObjectMapperFactory.get();

    @Rule
    public TestResourceStore store = new TestResourceStore();

    private FormResource resource;

    @Before
    public void setUp() throws IOException {

        Provider<AuthenticatedUser> authProvider = Providers.of(new AuthenticatedUser("", 123, "jorden@bdd.com"));
        store.setUser(authProvider.get().getId());

        FormInstance workspace = store.createWorkspace("My workspace");

        Resource formResource = objectMapper.readValue(Resources.getResource("formResourceTest.json"), Resource[].class)[0];
        formResource.setOwnerId(workspace.getId());
        store.create(store.getCurrentUser(), formResource);

        OdkFormFieldBuilderFactory factory = new OdkFormFieldBuilderFactory(
                new InstanceTableProvider(store, authProvider));

        resource = new FormResource(store, authProvider, factory,
                new TestAuthenticationTokenService());
    }

    @Test
    public void getBlankForm() throws JAXBException, URISyntaxException, IOException, InterruptedException {
        Response form = this.resource.form(1);
        File file = new File(targetDir(), "form.xml");
        JAXBContext context = JAXBContext.newInstance(Html.class);
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(form.getEntity(), file);
        validate(file);
    }

    private File targetDir() {
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath + "../../target");
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

    public void validate(File file) throws URISyntaxException, IOException, InterruptedException {


        URL validatorJar = Resources.getResource(FormResourceTest.class, "odk-validate-1.4.3.jar");
        String[] command = {"java", "-jar", Paths.get(validatorJar.toURI()).toString(), file.getAbsolutePath()};

        System.out.println(Joiner.on(' ').join(command));

        ProcessBuilder validator = new ProcessBuilder(command);
        validator.inheritIO();
        int exitCode = validator.start().waitFor();

        if(exitCode != 0) {
            System.out.println("Offending XML: " + Files.toString(file, Charsets.UTF_8));
        }

        assertThat(exitCode, equalTo(0));
    }
}
