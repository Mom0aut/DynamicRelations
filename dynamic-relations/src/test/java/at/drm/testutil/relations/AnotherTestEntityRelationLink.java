package at.drm.testutil.relations;

import at.drm.model.RelationLink;
import at.drm.testutil.entities.AnotherTestEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnotherTestEntityRelationLink implements RelationLink<AnotherTestEntity> {
    private AnotherTestEntity sourceObject;
    private Long targetId;
    private String targetType;
}
