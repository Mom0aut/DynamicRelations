package at.drm.model;

import at.drm.annotation.Relation;
import com.squareup.javapoet.TypeName;

public record RelationMetaData(TypeName sourceObjectName, String packageName, String generatedName,
                               Relation relationAnnotation) {

}
