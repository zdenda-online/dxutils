package cz.d1x.dxutils.storage;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

/**
 * Tests {@link MemoryFileStorage} implementation.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 */
public class MemoryFileStorageTest {

    @Test
    public void onlyInMemoryWithDefaultFile() throws IOException {
        MemoryFileStorage storage = new MemoryFileStorage();

        byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertTempFileExists(false);
        storage.destroy();
    }

    @Test
    public void onlyInMemoryWithCustomFile() throws IOException {
        File tempFile = prepareCustomTempFile();
        MemoryFileStorage storage = new MemoryFileStorage(tempFile);

        byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertCustomFileExists(tempFile, false);
        storage.destroy();
    }

    @Test
    public void thresholdExceededCreatesCustomFile() throws IOException {
        File tempFile = prepareCustomTempFile();
        MemoryFileStorage storage = new MemoryFileStorage(10, tempFile);

        byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertCustomFileExists(tempFile, true);
        storage.destroy();
    }

    @Test
    public void thresholdExceededCreatesDefaultFile() throws IOException {
        MemoryFileStorage storage = new MemoryFileStorage();

        byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertTempFileExists(false);
        storage.destroy();
    }

    @Test
    public void onlyInMemoryAppendsData() throws IOException {
        MemoryFileStorage storage = new MemoryFileStorage();

        byte[] bytes = "These are some".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);
        byte[] bytes2 = " nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes2);

        assertDataInStorage(storage, "These are some nice bytes".getBytes(Charsets.UTF_8.name()));
        assertTempFileExists(false);
        storage.destroy();
    }

    @Test
    public void thresholdExceededAppendsDataToDefaultFile() throws IOException {
        MemoryFileStorage storage = new MemoryFileStorage(5);

        byte[] bytes = "These are some".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);
        byte[] bytes2 = " nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes2);

        assertDataInStorage(storage, "These are some nice bytes".getBytes(Charsets.UTF_8.name()));
        assertTempFileExists(true);
        storage.destroy();
    }

    @Test
    public void writeBytes() throws IOException {
        MemoryFileStorage storage = new MemoryFileStorage();

        storage.write("Hello".getBytes());

        assertDataInStorage(storage, "Hello".getBytes());
        assertTempFileExists(false);
        storage.destroy();
    }

    @Test
    public void writeUtf8String() throws IOException {
        MemoryFileStorage storage = new MemoryFileStorage();

        storage.write("Hello");

        assertDataInStorage(storage, "Hello".getBytes(Charsets.UTF_8.name()));
        assertTempFileExists(false);
        storage.destroy();
    }

    @Test
    public void readString() throws IOException {
        MemoryFileStorage storage = new MemoryFileStorage();

        storage.write("Hello");
        String str = storage.readString();

        Assert.assertEquals("Hello", str);
        assertTempFileExists(false);
        storage.destroy();
    }

    @Test
    public void autocloseDestroysFile() throws IOException {
        File tempFile = prepareCustomTempFile();
        try (MemoryFileStorage storage = new MemoryFileStorage(10, tempFile)) {
            byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
            writeToStorage(storage, bytes);
            assertCustomFileExists(tempFile, true);
        }
        assertCustomFileExists(tempFile, false); // should be destroyed by autoclose
    }

    @Test
    public void concurrencyWorks() throws IOException, InterruptedException {
        final MemoryFileStorage storage = new MemoryFileStorage(100 * 8 * 1024); // store in memory

        for (int i = 0; i < 1000; i++) {
            final CountDownLatch latch = new CountDownLatch(2);
            final String s1 = generateLongString('X', 10 * 8 * 1024); // something over default buffer size
            final String s2 = generateLongString('Y', 10 * 8 * 1024); // something over default buffer size

            Thread t1 = new Thread() {
                @Override
                public void run() {
                    storage.write(s1);
                    latch.countDown();
                }
            };
            Thread t2 = new Thread() {
                @Override
                public void run() {
                    storage.write(s2);
                    latch.countDown();
                }
            };

            t1.start();
            t2.start();
            latch.await(); // wait for both threads
            String data = storage.readString();
            Assert.assertTrue(data.equals(s1 + s2) || data.equals(s2 + s1));
            storage.destroy();
        }
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

    private void assertDataInStorage(MemoryFileStorage storage, byte[] expectedData) {
        byte[] actualData = readFromStorage(storage);
        Assert.assertArrayEquals(expectedData, actualData);
    }

    private void writeToStorage(MemoryFileStorage storage, byte[] data) {
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

    private byte[] readFromStorage(MemoryFileStorage storage) {
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

    private String generateLongString(char character, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(character);
        }
        return sb.toString();
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
}
