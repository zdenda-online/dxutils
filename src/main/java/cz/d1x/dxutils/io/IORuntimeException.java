package cz.d1x.dxutils.io;

/**
 * Runtime exception that is related to exception states related to input/output operations.
 * Typically wraps checked {@link java.io.IOException} to allow "prettier" work with API without need to catch exception.
 */
public class IORuntimeException extends RuntimeException {

    public IORuntimeException() {
    }

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IORuntimeException(Throwable cause) {
        super(cause);
    }

    public IORuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
