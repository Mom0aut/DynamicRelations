package at.test.drm;

import at.drm.EnableDynamicRelation;
import at.drm.model.RelationLink;
import at.drm.service.RelationService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("integration")
@EnableDynamicRelation
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.ZONKY)
class ApplicationIntegrationTest {
    
    @Autowired
    private AnnotaionDao dao;
    @Autowired
    private Annotaion2Dao dao2;
    @Autowired
    private Annotaion3Dao dao3;
    @Autowired
    private RelationService relationService;

    @Test
    void shouldFindRelationBySourceObject() {
        var first = new AnnotationTest();
        var second = new AnnotationTest2();
        var third = new AnnotationTest3();
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
        var first = new AnnotationTest();
        var second = new AnnotationTest2();
        var third = new AnnotationTest3();
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
        var first = new AnnotationTest();
        var second = new AnnotationTest2();
        var third = new AnnotationTest3();
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
        var first = new AnnotationTest();
        var second = new AnnotationTest2();
        var third = new AnnotationTest3();
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
}
