package at.drm.testutil.relations;

import at.drm.model.RelationLink;
import at.drm.testutil.entities.SomeTestEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestEntityRelationLink implements RelationLink<SomeTestEntity> {
    private SomeTestEntity sourceObject;
    private Long targetId;
    private String targetType;
}
