package md.utm.pad.labs.exception;

/**
 * Created by anrosca on Dec, 2017
 */
public class UnknownHttpMethodException extends RuntimeException {
    public UnknownHttpMethodException(String message) {
        super(message);
    }
}
