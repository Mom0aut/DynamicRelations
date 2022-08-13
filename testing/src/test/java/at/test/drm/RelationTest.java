package at.test.drm;


import at.drm.factory.RelationDaoFactory;
import at.drm.model.RelationLink;
import at.drm.service.RelationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
@Disabled
//Test
class RelationTest {

    @Autowired
    private AnnotaionDao annotaionDao;

    @Autowired
    private Annotaion2Dao annotaion2Dao;

    private final RelationDaoFactory relationDaoFactory;

    private final RelationService relationService;


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
        RelationLink test = relationService.createRelation(annotationTest2, annotationTest);
        relationService.deleteRelation(test);

    }

}