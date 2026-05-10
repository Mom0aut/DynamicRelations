package at.test.drm;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertThat;

class RelationProcessorTest {

    @Test
    void shouldGenerateCodeForRelationAnnotation() {

        // 1. Fake input class
        JavaFileObject source = JavaFileObjects.forSourceString(
                "test.User",
                """
                package test;

                import at.drm.annotation.Relation;

                @Relation
                public class User {
                }
                """
        );

        // 2. Run processor
        Compilation compilation = Compiler.javac()
                .withProcessors(new at.drm.processor.RelationProcessor())
                .compile(source);

        // 3. Assert compilation success
        assertThat(compilation.status())
                .isEqualTo(Compilation.Status.SUCCESS);

        // 4. Assert something was generated
        assertThat(compilation.generatedSourceFiles())
                .isNotEmpty();

        // 5. Get generated file content
        String generated = String.valueOf(CompilationSubject.assertThat(compilation)
                .generatedSourceFile("test.UserRelation")
                .contentsAsUtf8String());

        // 6. Strong assertions
        assertThat(generated).contains("package test;");
        assertThat(generated).contains("class UserRelation");
    }


    @Test
    void shouldNotGenerateWhenAnnotationMissing() {

        // 1. Input WITHOUT @Relation
        JavaFileObject source = JavaFileObjects.forSourceString(
                "test.User",
                """
                package test;

                public class User {
                }
                """
        );

        // 2. Run processor
        Compilation compilation = Compiler.javac()
                .withProcessors(new at.drm.processor.RelationProcessor())
                .compile(source);

        // 3. Should still compile successfully
        assertThat(compilation.status())
                .isEqualTo(Compilation.Status.SUCCESS);

        // 4. But should NOT generate any files
        assertThat(compilation.generatedSourceFiles())
                .isEmpty();
    }
}