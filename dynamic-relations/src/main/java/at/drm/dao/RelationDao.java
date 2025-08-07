package at.drm.dao;

import at.drm.model.RelationIdentity;
import at.drm.model.RelationLink;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RelationDao<DRM extends RelationLink, L extends Long> extends CrudRepository<DRM, L> {

    RelationLink findBySourceObjectAndTargetIdAndTargetType(RelationIdentity sourceObject, Long targetId,
        String targetType);

    List<RelationLink> findBySourceObject(RelationIdentity sourceObject);

    List<RelationLink> findByTargetIdAndTargetType(Long targetId, String targetType);
}
