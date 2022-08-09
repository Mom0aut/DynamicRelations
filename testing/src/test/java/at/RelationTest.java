package at;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
//Test
class RelationTest {

    @Autowired
    private DokumentRelationDao dokumentRelationDao;

    @Autowired
    private Annotaion2Dao annotaion2Dao;


    @Test
    void createDokumentRelation() {


        AnnotationTest2 annotationTest2 = new AnnotationTest2();

        AnnotationTest2 save1 = annotaion2Dao.save(annotationTest2);

        DokumentRelation dokumentRelation = new DokumentRelation();

        dokumentRelation.setSourceObject(annotationTest2);

        dokumentRelation.setTargetId(save1.getId());

        dokumentRelation.setTargetType("TEST");

        DokumentRelation save = dokumentRelationDao.save(dokumentRelation);


        AnnotationTest2 sourceObject = save.getSourceObject();

        System.out.println();
    }

    @Test
    void shouldFail() {

        AnnotationTest2 annotationTest2 = annotaion2Dao.findById(1L).get();


        DokumentRelation dokumentRelation = new DokumentRelation();

        dokumentRelation.setSourceObject(annotationTest2);

        dokumentRelation.setTargetId(annotationTest2.getId());

        dokumentRelation.setTargetType("TEST");

        DokumentRelation save2 = dokumentRelationDao.save(dokumentRelation);

    }


    @Test
    void shouldNotFail() {

        AnnotationTest2 annotationTest2 = annotaion2Dao.findById(1L).get();


        DokumentRelation dokumentRelation = new DokumentRelation();

        dokumentRelation.setSourceObject(annotationTest2);

        dokumentRelation.setTargetId(annotationTest2.getId());

        dokumentRelation.setTargetType("TEST2");

        DokumentRelation save2 = dokumentRelationDao.save(dokumentRelation);

    }

}