package at.drm.util;

import at.drm.factory.RelationDaoFactory;
import at.drm.testutil.daos.AnotherTestEntityRelationDao;
import at.drm.testutil.daos.InvalidRelationDao;
import at.drm.testutil.daos.TestEntityRelationDao;
import at.drm.testutil.entities.AnotherTestEntity;
import at.drm.testutil.entities.SomeTestEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicRelationsUtilsTest {

    @Mock
    private RelationDaoFactory relationDaoFactory;

    private DynamicRelationsUtils dynamicRelationsUtils;

    @BeforeEach
    void setUp() {
        dynamicRelationsUtils = new DynamicRelationsUtils(relationDaoFactory);
    }

    @Test
    void testListRegisteredEntities_WithMultipleDaos_ShouldReturnDistinctEntityClasses() {
        TestEntityRelationDao dao1 = mock(TestEntityRelationDao.class);
        AnotherTestEntityRelationDao dao2 = mock(AnotherTestEntityRelationDao.class);

        when(relationDaoFactory.getAllDaos()).thenReturn(Set.of(dao1, dao2));

        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(SomeTestEntity.class));
        assertTrue(result.contains(AnotherTestEntity.class));
        verify(relationDaoFactory, times(1)).getAllDaos();
    }

    @Test
    void testListRegisteredEntities_WithDuplicateEntityTypes_ShouldReturnDistinctClasses() {
        TestEntityRelationDao dao1 = mock(TestEntityRelationDao.class);
        TestEntityRelationDao dao2 = mock(TestEntityRelationDao.class);

        when(relationDaoFactory.getAllDaos()).thenReturn(Set.of(dao1, dao2));

        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(SomeTestEntity.class, result.getFirst());
        verify(relationDaoFactory, times(1)).getAllDaos();
    }

    @Test
    void testListRegisteredEntities_WithEmptyDaoSet_ShouldReturnEmptyList() {
        when(relationDaoFactory.getAllDaos()).thenReturn(Set.of());

        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(relationDaoFactory, times(1)).getAllDaos();
    }

    @Test
    void testExtractEntityClassFromDao_WithValidDao_ShouldReturnCorrectEntityClass() {
        TestEntityRelationDao dao = mock(TestEntityRelationDao.class);
        when(relationDaoFactory.getAllDaos()).thenReturn(Set.of(dao));

        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(SomeTestEntity.class, result.getFirst());
    }

    @Test
    void testExtractEntityClassFromDao_WithInvalidRelationLink_ShouldThrowRuntimeException() {
        InvalidRelationDao invalidDao = mock(InvalidRelationDao.class);
        when(relationDaoFactory.getAllDaos()).thenReturn(Set.of(invalidDao));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> dynamicRelationsUtils.listRegisteredEntities());

        assertTrue(exception.getMessage().contains("Could not find sourceObject field"));
        assertTrue(exception.getMessage().contains("InvalidRelationDao"));
        verify(relationDaoFactory, times(1)).getAllDaos();
    }

    @Test
    void testListRegisteredEntities_WithSingleDao_ShouldReturnSingleEntity() {
        TestEntityRelationDao dao = mock(TestEntityRelationDao.class);
        when(relationDaoFactory.getAllDaos()).thenReturn(Set.of(dao));

        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(SomeTestEntity.class, result.getFirst());
        verify(relationDaoFactory, times(1)).getAllDaos();
    }

    @Test
    void testListRegisteredEntities_VerifyStreamProcessing() {
        TestEntityRelationDao dao1 = mock(TestEntityRelationDao.class);
        AnotherTestEntityRelationDao dao2 = mock(AnotherTestEntityRelationDao.class);
        TestEntityRelationDao dao3 = mock(TestEntityRelationDao.class);

        when(relationDaoFactory.getAllDaos()).thenReturn(Set.of(dao1, dao2, dao3));

        List<Class<?>> result = dynamicRelationsUtils.listRegisteredEntities();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(SomeTestEntity.class));
        assertTrue(result.contains(AnotherTestEntity.class));

        assertInstanceOf(List.class, result);
        verify(relationDaoFactory, times(1)).getAllDaos();
    }
}
