import at.drm.annotation.Relation;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Relation(type = "Testing", sourceClass = TestFile.class)
@Entity
@Data
public class TestFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}