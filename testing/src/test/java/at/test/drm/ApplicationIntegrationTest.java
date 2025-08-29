package at.test.drm;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import at.drm.EnableDynamicRelation;
import at.drm.model.RelationLink;
import at.drm.service.DynamicRelationsPrintService;
import at.drm.service.RelationService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;

@SpringBootTest
@ActiveProfiles("integration")
@EnableDynamicRelation
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY)
class ApplicationIntegrationTest {

    @Autowired
    private PersonEntityDao dao;
    @Autowired
    private DogEntityDao dao2;
    @Autowired
    private DocumentEntityDao dao3;
    @Autowired
    private RelationService relationService;
    @Autowired
    private DynamicRelationsPrintService dynamicRelationsPrintService;

    @Test
    void shouldFindRelationBySourceObject() {
        var first = new PersonEntity();
        var second = new DogEntity();
        var third = new DocumentEntity();
        dao.save(first);
        dao2.save(second);
        dao3.save(third);

        relationService.createRelation(first, second);
        relationService.createRelation(first, third);
        final List<RelationLink> relationBySourceObject =
            relationService.findRelationBySourceObject(first);

        Assertions.assertThat(relationBySourceObject).isNotNull();
        Assertions.assertThat(relationBySourceObject.size()).isEqualTo(2);
        Assertions.assertThat(relationBySourceObject.get(0).getTargetType())
            .isEqualTo(second.getType());
        Assertions.assertThat(relationBySourceObject.get(1).getTargetType())
            .isEqualTo(third.getType());
    }

    @Test
    void shouldFindRelationByTarget() {
        var first = new PersonEntity();
        var second = new DogEntity();
        var third = new DocumentEntity();
        dao.save(first);
        dao2.save(second);
        dao3.save(third);

        relationService.createRelation(first, second);
        relationService.createRelation(first, third);
        relationService.createRelation(second, third);
        final Set<RelationLink> byTarget =
            relationService.findRelationByTargetRelationIdentity(third);

        Assertions.assertThat(byTarget).isNotNull();
        Assertions.assertThat(byTarget.size()).isEqualTo(2);
        Assertions.assertThat(byTarget.stream().anyMatch(r -> r.getSourceObject().equals(first)))
            .isTrue();
        Assertions.assertThat(byTarget.stream().anyMatch(r -> r.getSourceObject().equals(second)))
            .isTrue();
    }

    @Test
    void shouldFindRelationBySourceObjectAndIdentity() {
        var first = new PersonEntity();
        var second = new DogEntity();
        var third = new DocumentEntity();
        dao.save(first);
        dao2.save(second);
        dao3.save(third);

        relationService.createRelation(first, second);
        relationService.createRelation(first, third);
        relationService.createRelation(second, third);
        final RelationLink bySourceAndIdentity =
            relationService.findRelationBySourceObjectAndRelationIdentity(first, second);

        Assertions.assertThat(bySourceAndIdentity).isNotNull();
        Assertions.assertThat(bySourceAndIdentity.getSourceObject()).isEqualTo(first);
        Assertions.assertThat(bySourceAndIdentity.getTargetType()).isEqualTo(second.getType());
    }


    @Test
    void shouldDeleteRelation() {
        var first = new PersonEntity();
        var second = new DogEntity();
        var third = new DocumentEntity();
        dao.save(first);
        dao2.save(second);
        dao3.save(third);
        relationService.createRelation(first, second);
        relationService.createRelation(first, third);
        relationService.createRelation(second, third);
        final RelationLink bySourceAndIdentity =
            relationService.findRelationBySourceObjectAndRelationIdentity(first, second);

        relationService.deleteRelation(bySourceAndIdentity);

        final RelationLink afterDelete =
            relationService.findRelationBySourceObjectAndRelationIdentity(first, second);
        Assertions.assertThat(afterDelete).isNull();
    }

    @Test
    void shouldPrintRelations() {
        var first = new PersonEntity();
        var second = new DogEntity();
        var third = new DocumentEntity();
        dao.save(first);
        dao2.save(second);
        dao3.save(third);

        relationService.createRelation(first, second);
        relationService.createRelation(first, third);
        relationService.createRelation(second, third);

        Assertions.assertThat(dynamicRelationsPrintService.printRelations(first)).isEqualTo("""
            AnnotationTestType
             AnnotationTest3Type
             AnnotationTest2Type
              AnnotationTest3Type
            """);
    }
    @Test
    void shouldPrintRelationsWithCyclicRelations() {
        var first = new PersonEntity();
        var second = new DogEntity();
        var third = new DocumentEntity();
        dao.save(first);
        dao2.save(second);
        dao3.save(third);

        relationService.createRelation(first, second);
        relationService.createRelation(first, third);
        relationService.createRelation(second, third);
        relationService.createRelation(third, second);

        Assertions.assertThat(dynamicRelationsPrintService.printRelations(first)).isEqualTo("""
          AnnotationTestType
           AnnotationTest3Type
            AnnotationTest2Type
           AnnotationTest2Type
            AnnotationTest3Type
          """);
    }
}
