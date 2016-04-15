package cz.d1x.dxutils;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MemoryFileBackedStorageTest {

    @Test
    public void onlyInMemory() throws IOException {
        File tempFile = File.createTempFile("data_storage_test", null);
        MemoryFileBackedStorage storage = new MemoryFileBackedStorage(tempFile);

        byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertTempFileExists(tempFile, false);
        storage.destroy();
    }

    @Test
    public void thresholdExceededCreatesAFile() throws IOException {
        File tempFile = File.createTempFile("tmp", null);
        MemoryFileBackedStorage storage = new MemoryFileBackedStorage(10, tempFile);

        byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
        writeToStorage(storage, bytes);

        assertDataInStorage(storage, bytes);
        assertTempFileExists(tempFile, true);
        storage.destroy();
    }

    @Test
    public void autocloseDestroysFile() throws IOException {
        File tempFile = File.createTempFile("tmp", null);
        try (MemoryFileBackedStorage storage = new MemoryFileBackedStorage(10, tempFile)) {
            byte[] bytes = "These are some nice bytes".getBytes(Charsets.UTF_8.name());
            writeToStorage(storage, bytes);
            assertTempFileExists(tempFile, true);
        }
        assertTempFileExists(tempFile, false); // should be destroyed by autoclose
    }

    private void assertTempFileExists(File file, boolean exists) {
        Assert.assertTrue((exists && file.exists()) || (!exists && !file.exists()));
    }

    private void assertDataInStorage(MemoryFileBackedStorage storage, byte[] expectedData) {
        byte[] actualData = readFromStorage(storage);
        Assert.assertArrayEquals(expectedData, actualData);
    }

    private void writeToStorage(MemoryFileBackedStorage storage, byte[] data) {
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

    private byte[] readFromStorage(MemoryFileBackedStorage storage) {
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
}
