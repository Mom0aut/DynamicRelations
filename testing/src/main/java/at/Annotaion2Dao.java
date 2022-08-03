package at;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Annotaion2Dao extends CrudRepository<AnnotationTest2, Long> {
}
