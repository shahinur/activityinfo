package org.activityinfo.model.annotation.processor;

import org.activityinfo.model.annotation.Form;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("org.activityinfo.model.annotation.Form")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class FormClassProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        ModelBuilder modelBuilder = new ModelBuilder(processingEnv, roundEnv);

        for (Element elem : roundEnv.getElementsAnnotatedWith(Form.class)) {

            try {
                FormModel form = modelBuilder.buildFormModel((TypeElement)elem);

            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Exception: " + e.getMessage());
            }
        }
        return true; // no further processing of this annotation type
    }

    private void generateImpl(Element e) throws IOException {
        TypeElement classElement = (TypeElement) e;
        PackageElement packageElement =
            (PackageElement) classElement.getEnclosingElement();

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
            classElement.getQualifiedName() + "BeanInfo");

        BufferedWriter bw = new BufferedWriter(jfo.openWriter());
        bw.append("package ");
        bw.append(packageElement.getQualifiedName());
        bw.append(";");
        bw.newLine();
        bw.newLine();
        bw.append("public class " + classElement.getSimpleName() + "BeanInfo { }\n");
        bw.close();
    }
}
