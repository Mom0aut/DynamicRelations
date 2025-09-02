package at.test.drm;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogDao extends CrudRepository<DogEntity, Long> {

}
