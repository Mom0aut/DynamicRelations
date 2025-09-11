package at.test.drm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import at.drm.dao.RelationDao;
import at.drm.factory.RelationDaoFactory;
import at.drm.util.DynamicRelationsUtils;
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
    private PersonDao dao;
    @Autowired
    private DogDao dao2;
    @Autowired
    private DocumentDao dao3;
    @Autowired
    private RelationService relationService;
    @Autowired
    private DynamicRelationsPrintService dynamicRelationsPrintService;
    @Autowired
    private DynamicRelationsUtils dynamicRelationsUtils;
    @Autowired
    private RelationDaoFactory relationDaoFactory;

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

        String actual = dynamicRelationsPrintService.printRelations(first);
        System.out.println(actual);
        Assertions.assertThat(actual).isEqualTo("""
            PersonEntityType
             DocumentEntityType
             DogEntityType
              DocumentEntityType
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
            PersonEntityType
             DocumentEntityType
              DogEntityType
             DogEntityType
              DocumentEntityType
            """);
    }

    @Test
    void testListRegisteredEntities_shouldReturnRegisteredEntities() {
        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(
            PersonEntity.class,
            DogEntity.class,
            DocumentEntity.class
        );
    }
}
