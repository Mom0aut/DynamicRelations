package at.drm.util;

import at.drm.dao.RelationDao;
import at.drm.factory.RelationDaoFactory;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DynamicRelationsUtils {

    private final RelationDaoFactory relationDaoFactory;

    public DynamicRelationsUtils(RelationDaoFactory relationDaoFactory) {
        this.relationDaoFactory = relationDaoFactory;
    }

    public List<Class<?>> listRegisteredEntities() {
        Set<RelationDao> allDaos = relationDaoFactory.getAllDaos();

        return allDaos.stream()
            .map(this::extractEntityClassFromDao)
            .distinct()
            .collect(Collectors.toList());
    }

    private Class<?> extractEntityClassFromDao(RelationDao relationDao) {
        try {
            ResolvableType resolvableType = ResolvableType.forClass(relationDao.getClass()).as(RelationDao.class);
            ResolvableType generic = resolvableType.getGeneric(0);
            Class<?> relationLinkClass = generic.resolve();

            if (relationLinkClass == null) {
                throw new RuntimeException("Could not resolve RelationLink class for DAO: " + relationDao.getClass());
            }

            Field sourceObjectField = relationLinkClass.getDeclaredField("sourceObject");
            return sourceObjectField.getType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(
                "Could not find sourceObject field in RelationLink class for DAO: " + relationDao.getClass(), e
            );
        }
    }
}
