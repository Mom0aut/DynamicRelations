package at.drm.processor;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

import at.drm.annotation.Relation;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReleationProcessorTest {

    @Mock
    private ProcessingEnvironment processingEnvironment;

    @Mock
    private Messager messager;

    ReleationProcessor releationProcessorUnderTest = new ReleationProcessor();

    @Test
    void init() {
        Mockito.when(processingEnvironment.getMessager()).thenReturn(messager);
        releationProcessorUnderTest.init(processingEnvironment);
    }


    @Test
    void getSupportedAnnotationTypes() {
        Mockito.when(processingEnvironment.getMessager()).thenReturn(messager);
        releationProcessorUnderTest.init(processingEnvironment);
        Set<String> supportedAnnotationTypes = releationProcessorUnderTest.getSupportedAnnotationTypes();
        assertThat(supportedAnnotationTypes).contains(Relation.class.getCanonicalName());
    }

    @Test
    void getSupportedSourceVersion() {
        Mockito.when(processingEnvironment.getMessager()).thenReturn(messager);
        releationProcessorUnderTest.init(processingEnvironment);
        SourceVersion supportedSourceVersion = releationProcessorUnderTest.getSupportedSourceVersion();
        assertThat(supportedSourceVersion).isEqualTo(SourceVersion.RELEASE_17);
    }

    @Test
    void process() {
        ReleationProcessor releationProcessor = new ReleationProcessor();
        Compilation compilation = javac()
            .withProcessors(releationProcessor)
            .compile(JavaFileObjects.forResource("TestFile.java"));
        CompilationSubject.assertThat(compilation).succeeded();
        ImmutableList<JavaFileObject> generatedFiles = compilation.generatedFiles();
        assertThat(generatedFiles).isNotEmpty();

        RelationClassMethods methods = generatedFiles.stream()
            .filter(file -> file.getName().endsWith("Relation.java"))
            .map(this::extractMethodsFromFile)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Keine generierte Relation-Klasse gefunden!"));

        assertThat(methods.hasGetterId).isTrue();
        assertThat(methods.hasSetterId).isTrue();
        assertThat(methods.hasGetterSourceObject).isTrue();
        assertThat(methods.hasSetterSourceObject).isTrue();
        assertThat(methods.hasGetterTargetId).isTrue();
        assertThat(methods.hasSetterTargetId).isTrue();
        assertThat(methods.hasGetterTargetType).isTrue();
        assertThat(methods.hasSetterTargetType).isTrue();
        assertThat(methods.hasConstructor).isTrue();
    }

    @SneakyThrows
    private RelationClassMethods extractMethodsFromFile(javax.tools.JavaFileObject file) {
        RelationClassMethods result = new RelationClassMethods();
        InputStream is = file.openInputStream();
        Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
        String content = scanner.hasNext() ? scanner.next() : "";
        result.hasGetterId = content.contains("public Long getId()");
        result.hasSetterId = content.contains("public void setId(Long id)");
        result.hasGetterSourceObject = content.contains("public ") && content.contains(" getSourceObject()");
        result.hasSetterSourceObject = content.contains("public void setSourceObject");
        result.hasGetterTargetId = content.contains("public Long getTargetId()");
        result.hasSetterTargetId = content.contains("public void setTargetId(Long targetId)");
        result.hasGetterTargetType = content.contains("public String getTargetType()");
        result.hasSetterTargetType = content.contains("public void setTargetType(String targetType)");
        result.hasConstructor = content.contains("public ") && content.contains("() {");
        return result;
    }

    private static class RelationClassMethods {

        boolean hasGetterId = false;
        boolean hasSetterId = false;
        boolean hasGetterSourceObject = false;
        boolean hasSetterSourceObject = false;
        boolean hasGetterTargetId = false;
        boolean hasSetterTargetId = false;
        boolean hasGetterTargetType = false;
        boolean hasSetterTargetType = false;
        boolean hasConstructor = false;
    }
}