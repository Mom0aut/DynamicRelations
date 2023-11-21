package at.test.drm;

import at.drm.annotation.Relation;
import at.drm.model.RelationIdentity;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Relation(sourceClass = AnnotationTest2.class)
@Entity
@Data
public class AnnotationTest2 implements RelationIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public String getType() {
        return "AnnotationType2";
    }
}
