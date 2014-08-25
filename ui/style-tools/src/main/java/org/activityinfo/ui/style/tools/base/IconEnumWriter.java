package org.activityinfo.ui.style.tools.base;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.activityinfo.ui.style.tools.rebind.ClassNames;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates sources for the names of the icon fonts we're using.
 *
 */
public class IconEnumWriter {

    public static final String PACKAGE_NAME = "org.activityinfo.ui.style.icons";
    private final File baseDir;

    public IconEnumWriter(File baseDir) {
        this.baseDir = baseDir;
    }

    private void generateFontAwesome() throws IOException {
        try(ClassWriter classWriter = new ClassWriter(baseDir, PACKAGE_NAME, "FontAwesome")) {

            classWriter.declareFinalClass();
            for (String iconName : readFontAwesomeIcons()) {
                classWriter.writeConstant(ClassNames.hyphenatedToEnumStyle(iconName),
                        Icon.class, "fa fa-" + iconName);
            }
            classWriter.close();
        }
    }

    private void generateGlyphIcons() throws IOException {
        try(ClassWriter classWriter = new ClassWriter(baseDir, PACKAGE_NAME, "GlyphIcons")) {

            classWriter.declareFinalClass();
            for (String iconName : readGlyphIcons()) {
                classWriter.writeConstant(ClassNames.hyphenatedToEnumStyle(iconName),
                        Icon.class, "glyphicon glyphicon-" + iconName);
            }
            classWriter.close();
        }
    }

    private List<String> readFontAwesomeIcons() throws IOException {
        // Icons are declared here as so:
        // @fa-var-bomb: "\f1e2";
        return extractIconNames("fontawesome/variables.less", "^@fa-var-([\\w\\-]+):");
    }

    private List<String> readGlyphIcons() throws IOException {
        // Icons are declared here as so:
        // .glyphicon-asterisk
        return extractIconNames("bootstrap/glyphicons.less", "^\\.glyphicon\\-([\\w\\-]+)");
    }


    private List<String> extractIconNames(String sourceFile, String regex) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        File file = new File(baseDir + File.separator + "src" + File.separator +
                             "main" + File.separator + "less" + File.separator + sourceFile);
        List<String> lines = Files.readLines(file, Charsets.UTF_8);
        Set<String> classNames = Sets.newHashSet();
        for(String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if(matcher.find()) {
                classNames.add(matcher.group(1));
            }
        }
        List<String> list = Lists.newArrayList(classNames);
        Collections.sort(list);
        return list;
    }

    public void write() throws IOException {
        generateFontAwesome();
        generateGlyphIcons();
    }
}
