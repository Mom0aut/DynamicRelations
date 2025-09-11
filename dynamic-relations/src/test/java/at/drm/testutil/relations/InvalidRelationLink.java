package at.drm.testutil.relations;

import at.drm.model.RelationLink;
import at.drm.testutil.entities.SomeTestEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvalidRelationLink implements RelationLink<SomeTestEntity> {
    private SomeTestEntity wrongFieldName;
    private Long targetId;
    private String targetType;

    @Override
    public SomeTestEntity getSourceObject() {
        return wrongFieldName;
    }

    @Override
    public void setSourceObject(SomeTestEntity sourceObject) {
        this.wrongFieldName = sourceObject;
    }
}
