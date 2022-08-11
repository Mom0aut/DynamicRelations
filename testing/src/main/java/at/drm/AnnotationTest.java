package at.drm;

import at.drm.annotation.Relation;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Relation(type = "Testing", sourceClass = AnnotationTest.class)
@Entity
@Data
public class AnnotationTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
