package at.test.drm;

import at.drm.annotation.Relation;
import at.drm.model.RelationLink;
import at.drm.model.RelationIdentity;
import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Relation
@Entity
@Data
public class AnnotationTestRelation implements RelationLink<RelationIdentity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_object_id")
    private RelationIdentity sourceObject;

    private Long targetId;
    
    private String targetType;
}
