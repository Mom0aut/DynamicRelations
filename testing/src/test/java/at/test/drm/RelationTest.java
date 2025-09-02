package at.test.drm;


import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import at.drm.factory.RelationDaoFactory;
import at.drm.model.RelationLink;
import at.drm.service.RelationService;

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

}