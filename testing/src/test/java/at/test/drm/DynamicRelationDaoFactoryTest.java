package at.test.drm;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

import at.drm.dao.DynamicRelationDao;
import at.drm.exception.NoDynamicDaoFoundException;
import at.drm.factory.DynamicRelationDaoFactory;
import at.drm.model.DynamicRelationModel;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

;

@ExtendWith(MockitoExtension.class)
class DynamicRelationDaoFactoryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private DokumentRelationDao dokumentRelationDao;

    @Mock
    private DynamicRelationDao dynamicRelationDao;

    @InjectMocks
    private DynamicRelationDaoFactory dynamicRelationDaoFactoryUnderTest;

    @Test
    void getDaoFromSourceObjectClass() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
            .thenReturn(Map.ofEntries(Map.entry("testDao", dokumentRelationDao)));
        DynamicRelationDao<DynamicRelationModel, Long> daoFromSourceObjectClass = dynamicRelationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
            AnnotationTest2.class);
        assertThat(daoFromSourceObjectClass).isNotNull();
        assertThat(daoFromSourceObjectClass).isInstanceOf(dokumentRelationDao.getClass());
    }

    @Test
    void getDaoFromSourceObjectClassShouldThrowNoDaoFoundException() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
            .thenReturn(Map.ofEntries(Map.entry("testDao", dokumentRelationDao)));
        NoDynamicDaoFoundException exception = Assertions.assertThrows(NoDynamicDaoFoundException.class, () -> {
            dynamicRelationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                AnnotationTest.class);
        });
        assertThat(exception).isNotNull();
    }


    @Test
    void getDaoFromSourceObjectClassShouldThrowRuntimeException() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
            .thenReturn(Map.ofEntries(Map.entry("wrongTestDao", dynamicRelationDao)));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            dynamicRelationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                AnnotaionDao.class);
        });
        assertThat(exception).isNotNull();
    }

}