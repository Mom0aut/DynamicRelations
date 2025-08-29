package at.test.drm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import at.drm.dao.RelationDao;
import at.drm.exception.NoRelationDaoFoundException;
import at.drm.factory.RelationDaoFactory;


@ExtendWith(MockitoExtension.class)
class RelationDaoFactoryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private DogEntityDao dogEntityDao;

    @Mock
    private RelationDao relationDao;

    @InjectMocks
    private RelationDaoFactory relationDaoFactoryUnderTest;

    @Test
    void getDaoFromSourceObjectClass() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
                .thenReturn(Map.ofEntries(Map.entry("testDao", dogEntityDao)));
        RelationDao daoFromSourceObjectClass = relationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                DogEntity.class);
        assertThat(daoFromSourceObjectClass).isNotNull();
        assertThat(daoFromSourceObjectClass).isInstanceOf(dogEntityDao.getClass());
    }

    @Test
    void getDaoFromSourceObjectClassShouldThrowNoDaoFoundException() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
                .thenReturn(Map.ofEntries(Map.entry("testDao", dogEntityDao)));
        NoRelationDaoFoundException exception = Assertions.assertThrows(NoRelationDaoFoundException.class, () -> {
            relationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                    PersonEntity.class);
        });
        assertThat(exception).isNotNull();
    }


    @Test
    void getDaoFromSourceObjectClassShouldThrowRuntimeException() {
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
                .thenReturn(Map.ofEntries(Map.entry("wrongTestDao", relationDao)));
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            relationDaoFactoryUnderTest.getDaoFromSourceObjectClass(
                    PersonEntity.class);
        });
        assertThat(exception).isNotNull();
    }

    @Test
    void getAllDaos() {
        Map<String, RelationDao> testBeansOfType = Map.ofEntries(Map.entry("testDaos", relationDao));
        Mockito.when(applicationContext.getBeansOfType(any(Class.class)))
                .thenReturn(Map.ofEntries(Map.entry("testDaos", relationDao)));
        Set<RelationDao> actual = new HashSet<>(testBeansOfType.values());
        Set<RelationDao> excepted = relationDaoFactoryUnderTest.getAllDaos();
        assertThat(actual).isEqualTo(excepted);
    }
}