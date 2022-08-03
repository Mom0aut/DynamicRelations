package at;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumentRelationDao extends CrudRepository<DokumentRelation, Long> {
}
