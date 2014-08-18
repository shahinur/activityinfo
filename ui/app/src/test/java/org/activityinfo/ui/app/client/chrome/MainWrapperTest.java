package org.activityinfo.ui.app.client.chrome;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gwt.dom.client.Style;
import org.activityinfo.ui.style.BaseStyleResources;
import org.activityinfo.ui.vdom.shared.html.HtmlRenderer;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class MainWrapperTest {

    private BaseStyleResources base;
    private File tempDir;

    @Before
    public void setUp() throws IOException {
        tempDir = new File(targetDir(), "html").getCanonicalFile();
        tempDir.mkdirs();
        base = BaseStyleResources.load();
        base.copyTo(tempDir);
    }

    @Test
    public void test() throws IOException {


        VTree tree =
        html(
            head(
                Meta.charset(Charsets.UTF_8),
                Meta.viewport("width=device-width, initial-scale=1.0, maximum-scale=1.0"),
                title("ActivityInfo 3.0 Beta"),
                Link.stylesheet(base.getStylesheetStrongName())
            ),
            body(style().overflow(Style.Overflow.VISIBLE),
                Chrome.mainWrapper()
            )
        );

        HtmlRenderer renderer = new HtmlRenderer();
        tree.accept(renderer);

        File indexFile = new File(tempDir, "index.html");
        Files.write(renderer.getHtml(), indexFile, Charsets.UTF_8);

        System.out.println("Navigate to file://" + indexFile.getCanonicalFile());
    }

    public File targetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath+"../../target");
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}