package at.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.ProcessingEnvironment;

@ExtendWith(MockitoExtension.class)
class ReleationProcessorTest {

    @Mock
    private ProcessingEnvironment processingEnvironment;
    

    @Test
    void init() {
        ReleationProcessor releationProcessor = new ReleationProcessor();

//        Mockito.when(processingEnvironment.getMessager()).then(new M)

        releationProcessor.init(processingEnvironment);
    }

    @Test
    void getSupportedAnnotationTypes() {
    }

    @Test
    void getSupportedSourceVersion() {
    }

    @Test
    void process() {
    }
}