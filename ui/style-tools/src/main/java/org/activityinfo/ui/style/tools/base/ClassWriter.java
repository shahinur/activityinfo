package org.activityinfo.ui.style.tools.base;

import com.google.common.base.Preconditions;
import com.google.gwt.core.ext.Generator;
import org.activityinfo.ui.vdom.shared.html.CssClass;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
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
        writer.println("import " + CssClass.class.getName() + ";");
        writer.println("import " + Icon.class.getName() + ";");

        writer.println();
    }

    public void declareClass() {
        writer.println("public final class " + className + " {");
    }

    public void declareSingleton() {
        writer.println("    public static final " + className + " STYLE = new " + className + "();");
        writer.println("    private " + className + "() { }");
    }


    public void declareFinalClass() {
        writer.println("public final class " + className + " {");
        writer.println("    private " + className + "() {}");
    }

    public void writeAccessor(String methodName, String classNames) {
        writer.println("    public String " + methodName + "() { return \"" + Generator.escape(classNames) + "\"; }");
    }

    public void writeConstant(String enumValue, Class wrapperClass, String className) {
        writer.println("   public static final " +
                       wrapperClass.getSimpleName() + " " + enumValue + " = " +
                       wrapperClass.getSimpleName() + ".valueOf(\"" +
                            Generator.escape(className) + " \");");
    }

    public void close() {
        writer.println("}");
        writer.close();
    }

}
