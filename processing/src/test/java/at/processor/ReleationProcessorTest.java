package at.processor;

import at.annotation.Relation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReleationProcessorTest {

    @Mock
    private ProcessingEnvironment processingEnvironment;

    @Mock
    private RoundEnvironment roundEnvironment;

    @Mock
    private Messager messager;

    ReleationProcessor releationProcessorUnderTest = new ReleationProcessor();


    @BeforeEach
    void beforeEach() {
        Mockito.when(processingEnvironment.getMessager()).thenReturn(messager);
    }

    @Test
    void init() {
        releationProcessorUnderTest.init(processingEnvironment);
    }

    @Test
    void getSupportedAnnotationTypes() {
        releationProcessorUnderTest.init(processingEnvironment);
        Set<String> supportedAnnotationTypes = releationProcessorUnderTest.getSupportedAnnotationTypes();
        assertThat(supportedAnnotationTypes).contains(Relation.class.getCanonicalName());
    }

    @Test
    void getSupportedSourceVersion() {
        releationProcessorUnderTest.init(processingEnvironment);
        SourceVersion supportedSourceVersion = releationProcessorUnderTest.getSupportedSourceVersion();
        assertThat(supportedSourceVersion).isEqualTo(SourceVersion.RELEASE_17);
    }

    @Test
    void process() {
        releationProcessorUnderTest.init(processingEnvironment);
        Mockito.when(roundEnvironment.processingOver()).thenReturn(false);
        Set<? extends TypeElement> typeElements = null;
        //TODO add usefull Annotation Processing Tests
        releationProcessorUnderTest.process(typeElements, roundEnvironment);
    }
}