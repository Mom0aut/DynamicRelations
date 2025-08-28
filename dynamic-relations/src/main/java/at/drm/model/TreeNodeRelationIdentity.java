package at.drm.model;

import java.util.List;

public record TreeNodeRelationIdentity(RelationIdentity object, List<TreeNodeRelationIdentity> childObjects) {

}
