package at.drm.processor;

import at.drm.annotation.Relation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
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
                TypeMirror typeMirror = getSourceClass(annotation);
                assert typeMirror != null;
                TypeName typeName = ClassName.get(typeMirror);

                String elementPackage = processingEnv.getElementUtils().getPackageOf(relationAnnotation).
                        getQualifiedName().toString();
                TypeSpec relation = TypeSpec.classBuilder(type + "Relation")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addAnnotation(Entity.class)
                        .addAnnotation(Setter.class)
                        .addAnnotation(Getter.class)
                        .addAnnotation(createTableAnnotation(type))
                        .addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE)
                                .addAnnotation(Id.class)
                                .addAnnotation(createGeneratedValueAnnotation())
                                .build())
                        .addField(FieldSpec.builder(typeName, "sourceObject", Modifier.PRIVATE)
                                .addAnnotation(ManyToOne.class)
                                .addAnnotation(createJoinColumnAnnotaion())
                                .build())
                        .addField(FieldSpec.builder(Long.class, "targetId", Modifier.PRIVATE)
                                .addAnnotation(AnnotationSpec.builder(Column.class)
                                        .addMember("name", "$S", "target_id")
                                        .addMember("nullable", "$L", false)
                                        .build())
                                .build())
                        .addField(FieldSpec.builder(String.class, "targetType", Modifier.PRIVATE)
                                .addAnnotation(AnnotationSpec.builder(Column.class)
                                        .addMember("name", "$S", "target_type")
                                        .addMember("nullable", "$L", false)
                                        .build())
                                .build())
                        .build();
                JavaFile javaFile = JavaFile.builder(elementPackage, relation)
                        .build();
                createJavaClass(javaFile);
            }
        }
        return false;
    }

    private AnnotationSpec createJoinColumnAnnotaion() {
        return AnnotationSpec.builder(JoinColumn.class)
                .addMember("name", "$S", "source_object")
                .build();
    }

    private AnnotationSpec createGeneratedValueAnnotation() {
        return AnnotationSpec.builder(GeneratedValue.class)
                .addMember("strategy", "$T.$L", GenerationType.class, GenerationType.IDENTITY.name())
                .build();
    }

    private AnnotationSpec createTableAnnotation(String type) {
        return AnnotationSpec.builder(Table.class)
                .addMember("name", "$S", type + "Relation")
                .addMember("uniqueConstraints", CodeBlock.builder()
                        .add("{ @$T(name = " + "\"unique_relation_$L\", columnNames = " +
                                        "{ \"target_id\", \"target_type\",\"source_object\" })}",
                                UniqueConstraint.class, type)
                        .build())
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


    //TODO refactor with the more right way see:
    // https://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
    private TypeMirror getSourceClass(Relation annotation) {
        try {
            annotation.sourceClass(); // this should throw
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }
}
