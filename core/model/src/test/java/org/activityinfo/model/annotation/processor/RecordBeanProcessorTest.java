package org.activityinfo.model.annotation.processor;

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static org.truth0.Truth.ASSERT;

public class RecordBeanProcessorTest {

    @Test
    public void testCompilation() throws IOException {

        JavaFileObject javaFileObject = JavaFileObjects.forSourceLines(
            "foo.bar.Baz",
            "package foo.bar;",
            "",
            "import java.util.ArrayList;",
            "import java.util.List;",
            "import org.activityinfo.model.annotation.RecordBean;",
            "",
            "@RecordBean(classId=\"_baz\")",
            "public class Baz {",
            "  private String label;",
            "  private boolean visible;",
            "  private List<Baz> children = new ArrayList<Baz>();",
            "",
            "  public String getLabel() { return label; }",
            "  public void setLabel(String label) { this.label = label; }",
            "  public boolean isVisible() { return visible; }",
            "  public void setVisible(boolean visible) { this.visible = visible; }",
            "  public List<Baz> getChildren() { return this.children; }",
            "",
            "}");

        ASSERT.about(JavaSourceSubjectFactory.javaSource())
            .that(javaFileObject)
            .processedWith(new RecordBeanProcessor())
            .compilesWithoutError();
    }
}