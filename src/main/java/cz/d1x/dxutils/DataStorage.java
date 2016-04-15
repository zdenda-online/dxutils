package cz.d1x.dxutils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Data storage is utility for storing any data and its later retrieval.
 * <p>
 * The typical usage is that data should be written to output stream via its {@link #getOutputStream()}
 * and later read via its {@link #getInputStream()}. The client should {@link #destroy()} the storage when finished with it
 * to release all the resources. The {@link #destroy()} can be used in combination with try-with-resource statement
 * as implementations are {@link AutoCloseable}.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see MemoryFileStorage
 */
public interface DataStorage extends AutoCloseable {

    /**
     * Gets output stream for writing data to the storage.
     * Note that client should close the stream when finished with writing to it.
     *
     * @return output stream for writing data to the storage.
     */
    OutputStream getOutputStream();

    /**
     * Gets input stream for reading data from the storage.
     * Note that client should close the stream when finished reading from it.
     *
     * @return input stream for reading data from the storage
     */
    InputStream getInputStream();

    /**
     * Destroys the storage and releases all resources held by it.
     */
    void destroy();
}
