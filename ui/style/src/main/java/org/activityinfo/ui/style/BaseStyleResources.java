package org.activityinfo.ui.style;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.gwt.core.shared.GwtIncompatible;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Stylesheet and collection of resources forming the base stylesheet
 */
@GwtIncompatible
public class BaseStyleResources {

    private String stylesheetStrongName;
    private List<String> resources = Lists.newArrayList();


    public URL getClasspathResourceUrl(String strongName) {
        return Resources.getResource(BaseStyleResources.class, strongName);
    }

    public String getStylesheetStrongName() {
        return stylesheetStrongName;
    }

    public static BaseStyleResources load() throws IOException {
        URL resource = Resources.getResource("org/activityinfo/ui/style/style.manifest");
        List<String> strongNames = Resources.readLines(resource, Charsets.UTF_8);

        BaseStyleResources base = new BaseStyleResources();
        base.stylesheetStrongName = strongNames.get(0);
        base.resources = strongNames;
        return base;
    }

    public void copyTo(File assetDir) throws IOException {
        for(String strongName : resources) {
            ByteSource asset = Resources.asByteSource(Resources.getResource(BaseStyleResources.class, strongName));
            File outputFile = new File(assetDir, strongName);

            System.out.println("Writing " + strongName + " to " + outputFile);
            asset.copyTo(com.google.common.io.Files.asByteSink(outputFile));
        }
    }

}
