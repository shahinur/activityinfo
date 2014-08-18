package org.activityinfo.ui.style.tools.base;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.activityinfo.ui.style.tools.gss.GssCompiler;
import org.activityinfo.ui.style.tools.gss.GssTree;
import org.activityinfo.ui.style.tools.rebind.ClassNames;
import org.activityinfo.ui.style.tools.rebind.SourceResolver;
import org.activityinfo.ui.vdom.shared.html.HasClassNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Compiles and strongly-names the site's base stylesheet
 */
public class BaseStylesheetCompiler {

    private final File baseDir;
    private final BundleBuilder bundle;

    private TreeLogger logger = new CliTreeLogger(TreeLogger.Type.DEBUG);

    private File lessOutput;
    private GssTree cssTree;

    public static void main(String[] args) throws Exception {
        new BaseStylesheetCompiler(args[0]).compile();
    }

    public BaseStylesheetCompiler(String arg) throws FileNotFoundException {
        baseDir = new File(arg);
        File targetDir = new File(baseDir, "target");
        targetDir.mkdirs();
        bundle = new BundleBuilder(new File(targetDir, "classes"));
        lessOutput = new File(targetDir, "base.css");
    }

    public void compile() throws Exception {
        compileLess();
        optimize();
        emitResources();
        emitStylesheet();
        generateAccessorSources();

        // Writes a mapping to strong names that can be
        // loaded at runtime
        bundle.writeManifest();

        // Write icon font sources
        new IconEnumWriter(baseDir).write();
    }

    public void compileLess() throws IOException, LessException {

        File lessInput = new File(baseDir, "src/main/less/base.less");
        if (!lessInput.exists()) {
            throw new FileNotFoundException(lessInput.getAbsolutePath());
        }

        logger.log(TreeLogger.Type.INFO, "Compiling " + lessInput.getAbsoluteFile().getCanonicalFile());

        LessEngine engine = new LessEngine();
        engine.compile(lessInput, lessOutput);
    }


    private void generateAccessorSources() throws IOException {
        List<String> classNames = Lists.newArrayList(cssTree.getClassNames());
        Collections.sort(classNames);

        ClassWriter accessorWriter = new ClassWriter(baseDir, "org.activityinfo.ui.style", "BaseStyles");
        accessorWriter.declareEnum(HasClassNames.class);

        for(String className : classNames) {
            if(!className.startsWith("glyphicon") &&
               !className.startsWith("fa")) {

                accessorWriter.writeEnumValue(ClassNames.hyphenatedToEnumStyle(className), className);
            }
        }
        accessorWriter.writeEnumGetClassNameImpl();
        accessorWriter.writeDivBuilder();
        accessorWriter.close();
    }

    private void optimize() throws IOException, UnableToCompleteException {
        GssCompiler compiler = new GssCompiler();
        cssTree = compiler.compile(logger, Files.toString(lessOutput, Charsets.UTF_8));
    }

    private void emitResources() throws UnableToCompleteException {
        SourceResolver resolver = new BaseSourceResolver();
        cssTree.emitResources(logger, resolver, bundle);
    }

    private void emitStylesheet() throws IOException {
        cssTree.simplifyCSS();
        bundle.writeStylesheet(cssTree.toCompactCSS());
    }

    private class BaseSourceResolver implements SourceResolver {
        private final List<File> sourceDirs = Lists.newArrayList();

        private BaseSourceResolver() {
            sourceDirs.add(new File(baseDir, "src/main/font"));
        }

        private File resolveFile(String relativePath) throws IOException {
            List<File> attempts = Lists.newArrayList();
            for(File sourceDir : sourceDirs) {
                File sourceFile = new File(sourceDir, relativePath);
                if(sourceFile.exists()) {
                    return sourceFile;
                } else {
                    attempts.add(sourceFile.getCanonicalFile());
                }
            }
            throw new RuntimeException("Could not find resource [" + relativePath + "], " +
                                            "tried:\n" + Joiner.on("\n").join(attempts));
        }

        @Override
        public String resolveSourceText(TreeLogger logger, String relativePath) throws UnableToCompleteException {
            return new String(resolveByteArray(logger, relativePath), Charsets.UTF_8);
        }

        @Override
        public byte[] resolveByteArray(TreeLogger logger, String path) throws UnableToCompleteException {
            try {
                return Files.asByteSource(resolveFile(path)).read();
            } catch (IOException e) {
                logger.log(TreeLogger.Type.ERROR, e.getMessage(), e);
                throw new UnableToCompleteException();
            }
        }
    }

}
