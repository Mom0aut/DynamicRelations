package at.drm.dao;

import at.drm.model.DynamicRelationModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DynamicRelationDao<DRM extends DynamicRelationModel, L extends Long> extends CrudRepository<DRM, L> {

}
