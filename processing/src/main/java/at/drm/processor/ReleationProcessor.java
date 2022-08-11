package at.drm.processor;

import at.drm.annotation.Relation;
import at.drm.dao.DynamicRelationDao;
import at.drm.model.DynamicRelationMetaData;
import at.drm.model.DynamicRelationModel;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.tools.Diagnostic;
import lombok.Getter;
import lombok.Setter;

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
        String type = relationAnnotation.type();
        String elementPackage = processingEnv.getElementUtils()
            .getPackageOf(relationElement).getQualifiedName().toString();
        String entityName = type + "Relation";
        return new DynamicRelationMetaData(type, elementPackage, entityName, relationAnnotation);
    }

    private void createDynamicRelationDao(DynamicRelationMetaData entityMetaData) {
        String packageName = entityMetaData.packageName();
        String generatedName = entityMetaData.generatedName();
        ClassName entityClassName = ClassName.get(packageName, generatedName);
        TypeName longTypeName = TypeVariableName.get(Long.class);
        TypeSpec relationDao = TypeSpec.interfaceBuilder(entityMetaData.type() + "RelationDao")
            .addSuperinterface(
                ParameterizedTypeName.get(ClassName.get(DynamicRelationDao.class), entityClassName, longTypeName))
            .build();
        JavaFile javaFileDao = JavaFile.builder(packageName, relationDao)
            .build();
        createJavaClass(javaFileDao);
    }

    private void createDynamicRelationEntity(DynamicRelationMetaData entityMetaData) {
        TypeName typeName = getTypeName(entityMetaData.relationAnnotation());
        String generatedName = entityMetaData.generatedName();
        String type = entityMetaData.type();
        TypeSpec relationEntity = TypeSpec.classBuilder(generatedName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(DynamicRelationModel.class)
            .addAnnotation(Entity.class)
            .addAnnotation(Setter.class)
            .addAnnotation(Getter.class)
            .addAnnotation(createTableAnnotation(type))
            .addField(createIdAnnotation())
            .addField(createSourceObjectField(typeName))
            .addField(createTargetIdField())
            .addField(createTargetTypeField())
            .build();
        String packageName = entityMetaData.packageName();
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

    private TypeName getTypeName(Relation annotation) {
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
