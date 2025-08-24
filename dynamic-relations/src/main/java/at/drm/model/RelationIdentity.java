package at.drm.model;

public interface RelationIdentity {

    Long getId();

    /**
     * Returns the type of the relation identity.
     * This is used to identify the type of the relation in a generic way.
     *
     * @return the type of the relation identity
     */
    default String getType() {
        return this.getClass().getSimpleName() + "Type";
    }
}
