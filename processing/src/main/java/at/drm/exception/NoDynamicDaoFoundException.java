package at.drm.exception;

public class NoDynamicDaoFoundException extends RuntimeException {

    public NoDynamicDaoFoundException(String message) {
        super(message);
    }
}
