package at.test.drm;


import at.drm.factory.RelationDaoFactory;
import at.drm.model.RelationLink;
import at.drm.service.RelationService;
import at.drm.util.DynamicRelationsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Disabled
//Test
class RelationTest {

    @Autowired
    private AnnotaionDao annotaionDao;

    @Autowired
    private Annotaion2Dao annotaion2Dao;

    @Autowired
    private Annotaion3Dao annotaion3Dao;

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

        AnnotationTest annotationTest = new AnnotationTest();
        annotaionDao.save(annotationTest);

        AnnotationTest2 annotationTest2 = new AnnotationTest2();
        annotaion2Dao.save(annotationTest2);

        AnnotationTest3 annotationTest3 = new AnnotationTest3();
        annotaion3Dao.save(annotationTest3);

        RelationLink test = relationService.createRelation(annotationTest, annotationTest2);
        RelationLink test2 = relationService.createRelation(annotationTest, annotationTest3);
        RelationLink test3 = relationService.createRelation(annotationTest2, annotationTest3);

        RelationLink relationLink = relationService.findRelationBySourceObjectAndRelationIdentity(annotationTest, annotationTest2);

        List<RelationLink> relationBySourceObject = relationService.findRelationBySourceObject(annotationTest);
        Set<RelationLink> relationByTargetRelationIdentity = relationService.findRelationByTargetRelationIdentity(annotationTest3);
        relationService.deleteRelation(test);
        System.out.println();

    }

    @Test
    void testListRegisteredEntities_shouldReturnRegisteredEntities() {
        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(
            AnnotationTest.class,
            AnnotationTest2.class,
            AnnotationTest3.class
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