package org.activityinfo.ui.app.client;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.app.client.chrome.HeaderBar;
import org.activityinfo.ui.app.client.chrome.TestPageContext;
import org.activityinfo.ui.app.client.chrome.nav.LeftPanel;
import org.activityinfo.ui.app.client.page.create.MockRemoteStoreService;
import org.activityinfo.ui.style.BaseStyleResources;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.vdom.shared.html.HtmlRenderer;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.section;

public class PreviewRule extends TestWatcher {

    private File tempDir;
    private BaseStyleResources base;
    private File htmlFile;
    private Application application;

    public PreviewRule() {
        application = new Application(new MockRemoteStoreService());
        LocaleProxy.initialize();
    }

    @Override
    protected void starting(Description description) {
        tempDir = Files.createTempDir();
        try {
            base = BaseStyleResources.load();
            base.copyTo(tempDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        htmlFile = new File(tempDir, description.getMethodName() + ".html");
    }

    public void render(VTree mainPage) throws IOException {

        TestPageContext pageContext = new TestPageContext(base);

        VTree tree = Chrome.renderPage(pageContext,
            section(new LeftPanel(application),
                div(BaseStyles.MAINPANEL,
                    new HeaderBar(application),
                    mainPage)));

        HtmlRenderer renderer = new HtmlRenderer();
        tree.accept(renderer);

        Files.write(renderer.getHtml(), htmlFile, Charsets.UTF_8);

        try {
            Desktop.getDesktop().open(htmlFile);
        } catch (IOException e) {
        }

    }

    public Application getApplication() {
        return application;
    }


}
