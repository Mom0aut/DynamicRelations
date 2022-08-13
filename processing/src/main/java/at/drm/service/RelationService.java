package at.drm.service;


import at.drm.dao.RelationDao;
import at.drm.factory.RelationDaoFactory;
import at.drm.model.RelationIdentity;
import at.drm.model.RelationLink;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationDaoFactory relationDaoFactory;

    public RelationLink createRelation(@NonNull Object sourceObject, @NonNull RelationIdentity targetObect) {
        Long targetId = targetObect.getId();
        String targetType = targetObect.getType();
        RelationDao<RelationLink, Long> daoFromSourceObjectClass =
                relationDaoFactory.getDaoFromSourceObjectClass(sourceObject.getClass());
        RelationLink relationLink = createRelationModelFromGenericParameter(daoFromSourceObjectClass);
        relationLink.setSourceObject(sourceObject);
        relationLink.setTargetId(targetId);
        relationLink.setTargetType(targetType);
        return daoFromSourceObjectClass.save(relationLink);
    }

    public void deleteRelation(RelationLink relationLink) {
        RelationDao<RelationLink, Long> daoFromSourceObjectClass =
                relationDaoFactory.getDaoFromSourceObjectClass(relationLink.getSourceObject().getClass());
        daoFromSourceObjectClass.delete(relationLink);
    }

    private static RelationLink createRelationModelFromGenericParameter(RelationDao<RelationLink,
            Long> daoFromSourceObjectClass) {
        ResolvableType resolvableType = ResolvableType.forClass(daoFromSourceObjectClass.getClass())
                .as(RelationDao.class);
        ResolvableType generic = resolvableType.getGeneric(0);
        Class<?> resolve = generic.resolve();
        return (RelationLink) BeanUtils.instantiateClass(resolve);
    }
}
