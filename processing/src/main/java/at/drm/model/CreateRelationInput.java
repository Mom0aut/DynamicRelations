package at.drm.model;

public record CreateRelationInput(Object sourceObject, Long targetId, String targetType) {
}
