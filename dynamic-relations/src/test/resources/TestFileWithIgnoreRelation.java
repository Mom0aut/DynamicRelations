import at.drm.annotation.Relation;
import at.drm.annotation.IgnoreRelation;
// Lombok entfernt, Methoden werden manuell implementiert

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@IgnoreRelation
@Relation(sourceClass = TestFileWithIgnoreRelation.class)
@Entity
public class TestFileWithIgnoreRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public TestFileWithIgnoreRelation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}