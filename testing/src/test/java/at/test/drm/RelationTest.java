package at.test.drm;


import at.drm.factory.DynamicRelationDaoFactory;
import at.drm.model.CreateRelationInput;
import at.drm.model.DynamicRelationModel;
import at.drm.service.DynamicRelationService;
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
    private Annotaion2Dao annotaion2Dao;

    private final DynamicRelationDaoFactory dynamicRelationDaoFactory;

    private final DynamicRelationService dynamicRelationService;


    public RelationTest(ApplicationContext applicationContext) {
        this.dynamicRelationDaoFactory = new DynamicRelationDaoFactory(applicationContext);
        this.dynamicRelationService = new DynamicRelationService(this.dynamicRelationDaoFactory);
    }


    @Test
    void testCreateRelation() {
        AnnotationTest2 annotationTest2 = new AnnotationTest2();
        annotaion2Dao.save(annotationTest2);
        CreateRelationInput createRelationInput = new CreateRelationInput(annotationTest2, 2L, "Testing");
        DynamicRelationModel test = dynamicRelationService.createDynamicRelation(createRelationInput);
        dynamicRelationService.deleteDynamicRelation(test);

    }

}