package org.activityinfo.ui.style.tools.base;

import com.google.common.base.Preconditions;
import com.google.gwt.core.ext.Generator;
import org.activityinfo.ui.style.tools.rebind.ClassNames;
import org.activityinfo.ui.vdom.shared.html.HasClassNames;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class ClassWriter implements AutoCloseable {

    private final PrintWriter writer;
    private String className;

    public ClassWriter(File baseDir, String packageName, String className) throws IOException {
        this.className = className;

        File outputDir = new File(baseDir.getCanonicalPath() + File.separator +
                            "target" + File.separator +
                            "generated-sources" + File.separator +
                            "style" + File.separator +
                            packageName.replaceAll("\\.", File.separator));

        if(!outputDir.exists()) {
            Preconditions.checkState(outputDir.mkdirs());
        }

        writer = new PrintWriter(new FileWriter(new File(outputDir, className + ".java")));
        writer.println("package " + packageName + ";");
        writer.println();
        writer.println("import " + VNode.class.getName() + ";");
        writer.println("import " + VTree.class.getName() + ";");
        writer.println("import " + PropMap.class.getName() + ";");
        writer.println("import " + HtmlTag.class.getName() + ";");

        writer.println();
    }

    public void declareClass() {
        writer.println("public final class " + className + " {");
    }

    public void declareSingleton() {
        writer.println("    public static final " + className + " STYLE = new " + className + "();");
        writer.println("    private " + className + "() { }");
    }

    public void declareEnum(Class<? extends HasClassNames> implementsInterface) {
        writer.println("public enum " + className + " implements " +
                       implementsInterface.getName() + " {");
    }

    public void writeAccessor(String methodName, String classNames) {
        writer.println("    public String " + methodName + "() { return \"" + Generator.escape(classNames) + "\"; }");
    }

    public void writeAccessor(String className) {
        Preconditions.checkArgument(className.indexOf(' ') == -1);

        writeAccessor(ClassNames.toCamelCase(className), className);
    }

    public void writeEnumValue(String enumValue, String className) {
        writer.println("   " + enumValue + "(\"" + Generator.escape(className) + "\"),");
    }

    public void writeEnumGetClassNameImpl() {
        writer.println("    ;");
        writer.println();
        writer.println("    private final String classNames;");
        writer.println("    private " + className + "(String classNames) { this.classNames = classNames; }");
        writer.println("    @Override");
        writer.println("    public final String getClassNames() { return classNames; }");
    }

    public void writeIconRender() {
        writer.println("    public final VNode render() { return new VNode(HtmlTag.SPAN, PropMap.withClasses(classNames)); }");
    }

    public void writeDivBuilder() {
        writer.println("    public final VNode div(VTree... children) { " +
                       "return new VNode(HtmlTag.DIV, PropMap.withClasses(classNames), children); }");
    }

    public void close() {
        writer.println("}");
        writer.close();
    }
}
