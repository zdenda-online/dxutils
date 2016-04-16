package cz.d1x.dxutils.storage;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Base implementation that only adds few useful methods built on top of default methods from {@link DataStorage}.
 * <p>
 * The implementation is thread-safe so you can concurrently call read/write operations without risk.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see DataStorage
 * @see MemoryFileStorage
 */
public abstract class BaseDataStorage implements DataStorage {

    public static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Stores given bytes to the storage.
     *
     * @param bytes bytes to be stored
     */
    public void write(byte[] bytes) {
        write(new ByteArrayInputStream(bytes));
    }

    /**
     * Stores given string to the storage using UTF-8 encoding.
     *
     * @param data data to be stored
     */
    public void write(String data) {
        write(data.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Stores given string to the storage using given encoding.
     *
     * @param data     data to be stored
     * @param encoding encoding of the string
     */
    public void write(String data, String encoding) {
        write(data.getBytes(Charset.forName(encoding)));
    }

    /**
     * Writes data from given input stream to the storage.
     * Note that client is responsible for closing the stream (this method does not do that).
     *
     * @param is input stream with data
     */
    public synchronized void write(InputStream is) {
        try (OutputStream os = getOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new DataStorageException("Unable to copy input stream to output stream", e);
        }
    }

    /**
     * Reads data from the storage and gives them as {@link String} representation in UTF-8 encoding.
     * Note that if data are larger than Integer.MAX_VALUE thus larger than {@link String} can carry, then
     * {@link OutOfMemoryError} is thrown.
     *
     * @return string representation of data in UTF-8
     */
    public String readString() {
        return new String(readBytes(), Charset.forName("UTF-8"));
    }

    /**
     * Reads data from the storage and gives them as {@link String} representation in given encoding.
     * Note that if data are larger than Integer.MAX_VALUE thus larger than {@link String} can carry, then
     * {@link OutOfMemoryError} is thrown.
     *
     * @param encoding encoding for the string
     * @return string representation of data in given encoding
     */
    public String readString(String encoding) {
        return new String(readBytes(), Charset.forName(encoding));
    }

    /**
     * Reads data from the storage and gives them as byte array representation.
     * Note that if data are larger than Integer.MAX_VALUE thus larger than byte array can carry, then
     * {@link OutOfMemoryError} is thrown.
     *
     * @return bytes of data
     */
    public synchronized byte[] readBytes() {
        try (InputStream is = getInputStream()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int len;
            byte[] data = new byte[BUFFER_SIZE];
            while ((len = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, len);
            }
            os.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new DataStorageException("Unable to read from input stream", e);
        }
    }

    /**
     * Destroys the storage and releases all resources held by it.
     */
    public void close() {
        destroy();
    }
}
