package at.test.drm;

import at.drm.annotation.Relation;
import at.drm.model.RelationIdentity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Relation(sourceClass = AnnotationTest.class)
@Entity
@Data
public class AnnotationTest implements RelationIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public String getType() {
        return "TestType";
    }
}
