package at.drm.processor;

import at.drm.annotation.IgnoreRelation;
import at.drm.annotation.Relation;
import at.drm.dao.RelationDao;
import at.drm.model.RelationLink;
import at.drm.model.RelationMetaData;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RelationProcessor extends AbstractProcessor {

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
        return SourceVersion.RELEASE_21;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "process");
            for (Element relationElement : roundEnv.getElementsAnnotatedWith(Relation.class)) {
                if (relationElement.getAnnotation(IgnoreRelation.class) != null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                            "Skipping @Relation at " + relationElement);
                    continue;
                }

                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found @Relation at " + relationElement);
                RelationMetaData entityMetaData = createEntityMetaData(relationElement);
                createDynamicRelationEntity(entityMetaData);
                createDynamicRelationDao(entityMetaData);
            }
        }
        return false;
    }

    private RelationMetaData createEntityMetaData(Element relationElement) {
        Relation relationAnnotation = relationElement.getAnnotation(Relation.class);
        String elementPackage = processingEnv.getElementUtils()
            .getPackageOf(relationElement).getQualifiedName().toString();
        TypeName sourceObjectName = ClassName.get(relationElement.asType());
        String sourceObjectWithoutPackages = sourceObjectName.toString().replace(elementPackage + ".", "");
        String generatedEntityName = sourceObjectWithoutPackages + "Relation";
        return new RelationMetaData(sourceObjectName, elementPackage, generatedEntityName, relationAnnotation);
    }

    private void createDynamicRelationDao(RelationMetaData entityMetaData) {
        String packageName = entityMetaData.packageName();
        String generatedName = entityMetaData.generatedName();
        ClassName entityClassName = ClassName.get(packageName, generatedName);
        TypeName longTypeName = TypeVariableName.get(Long.class);
        TypeSpec relationDao = TypeSpec.interfaceBuilder(
                entityMetaData.generatedName().replace("Relation", "RelationDao"))
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(
                ParameterizedTypeName.get(ClassName.get(RelationDao.class), entityClassName, longTypeName))
            .build();
        JavaFile javaFileDao = JavaFile.builder(packageName, relationDao)
            .build();
        createJavaClass(javaFileDao);
    }

    private void createDynamicRelationEntity(RelationMetaData entityMetaData) {
        String generatedName = entityMetaData.generatedName();
        String packageName = entityMetaData.packageName();
        TypeName sourceObjectName = entityMetaData.sourceObjectName();
        TypeSpec relationEntity = TypeSpec.classBuilder(generatedName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(
                ParameterizedTypeName.get(ClassName.get(RelationLink.class), sourceObjectName))
            .addAnnotation(Entity.class)
            .addAnnotation(createTableAnnotation(generatedName))
            .addField(createIdAnnotation())
            .addField(createSourceObjectField(sourceObjectName))
            .addField(createTargetIdField())
            .addField(createTargetTypeField())
            // expliziter Konstruktor
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build())
            // Getter für die Felder
            .addMethod(MethodSpec.methodBuilder("getId")
                .addModifiers(Modifier.PUBLIC)
                .returns(Long.class)
                .addStatement("return this.id")
                .build())
            .addMethod(MethodSpec.methodBuilder("getSourceObject")
                .addModifiers(Modifier.PUBLIC)
                .returns(sourceObjectName)
                .addStatement("return this.sourceObject")
                .build())
            .addMethod(MethodSpec.methodBuilder("getTargetId")
                .addModifiers(Modifier.PUBLIC)
                .returns(Long.class)
                .addStatement("return this.targetId")
                .build())
            .addMethod(MethodSpec.methodBuilder("getTargetType")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return this.targetType")
                .build())
            // Setter für die Felder
            .addMethod(MethodSpec.methodBuilder("setId")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Long.class, "id")
                .addStatement("this.id = id")
                .build())
            .addMethod(MethodSpec.methodBuilder("setSourceObject")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(sourceObjectName, "sourceObject")
                .addStatement("this.sourceObject = sourceObject")
                .build())
            .addMethod(MethodSpec.methodBuilder("setTargetId")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Long.class, "targetId")
                .addStatement("this.targetId = targetId")
                .build())
            .addMethod(MethodSpec.methodBuilder("setTargetType")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "targetType")
                .addStatement("this.targetType = targetType")
                .build())
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
}
