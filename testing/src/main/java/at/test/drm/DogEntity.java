package at.test.drm;

import at.drm.annotation.Relation;
import at.drm.model.RelationIdentity;
import jakarta.persistence.*;
import lombok.Data;

@Relation
@Entity
@Data
public class DogEntity implements RelationIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
}
