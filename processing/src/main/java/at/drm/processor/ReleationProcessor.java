package at.drm.processor;

import at.drm.annotation.Relation;
import at.drm.dao.DynamicRelationDao;
import at.drm.model.DynamicRelationMetaData;
import at.drm.model.DynamicRelationModel;
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
        processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.NOTE, "getSupportedAnnotationTypes: "
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
            for (Element relationElement : roundEnv.getElementsAnnotatedWith(Relation.class)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found @Relation at " + relationElement);
                DynamicRelationMetaData entityMetaData = createEntityMetaData(relationElement);
                createDynamicRelationEntity(entityMetaData);
                createDynamicRelationDao(entityMetaData);
            }
        }
        return false;
    }

    private DynamicRelationMetaData createEntityMetaData(Element relationElement) {
        Relation relationAnnotation = relationElement.getAnnotation(Relation.class);
        String elementPackage = processingEnv.getElementUtils()
                .getPackageOf(relationElement).getQualifiedName().toString();
        TypeName sourceObjectName = getSourceObjectTypeName(relationAnnotation);
        String sourceObjectWithoutPackages = sourceObjectName.toString().replace(elementPackage + ".", "");
        String generatedEntityName = sourceObjectWithoutPackages + "Relation";
        return new DynamicRelationMetaData(sourceObjectName, elementPackage, generatedEntityName, relationAnnotation);
    }

    private void createDynamicRelationDao(DynamicRelationMetaData entityMetaData) {
        String packageName = entityMetaData.packageName();
        String generatedName = entityMetaData.generatedName();
        ClassName entityClassName = ClassName.get(packageName, generatedName);
        TypeName longTypeName = TypeVariableName.get(Long.class);
        TypeSpec relationDao = TypeSpec.interfaceBuilder(entityMetaData.generatedName().replace("Relation", "RelationDao"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(
                        ParameterizedTypeName.get(ClassName.get(DynamicRelationDao.class), entityClassName, longTypeName))
                .build();
        JavaFile javaFileDao = JavaFile.builder(packageName, relationDao)
                .build();
        createJavaClass(javaFileDao);
    }

    private void createDynamicRelationEntity(DynamicRelationMetaData entityMetaData) {
        String generatedName = entityMetaData.generatedName();
        String packageName = entityMetaData.packageName();
        TypeName sourceObjectName = entityMetaData.sourceObjectName();
        TypeSpec relationEntity = TypeSpec.classBuilder(generatedName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(
                        ParameterizedTypeName.get(ClassName.get(DynamicRelationModel.class), sourceObjectName))
                .addAnnotation(Entity.class)
                .addAnnotation(Setter.class)
                .addAnnotation(Getter.class)
                .addAnnotation(createTableAnnotation(generatedName))
                .addField(createIdAnnotation())
                .addField(createSourceObjectField(sourceObjectName))
                .addField(createTargetIdField())
                .addField(createTargetTypeField())
                .build();
        JavaFile entityJavaFile = JavaFile.builder(packageName, relationEntity)
                .build();
        createJavaClass(entityJavaFile);
    }

    private static FieldSpec createTargetTypeField() {
        return FieldSpec.builder(String.class, "targetType", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", "target_type")
                        .addMember("nullable", "$L", false)
                        .build())
                .build();
    }

    private static FieldSpec createTargetIdField() {
        return FieldSpec.builder(Long.class, "targetId", Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", "target_id")
                        .addMember("nullable", "$L", false)
                        .build())
                .build();
    }

    private TypeName getSourceObjectTypeName(Relation annotation) {
        TypeMirror typeMirror = getSourceClass(annotation);
        assert typeMirror != null;
        return ClassName.get(typeMirror);
    }

    private FieldSpec createSourceObjectField(TypeName typeName) {
        return FieldSpec.builder(typeName, "sourceObject", Modifier.PRIVATE)
                .addAnnotation(ManyToOne.class)
                .addAnnotation(createJoinColumnAnnotaion())
                .build();
    }

    private FieldSpec createIdAnnotation() {
        return FieldSpec.builder(Long.class, "id", Modifier.PRIVATE)
                .addAnnotation(Id.class)
                .addAnnotation(createGeneratedValueAnnotation())
                .build();
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

    private AnnotationSpec createTableAnnotation(String generatedName) {
        return AnnotationSpec.builder(Table.class)
                .addMember("name", "$S", generatedName)
                .addMember("uniqueConstraints", CodeBlock.builder()
                        .add("{ @$T(name = " + "\"unique_$L\", columnNames = " +
                                        "{ \"target_id\", \"target_type\",\"source_object\" })}",
                                UniqueConstraint.class, generatedName)
                        .build())
                .build();
    }

    private void createJavaClass(JavaFile javaFile) {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "ERROR ON write file: " +
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
