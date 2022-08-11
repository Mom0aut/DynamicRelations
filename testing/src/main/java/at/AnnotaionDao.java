package at;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnotaionDao extends CrudRepository<AnnotationTest, Long> {

}
