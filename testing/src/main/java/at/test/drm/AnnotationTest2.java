package at.test.drm;

import at.drm.annotation.Relation;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Relation(sourceClass = AnnotationTest2.class)
@Entity
@Data
public class AnnotationTest2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
