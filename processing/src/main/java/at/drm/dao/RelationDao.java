package at.drm.dao;

import at.drm.model.RelationLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RelationDao<DRM extends RelationLink, L extends Long> extends CrudRepository<DRM, L> {

}
