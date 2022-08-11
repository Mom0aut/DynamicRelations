package at.drm.factory;

import at.drm.dao.DynamicRelationDao;
import at.drm.exception.NoDynamicDaoFoundException;
import at.drm.model.DynamicRelationModel;
import java.lang.reflect.Field;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DynamicRelationDaoFactory {

    private final ApplicationContext applicationContext;

    public DynamicRelationDao<DynamicRelationModel, Long> getDaoFromSourceObjectClass(Class dynamicRelactionClass) {
        Map<String, DynamicRelationDao> beansOfType = applicationContext.getBeansOfType(DynamicRelationDao.class);
        DynamicRelationDao<DynamicRelationModel, Long> dynamicRelationDao = beansOfType.values().stream()
            .filter(dao -> {
                ResolvableType resolvableType = ResolvableType.forClass(dao.getClass())
                    .as(DynamicRelationDao.class);
                ResolvableType generic = resolvableType.getGeneric(0);
                Class<?> resolve = generic.resolve();
                assert resolve != null;
                Field sourceObject = getDeclaredField(resolve, "sourceObject");
                Class<?> type = sourceObject.getType();
                return type.equals(dynamicRelactionClass);
            }).findFirst().orElseThrow(() -> new NoDynamicDaoFoundException("No DynamicRelationDao was found!"));
        return dynamicRelationDao;
    }

    private Field getDeclaredField(Class<?> resolve, String field) {
        try {
            return resolve.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
