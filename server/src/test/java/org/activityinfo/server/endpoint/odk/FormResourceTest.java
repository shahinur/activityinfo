package org.activityinfo.server.endpoint.odk;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.util.Providers;
import com.sun.jersey.api.view.Viewable;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.server.branding.ScaffoldingDirective;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.util.jaxrs.FreemarkerViewProcessor;
import org.activityinfo.server.util.locale.LocaleModule;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@Modules(LocaleModule.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class FormResourceTest extends CommandTestCase2 {

    @Inject
    public FormResource resource;

    @Inject
    public Configuration templateConfig;

    @Test
    public void getBlankForm() throws Exception {
        Response form = this.resource.form(1);
        Viewable viewable = (Viewable) form.getEntity();

        ScaffoldingDirective scaffoldingDirective = EasyMock.createMock(ScaffoldingDirective.class);
        EasyMock.replay(scaffoldingDirective);

        FreemarkerViewProcessor processor = new FreemarkerViewProcessor(templateConfig, Providers.of(Locale.ENGLISH),
                scaffoldingDirective);
        Template template = processor.resolve(viewable.getTemplateName());

        File file = new File(targetDir(), "form.xml");
        try (OutputStream out = new FileOutputStream(file)) {
            processor.writeTo(template, viewable, out);
        }

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
        String[] command = {"java", "-jar", validatorJar.getFile(), file.getAbsolutePath()};

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
