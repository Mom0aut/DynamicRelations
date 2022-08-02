package processor;

import annotation.Relation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.persistence.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@AutoService(Processor.class)
public class ReleationProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "init");
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Relation.class.getCanonicalName());
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "getSupportedAnnotationTypes: "
                + annotations);
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "getSupportedSourceVersion");
        return SourceVersion.RELEASE_17;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "process");
            for (Element relationAnnotation : roundEnv.getElementsAnnotatedWith(Relation.class)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found @Relation at " + relationAnnotation);
                Relation annotation = relationAnnotation.getAnnotation(Relation.class);
                String type = annotation.type();
                String elementPackage = processingEnv.getElementUtils().getPackageOf(relationAnnotation).
                        getQualifiedName().toString();
                TypeSpec relation = TypeSpec.classBuilder(type + "Relation")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addAnnotation(Entity.class)
                        .addAnnotation(createTableAnnotation(type))
                        .addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE)
                                .addAnnotation(Id.class)
                                .addAnnotation(createGeneratedValueAnnotation())
                                .build())
                        .build();
                JavaFile javaFile = JavaFile.builder(elementPackage, relation)
                        .build();
                createJavaClass(javaFile);
            }
        }
        return false;
    }

    private static AnnotationSpec createGeneratedValueAnnotation() {
        return AnnotationSpec.builder(GeneratedValue.class)
                .addMember("strategy", "$T.$L", GenerationType.class, GenerationType.IDENTITY.name())
                .build();
    }

    private static AnnotationSpec createTableAnnotation(String type) {
        return AnnotationSpec.builder(Table.class)
                .addMember("name", "$S", type + "Relation")
                .build();
    }

    private void createJavaClass(JavaFile javaFile) {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR ON write file: " +
                    e.getMessage());
        }
    }
}
