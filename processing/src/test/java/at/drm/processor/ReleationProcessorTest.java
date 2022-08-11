package at.drm.processor;

import at.drm.annotation.Relation;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileObject;
import java.util.Set;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

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
    }

}