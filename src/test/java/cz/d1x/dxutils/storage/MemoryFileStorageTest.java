package cz.d1x.dxutils.storage;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Tests {@link MemoryFileStorage} implementation.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 */
public class MemoryFileStorageTest {

    @Test
    public void onlyInMemoryWithDefaultFile() {
        MemoryFileStorage storage = new MemoryFileStorage();

        byte[] bytes = utf8Bytes("These are some nice bytes");
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertTempFileExists(false);
        storage.clear();
    }

    @Test
    public void onlyInMemoryWithCustomFile() {
        File tempFile = prepareCustomTempFile();
        MemoryFileStorage storage = new MemoryFileStorage(tempFile);

        byte[] bytes = utf8Bytes("These are some nice bytes");
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertCustomFileExists(tempFile, false);
        storage.clear();
    }

    @Test
    public void thresholdExceededCreatesCustomFile() {
        File tempFile = prepareCustomTempFile();
        MemoryFileStorage storage = new MemoryFileStorage(10, tempFile);

        byte[] bytes = utf8Bytes("These are some nice bytes");
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertCustomFileExists(tempFile, true);
        storage.clear();
    }

    @Test
    public void thresholdExceededCreatesDefaultFile() {
        MemoryFileStorage storage = new MemoryFileStorage();

        byte[] bytes = utf8Bytes("These are some nice bytes");
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertTempFileExists(false);
        storage.clear();
    }

    @Test
    public void onlyInMemoryAppendsData() {
        MemoryFileStorage storage = new MemoryFileStorage();

        byte[] bytes = utf8Bytes("These are some");
        writeToStorage(storage, bytes);
        byte[] bytes2 = utf8Bytes(" nice bytes");
        writeToStorage(storage, bytes2);

        assertDataInStorage(storage, utf8Bytes("These are some nice bytes"));
        assertTempFileExists(false);
        storage.clear();
    }

    @Test
    public void thresholdExceededAppendsDataToDefaultFile() {
        MemoryFileStorage storage = new MemoryFileStorage(5);

        byte[] bytes = utf8Bytes("These are some");
        writeToStorage(storage, bytes);
        byte[] bytes2 = utf8Bytes(" nice bytes");
        writeToStorage(storage, bytes2);

        assertDataInStorage(storage, utf8Bytes("These are some nice bytes"));
        assertTempFileExists(true);
        storage.clear();
    }

    @Test
    public void writeBytes() {
        MemoryFileStorage storage = new MemoryFileStorage();

        storage.write("Hello".getBytes());

        assertDataInStorage(storage, "Hello".getBytes());
        assertTempFileExists(false);
        storage.clear();
    }

    @Test
    public void writeUtf8String() {
        MemoryFileStorage storage = new MemoryFileStorage();

        storage.write("Hello");

        assertDataInStorage(storage, utf8Bytes("Hello"));
        assertTempFileExists(false);
        storage.clear();
    }

    @Test
    public void readString() {
        MemoryFileStorage storage = new MemoryFileStorage();

        storage.write("Hello");
        String str = storage.readString();

        Assert.assertEquals("Hello", str);
        assertTempFileExists(false);
        storage.clear();
    }

    @Test
    public void autocloseDestroysFile() {
        File tempFile = prepareCustomTempFile();
        try (MemoryFileStorage storage = new MemoryFileStorage(10, tempFile)) {
            byte[] bytes = utf8Bytes("These are some nice bytes");
            writeToStorage(storage, bytes);
            assertCustomFileExists(tempFile, true);
        }
        assertCustomFileExists(tempFile, false); // should be destroyed by autoclose
    }

    private void assertCustomFileExists(File file, boolean exists) {
        Assert.assertTrue((exists && file.exists()) || (!exists && !file.exists()));
    }

    private void assertTempFileExists(boolean exists) {
        File[] files = new File(System.getProperty("java.io.tmpdir")).listFiles();
        for (File f : files) {
            if (f.getName().contains("memory_file")) {
                if (exists) {
                    return; // it should exist and is there!
                } else {
                    Assert.fail("memory_file should not exist in temp but " + f.getAbsolutePath() + " found!");
                }
            }
        }
    }

    private void assertDataInStorage(DataStorage storage, byte[] expectedData) {
        byte[] actualData = readFromStorage(storage);
        Assert.assertArrayEquals(expectedData, actualData);
    }

    private void writeToStorage(DataStorage storage, byte[] data) {
        OutputStream os = storage.getOutputStream();
        try {
            os.write(data);
        } catch (IOException e) {
            Assert.fail("IOException thrown " + e.getMessage());
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                Assert.fail("IOException thrown " + e.getMessage());
            }
        }
    }

    private byte[] readFromStorage(DataStorage storage) {
        InputStream is = storage.getInputStream();
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            Assert.fail("IOException thrown " + e.getMessage());
            return new byte[0]; // satisfy compiler
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Assert.fail("IOException thrown " + e.getMessage());
            }
        }
    }

    private File prepareCustomTempFile() {
        try {
            File f = File.createTempFile("data_storage_test", null);
            f.delete();
            return f;
        } catch (IOException e) {
            Assert.fail("IOException " + e.getMessage());
            return new File("."); // satisfy compiler
        }
    }

    private byte[] utf8Bytes(String str) {
        try {
            return str.getBytes(Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
