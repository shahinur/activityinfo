package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.store.test.TestResourceStore;
import org.activityinfo.ui.app.client.request.TestRemoteStoreService;
import org.activityinfo.ui.style.BaseStyleResources;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MainWrapperTest {

    private BaseStyleResources base;
    private File tempDir;
    private TestResourceStore store;
    private TestRemoteStoreService service;

    @Before
    public void setUp() throws IOException {
        tempDir = new File(targetDir(), "html").getCanonicalFile();
        tempDir.mkdirs();
        base = BaseStyleResources.load();
        base.copyTo(tempDir);

        store = new TestResourceStore().load("test.json");
        service = new TestRemoteStoreService(store);
    }

    @Test
    public void test() throws IOException {
//
//        PageContext pageContext = new
//        pageContext.setStylesheetUrl(base.getStylesheetStrongName());
//        pageContext.setApplicationTitle("ActivityInfo");
//
//
//        AppState state = new AppState(service);
//
//        VTree tree = Chrome.renderPage(pageContext, Chrome.mainWrapper(state));
//
//        HtmlRenderer renderer = new HtmlRenderer();
//        tree.accept(renderer);
//
//        File indexFile = new File(tempDir, "index.html");
//        Files.write(renderer.getHtml(), indexFile, Charsets.UTF_8);
//
//        System.out.println("Navigate to file://" + indexFile.getCanonicalFile());
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