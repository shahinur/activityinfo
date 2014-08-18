package org.activityinfo.ui.style.tools.base;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.activityinfo.ui.style.tools.gss.ResourceWriter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

class BundleBuilder implements ResourceWriter {
    private final File outputDir;
    private final List<String> resources = Lists.newArrayList();
    private String stylesheetStrongName;

    /**
     *
     * @param outputDir the classes output directory
     */
    public BundleBuilder(File outputDir) {
        this.outputDir = new File(outputDir, "org.activityinfo.ui.style".replaceAll("\\.", File.separator));
        ensureOutputDirExists();
    }

    private void ensureOutputDirExists() {
        System.out.println("Writing resources to " + outputDir.getAbsolutePath());
        if(!outputDir.exists()) {
            Preconditions.checkState(outputDir.mkdirs());
            System.out.println("Created " + outputDir.getAbsolutePath());
        }
    }

    /**
     * Adds the main stylesheet to the bundle. There should be
     * only one base stylesheet.
     * @param css minified CSS
     */
    public void writeStylesheet(String css) throws IOException {
        String strongName = writeResource(ByteSource.wrap(css.getBytes(Charsets.UTF_8)), "css");
        this.stylesheetStrongName = strongName;
    }

    @Override
    public String writeResource(ByteSource source, String extension) throws IOException {
        String strongName = source.hash(Hashing.md5()).toString() + ".cache." + extension;
        File outputFile = new File(outputDir, strongName);
        if(!outputFile.exists()) {
            source.copyTo(Files.asByteSink(outputFile));
            return strongName;
        }
        resources.add(strongName);
        return strongName;
    }

    public void writeManifest() throws IOException {
        File resourceFile = new File(outputDir, "style.manifest");
        try(PrintWriter writer = new PrintWriter(resourceFile)) {
            writer.println(stylesheetStrongName);
            for(String strongName : resources) {
                writer.println(strongName);
            }
        }
    }
}
