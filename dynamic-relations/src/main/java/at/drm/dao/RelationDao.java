package at.drm.dao;

import at.drm.model.RelationLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface RelationDao<DRM extends RelationLink, L extends Long> extends CrudRepository<DRM, L> {

    RelationLink findBySourceObjectAndTargetIdAndTargetType(Object sourceObject, Long targetId, String targetType);

    List<RelationLink> findBySourceObject(Object sourceObject);

    List<RelationLink> findByTargetIdAndTargetType(Long targetId, String targetType);
}
