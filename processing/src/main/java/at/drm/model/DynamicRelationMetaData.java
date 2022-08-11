package at.drm.model;

import at.drm.annotation.Relation;

public record DynamicRelationMetaData(String type, String packageName, String generatedName,
                                      Relation relationAnnotation) {

}
