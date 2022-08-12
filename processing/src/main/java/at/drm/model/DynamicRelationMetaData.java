package at.drm.model;

import at.drm.annotation.Relation;
import com.squareup.javapoet.TypeName;

public record DynamicRelationMetaData(TypeName sourceObjectName, String packageName, String generatedName,
                                      Relation relationAnnotation) {

}
