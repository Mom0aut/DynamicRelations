import at.drm.annotation.Relation;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Relation(sourceClass = TestFile.class)
@Entity
@Data
public class TestFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}