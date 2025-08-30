package at.test.drm;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogEntityDao extends CrudRepository<DogEntity, Long> {

}
