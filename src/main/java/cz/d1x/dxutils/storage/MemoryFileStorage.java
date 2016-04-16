package cz.d1x.dxutils.storage;

import cz.d1x.dxutils.io.IORuntimeException;

import java.io.*;

/**
 * Implementation of {@link DataStorage} that stores data in-memory as long as they does not grow over given
 * size threshold. Once the threshold is reached, the data gets automatically serialized to the backing file.
 * <p>
 * Multiple writes (by any of write operations including output stream) append the data. If you want to start-over, you
 * can {@link #clear()} the storage and start writing again.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see SynchronizedDataStorage
 * @see ThresholdStorage
 * @see DataStorage
 */
public class MemoryFileStorage extends ThresholdStorage {

    private final File backingFile;

    /**
     * Creates a new storage with backing file in temporary directory and default threshold of 5MB.
     *
     * @throws IllegalArgumentException possible exception if given threshold value is not valid or backing file
     *                                  in temporary directory cannot be created
     */
    public MemoryFileStorage() throws IllegalArgumentException {
        this(prepareTempFile());
    }

    /**
     * Creates a new storage with backing file in temporary directory and given threshold.
     *
     * @param sizeThreshold size threshold (in bytes) to drive switching from memory to file.
     * @throws IllegalArgumentException possible exception if given threshold value is not valid or backing file
     *                                  in temporary directory cannot be created
     */
    public MemoryFileStorage(long sizeThreshold) throws IllegalArgumentException {
        this(sizeThreshold, prepareTempFile());
    }

    /**
     * Creates a new storage with given backing file and default threshold of 5MB.
     *
     * @param backingFile file to be used for storing bytes if size exceeds 5MB
     * @throws IllegalArgumentException possible exception if given threshold value is not valid
     */
    public MemoryFileStorage(File backingFile) throws IllegalArgumentException {
        super();
        this.backingFile = backingFile;
    }

    /**
     * Creates a new storage with given backing file and given threshold.
     *
     * @param sizeThreshold size threshold (in bytes) to drive switching from memory to file.
     * @param backingFile   file to be used for storing bytes if size exceeds threshold
     * @throws IllegalArgumentException possible exception if given threshold value is not valid
     */
    public MemoryFileStorage(long sizeThreshold, File backingFile) throws IllegalArgumentException {
        super(sizeThreshold);
        this.backingFile = backingFile;
    }

    @Override
    protected OutputStream getSecondaryOutputStream() {
        try {
            return new FileOutputStream(backingFile, true);
        } catch (FileNotFoundException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    protected InputStream getSecondaryInputStream() {
        try {
            return new FileInputStream(backingFile);
        } catch (FileNotFoundException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Clears the memory and deletes backing file if exists.
     */
    @Override
    public void clear() {
        backingFile.delete();
        super.clear();
    }

    private static File prepareTempFile() {
        try {
            File tempFile = File.createTempFile("memory_file", null);
            tempFile.delete(); // delete it, I want to create it if needed
            return tempFile;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to create temporary file", e);
        }
    }
}
