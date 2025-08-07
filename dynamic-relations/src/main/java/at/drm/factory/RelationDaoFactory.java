package at.drm.factory;

import at.drm.dao.RelationDao;
import at.drm.exception.NoRelationDaoFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RelationDaoFactory {

    private final ApplicationContext applicationContext;

    public RelationDao getDaoFromSourceObjectClass(Class dynamicRelactionClass) {
        Map<String, RelationDao> beansOfType = applicationContext.getBeansOfType(RelationDao.class);
        RelationDao relationDao = beansOfType.values().stream()
                .filter(dao -> {
                    ResolvableType resolvableType = ResolvableType.forClass(dao.getClass())
                            .as(RelationDao.class);
                    ResolvableType generic = resolvableType.getGeneric(0);
                    Class<?> resolve = generic.resolve();
                    assert resolve != null;
                    Field sourceObject = getDeclaredField(resolve, "sourceObject");
                    Class<?> type = sourceObject.getType();
                    return type.equals(dynamicRelactionClass);
                }).findFirst().orElseThrow(() -> new NoRelationDaoFoundException("No DynamicRelationDao was found!"));
        return relationDao;
    }

    public Set<RelationDao> getAllDaos() {
        Map<String, RelationDao> beansOfType = applicationContext.getBeansOfType(RelationDao.class);
        Set<RelationDao> relationDaos = new HashSet<>(beansOfType.values());
        return relationDaos;
    }

    private Field getDeclaredField(Class<?> resolve, String field) {
        try {
            return resolve.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
