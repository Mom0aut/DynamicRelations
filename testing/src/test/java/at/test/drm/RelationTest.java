package at.test.drm;


import java.util.List;
import java.util.Set;

import at.drm.util.DynamicRelationsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import at.drm.factory.RelationDaoFactory;
import at.drm.model.RelationLink;
import at.drm.service.RelationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Disabled
//Test
class RelationTest {

    @Autowired
    private PersonDao personEntityDao;

    @Autowired
    private DogDao dogEntityDao;

    @Autowired
    private DocumentDao documentEntityDao;

    private final RelationDaoFactory relationDaoFactory;

    private final RelationService relationService;

    private DynamicRelationsUtils dynamicRelationsUtils;

    @BeforeEach
    void setUp() {
        dynamicRelationsUtils = new DynamicRelationsUtils(relationDaoFactory);
    }

    public RelationTest(ApplicationContext applicationContext) {
        this.relationDaoFactory = new RelationDaoFactory(applicationContext);
        this.relationService = new RelationService(this.relationDaoFactory);
    }


    @Test
    void testCreateRelation() {

        PersonEntity personEntity = new PersonEntity();
        personEntityDao.save(personEntity);

        DogEntity dogEntity = new DogEntity();
        dogEntityDao.save(dogEntity);

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntityDao.save(documentEntity);

        RelationLink test = relationService.createRelation(personEntity, dogEntity);
        RelationLink test2 = relationService.createRelation(personEntity, documentEntity);
        RelationLink test3 = relationService.createRelation(dogEntity, documentEntity);

        RelationLink relationLink = relationService.findRelationBySourceObjectAndRelationIdentity(personEntity, dogEntity);

        List<RelationLink> relationBySourceObject = relationService.findRelationBySourceObject(personEntity);
        Set<RelationLink> relationByTargetRelationIdentity = relationService.findRelationByTargetRelationIdentity(documentEntity);
        relationService.deleteRelation(test);
        System.out.println();

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

    @Test
    void testListRegisteredEntities_shouldReturnEmptyListWhenNoDAOsRegistered() {
        RelationDaoFactory mockFactory = org.mockito.Mockito.mock(RelationDaoFactory.class);
        when(mockFactory.getAllDaos()).thenReturn(Set.of());

        DynamicRelationsUtils mockUtils = new DynamicRelationsUtils(mockFactory);
        List<Class<?>> result = mockUtils.listRegisteredEntities();

        assertThat(result).isEmpty();
    }

}