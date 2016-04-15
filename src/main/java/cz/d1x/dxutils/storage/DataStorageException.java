package cz.d1x.dxutils.storage;

/**
 * Runtime exception wrapping other exceptions thrown directly from {@link DataStorage} implementation.
 * Typically wraps checked {@link java.io.IOException}
 */
public class DataStorageException extends RuntimeException {

    public DataStorageException(Throwable cause) {
        super(cause);
    }

    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
