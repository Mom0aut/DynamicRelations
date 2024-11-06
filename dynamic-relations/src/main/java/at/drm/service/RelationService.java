package at.drm.service;

import at.drm.dao.RelationDao;
import at.drm.model.RelationIdentity;
import at.drm.model.RelationLink;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final List<RelationDao> relationDaos;

    public RelationLink createRelation(@NonNull Object sourceObject, @NonNull RelationIdentity targetObect) {
        Long targetId = targetObect.getId();
        String targetType = targetObect.getType();
        RelationDao<RelationLink, Long> daoFromSourceObject = getDaoFromSourceObjectClass(sourceObject.getClass());
        RelationLink relationLink = createRelationModelFromGenericParameter(daoFromSourceObject);
        relationLink.setSourceObject(sourceObject);
        relationLink.setTargetId(targetId);
        relationLink.setTargetType(targetType);
        return daoFromSourceObject.save(relationLink);
    }

    public void deleteRelation(RelationLink relationLink) {
        RelationDao<RelationLink, Long> daoFromSourceObjectClass = getDaoFromSourceObjectClass(relationLink.getSourceObject().getClass());
        daoFromSourceObjectClass.delete(relationLink);
    }

    public RelationLink findRelationBySourceObjectAndRelationIdentity(@NonNull Object sourceObject, @NonNull RelationIdentity targetObect) {
        RelationDao<RelationLink, Long> daoFromSourceObject = getDaoFromSourceObjectClass(sourceObject.getClass());
        RelationLink relationLink = daoFromSourceObject.findBySourceObjectAndTargetIdAndTargetType(sourceObject, targetObect.getId(), targetObect.getType());
        return relationLink;
    }

    public List<RelationLink> findRelationBySourceObject(@NonNull Object sourceObject) {
        RelationDao<RelationLink, Long> daoFromSourceObject = getDaoFromSourceObjectClass(sourceObject.getClass());
        List<RelationLink> relationLinks = daoFromSourceObject.findBySourceObject(sourceObject);
        return relationLinks;
    }

    public Set<RelationLink> findRelationByTargetRelationIdentity(@NonNull RelationIdentity targetObect) {
        Set<RelationLink> relations = new HashSet<>();
        relationDaos.forEach(dao -> relations.addAll(dao.findByTargetIdAndTargetType(targetObect.getId(), targetObect.getType())));
        return relations;
    }

    private RelationDao<RelationLink, Long> getDaoFromSourceObjectClass(Class<?> sourceObjectClass) {
        return relationDaos.stream()
                .filter(dao -> {
                    ResolvableType resolvableType = ResolvableType.forClass(dao.getClass()).as(RelationDao.class);
                    ResolvableType generic = resolvableType.getGeneric(0);
                    Class<?> resolve = generic.resolve();
                    assert resolve != null;
                    return resolve.equals(sourceObjectClass);
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No RelationDao found for class: " + sourceObjectClass.getName()));
    }

    private static RelationLink createRelationModelFromGenericParameter(RelationDao<RelationLink, Long> daoFromSourceObjectClass) {
        ResolvableType resolvableType = ResolvableType.forClass(daoFromSourceObjectClass.getClass()).as(RelationDao.class);
        ResolvableType generic = resolvableType.getGeneric(0);
        Class<?> resolve = generic.resolve();
        return (RelationLink) BeanUtils.instantiateClass(resolve);
    }
}
