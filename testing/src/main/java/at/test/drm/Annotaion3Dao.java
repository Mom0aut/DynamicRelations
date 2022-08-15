package at.test.drm;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Annotaion3Dao extends CrudRepository<AnnotationTest3, Long> {

}
