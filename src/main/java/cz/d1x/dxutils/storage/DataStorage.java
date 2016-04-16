package cz.d1x.dxutils.storage;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Data storage is utility for storing any data and its later retrieval.
 * <p>
 * The typical usage is that data are written by {@link #getOutputStream()} or any direct method like {@link #write(byte[])}.
 * Later can be data read by {@link #getInputStream()} or any direct method like {@link #readBytes()}.
 * <p>
 * The logic of writing is implementation-specific. Typical behavior is that data can be appended to the storage by
 * multiple write operations but it is not guaranteed (read implementation javadoc). If you want to start-over, you can
 * call {@link #clear()}.
 * <p>
 * By default the implementations are not thread-safe but you can wrap any implementation by {@link SynchronizedDataStorage}
 * to achieve thread-safety.
 * <p>
 * The client should {@link #clear()} the storage when finished with it
 * to release all the resources. The {@link #clear()} is called automatically in combination with try-with-resource
 * statement as implementations are {@link AutoCloseable}.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see BaseDataStorage
 * @see ThresholdStorage
 * @see MemoryFileStorage
 * @see SynchronizedDataStorage
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
     * Stores given bytes to the storage.
     *
     * @param bytes bytes to be stored
     */
    void write(byte[] bytes);

    /**
     * Stores given string to the storage using UTF-8 encoding.
     *
     * @param data data to be stored
     */
    void write(String data);

    /**
     * Stores given string to the storage using given encoding.
     *
     * @param data     data to be stored
     * @param encoding encoding of the string
     */
    void write(String data, String encoding);

    /**
     * Writes data from given input stream to the storage.
     * Note that client is responsible for closing the stream (implementations should not close it).
     *
     * @param is input stream with data
     */
    void write(InputStream is);

    /**
     * Reads data from the storage and gives them as {@link String} representation in UTF-8 encoding.
     * Note that if data are larger than Integer.MAX_VALUE thus larger than {@link String} can carry, then
     * {@link OutOfMemoryError} is thrown.
     *
     * @return string representation of data in UTF-8
     */
    String readString();

    /**
     * Reads data from the storage and gives them as {@link String} representation in given encoding.
     * Note that if data are larger than Integer.MAX_VALUE thus larger than {@link String} can carry, then
     * {@link OutOfMemoryError} is thrown.
     *
     * @param encoding encoding for the string
     * @return string representation of data in given encoding
     */
    String readString(String encoding);

    /**
     * Reads data from the storage and gives them as byte array representation.
     * Note that if data are larger than Integer.MAX_VALUE thus larger than byte array can carry, then
     * {@link OutOfMemoryError} is thrown.
     *
     * @return bytes of data
     */
    byte[] readBytes();

    /**
     * Clears the storage and releases resources held by it.
     */
    void clear();

    /**
     * Clears the storage and releases resources held by it.
     */
    void close();
}
