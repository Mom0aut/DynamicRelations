package at.test.drm;

import at.drm.dao.RelationDao;
import at.drm.model.RelationIdentity;
import at.drm.model.RelationLink;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface AnnotationTestRelationDao extends RelationDao<AnnotationTestRelation, Long> {

    @Override
    default RelationLink findBySourceObjectAndTargetIdAndTargetType(RelationIdentity sourceObject, Long targetId, String targetType) {

        return null;
    }

    @Override
    default List<RelationLink> findBySourceObject(RelationIdentity sourceObject) {

        return List.of();
    }

    @Override
    default List<RelationLink> findByTargetIdAndTargetType(Long targetId, String targetType) {

        return List.of();
    }
}
