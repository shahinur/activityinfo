package org.activityinfo.server.endpoint.odk;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import freemarker.template.Configuration;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.endpoint.odk.xform.Html;
import org.activityinfo.server.store.ResourceStoreModule;
import org.activityinfo.server.util.locale.LocaleModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@Modules({LocaleModule.class, ResourceStoreModule.class})
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class FormResourceTest extends CommandTestCase2 {

    @Inject
    public FormResource resource;

    @Inject
    public Configuration templateConfig;

    @Test
    public void getBlankForm() throws Exception {
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

    public void validate(File file) throws Exception {


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
