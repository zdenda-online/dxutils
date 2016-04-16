package cz.d1x.dxutils.storage;

import cz.d1x.dxutils.io.IORuntimeException;

import java.io.*;

/**
 * Base implementation of {@link DataStorage} that stores data in-memory as long as they does not grow over given
 * size threshold. Once the threshold is reached, the data gets automatically serialized to secondary storage.
 * <p>
 * If implementations require some additional clean-up logic, they should override {@link #clear()} and call
 * super.clear() after doing its clean-up.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see DataStorage
 * @see MemoryFileStorage
 */
public abstract class ThresholdStorage extends BaseDataStorage {

    private static final long DEFAULT_THRESHOLD = 5 * 1000 * 1000; // 5MB
    private final long sizeThreshold;

    private ByteArrayOutputStream memoryBytes = new ByteArrayOutputStream();
    private boolean thresholdReached = false;

    /**
     * Gets a secondary output stream for writing data when threshold is reached.
     *
     * @return secondary output stream
     * @throws IORuntimeException possible exception if stream cannot be retrieved
     */
    protected abstract OutputStream getSecondaryOutputStream() throws IORuntimeException;

    /**
     * Gets a secondary input stream for reading data when threshold is reached.
     *
     * @return secondary input stream
     * @throws IORuntimeException possible exception if stream cannot be retrieved
     */
    protected abstract InputStream getSecondaryInputStream() throws IORuntimeException;

    /**
     * Creates a new storage with default threshold of 5MB.
     */
    public ThresholdStorage() {
        this(DEFAULT_THRESHOLD);
    }

    /**
     * Creates a new storage with given threshold.
     *
     * @param sizeThreshold size threshold (in bytes) to drive switching to secondary storage.
     * @throws IllegalArgumentException possible exception if given threshold is not valid
     */
    public ThresholdStorage(long sizeThreshold) {
        super();
        if (sizeThreshold < 0 || sizeThreshold > (Integer.MAX_VALUE - 8)) {
            throw new IllegalArgumentException("Threshold must be value between 0 and (Integer.MAX_VALUE - 8)");
        }
        this.sizeThreshold = sizeThreshold;
    }

    /**
     * Gets output stream for writing data to the storage.
     * The stream automatically starts writing to memory and switches to secondary storage when threshold is reached.
     * Note that client should close the stream when finished with writing to it.
     * <p>
     * You can retrieve output stream multiple times while writing to it appends data
     * (if not destroyed by {@link #clear()}).
     *
     * @return output stream for writing data to the storage.
     */
    @Override
    public OutputStream getOutputStream() {
        return new ThresholdOutputStream();
    }

    /**
     * Gets input stream for reading data from the storage.
     * The stream automatically reads from memory or secondary storage depending on the data size in the storage.
     * Note that client should close the stream when finished reading from it.
     * <p>
     * Clients can create as many input streams as they want.
     *
     * @return input stream for reading data from the storage
     */
    @Override
    public InputStream getInputStream() {
        if (thresholdReached) {
            return getSecondaryInputStream();
        } else {
            return new ByteArrayInputStream(memoryBytes.toByteArray());
        }
    }

    @Override
    public void clear() {
        memoryBytes = new ByteArrayOutputStream();
    }

    /**
     * Internal class with whole write logic.
     */
    private final class ThresholdOutputStream extends OutputStream {

        private OutputStream secondaryOutputStream = null;

        @Override
        public void write(int b) throws IORuntimeException {
            if (secondaryOutputStream != null) {
                try {
                    secondaryOutputStream.write(b);
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            } else {
                thresholdReached = memoryBytes.size() + 1 >= sizeThreshold;
                if (thresholdReached) {
                    switchToSecondaryOutputStream();
                    write(b);
                } else {
                    memoryBytes.write(b);
                }
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IORuntimeException {
            if (secondaryOutputStream != null) {
                try {
                    secondaryOutputStream.write(b, off, len);
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            } else {
                thresholdReached = memoryBytes.size() + len >= sizeThreshold;
                if (thresholdReached) {
                    switchToSecondaryOutputStream();
                    write(b, off, len);
                } else {
                    memoryBytes.write(b, off, len);
                }
            }
        }

        @Override
        public void close() throws IORuntimeException {
            if (secondaryOutputStream != null) {
                try {
                    secondaryOutputStream.close();
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            }
        }

        private void switchToSecondaryOutputStream() {
            secondaryOutputStream = getSecondaryOutputStream();
            if (memoryBytes != null && memoryBytes.size() > 0) {
                try {
                    secondaryOutputStream.write(memoryBytes.toByteArray());
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
                memoryBytes = null; // free memory
            }
        }
    }
}
