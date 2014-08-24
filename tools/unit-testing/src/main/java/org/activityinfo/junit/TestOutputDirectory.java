package org.activityinfo.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;

/**
 * JUnit Rule which creates an output directory within the target/ folder
 * of the build.
 */
public class TestOutputDirectory extends TestWatcher {

    private File outputDir;

    @Override
    protected void starting(Description description) {
        File targetDir = targetDir(description.getTestClass());
        File testClassOutputDir = new File(targetDir, description.getClassName());
        File methodOutputDir = new File(testClassOutputDir, description.getMethodName());
        createDirectory(methodOutputDir);

        try {
            outputDir = methodOutputDir.getCanonicalFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File targetDir(Class testClass) {
        String relPath = testClass.getProtectionDomain().getCodeSource().getLocation().getFile();
        return new File(relPath + "../../target");
    }

    private void createDirectory(File dir) {
        if(!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create directory at " + dir.getAbsolutePath());
            }
        }
    }

    public File subDir(String name) {
        File subDir = new File(outputDir, name);
        createDirectory(subDir);
        return subDir;
    }
}

