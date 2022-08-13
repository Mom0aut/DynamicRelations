package at.test.drm;

import at.drm.dao.RelationDao;
import at.drm.exception.NoRelationDaoFoundException;
import at.drm.factory.RelationDaoFactory;
import at.drm.model.RelationLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

;

@ExtendWith(MockitoExtension.class)
class RelationDaoFactoryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private AnnotationTest2RelationDao annotationTest2RelationDao;

    @Mock
    private RelationDao relationDao;

    @InjectMocks
    private RelationDaoFactory relationDaoFactoryUnderTest;

    @Test
    void getDaoFromSourceObjectClass() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
                .thenReturn(Map.ofEntries(Map.entry("testDao", annotationTest2RelationDao)));
        RelationDao<RelationLink, Long> daoFromSourceObjectClass = relationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                AnnotationTest2.class);
        assertThat(daoFromSourceObjectClass).isNotNull();
        assertThat(daoFromSourceObjectClass).isInstanceOf(annotationTest2RelationDao.getClass());
    }

    @Test
    void getDaoFromSourceObjectClassShouldThrowNoDaoFoundException() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
                .thenReturn(Map.ofEntries(Map.entry("testDao", annotationTest2RelationDao)));
        NoRelationDaoFoundException exception = Assertions.assertThrows(NoRelationDaoFoundException.class, () -> {
            relationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                    AnnotationTest.class);
        });
        assertThat(exception).isNotNull();
    }


    @Test
    void getDaoFromSourceObjectClassShouldThrowRuntimeException() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
                .thenReturn(Map.ofEntries(Map.entry("wrongTestDao", relationDao)));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            relationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                    AnnotaionDao.class);
        });
        assertThat(exception).isNotNull();
    }

}