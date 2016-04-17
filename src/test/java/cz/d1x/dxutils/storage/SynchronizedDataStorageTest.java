package cz.d1x.dxutils.storage;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tests {@link SynchronizedDataStorage} implementation.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 */
public class SynchronizedDataStorageTest {

    @Test
    public void concurrencyWorksForLiteralReadWrite() throws InterruptedException {
        final DataStorage delegate = new MemoryFileStorage(100 * 8 * 1024); // any implementation will do
        final DataStorage storage = new SynchronizedDataStorage(delegate);

        final int CONCURRENT_TRIES = 1000; // larger value for better check while lower for better speed
        for (int i = 0; i < CONCURRENT_TRIES; i++) {
            final CountDownLatch latch = new CountDownLatch(2);
            final String s1 = generateLongString('X', 10 * 8 * 1024); // something over default buffer size
            final String s2 = generateLongString('Y', 10 * 8 * 1024); // something over default buffer size

            Thread writeThread1 = new Thread() {
                @Override
                public void run() {
                    storage.write(s1);
                    latch.countDown();
                }
            };
            Thread writeThread2 = new Thread() {
                @Override
                public void run() {
                    storage.write(s2);
                    latch.countDown();
                }
            };
            Thread readThread = new Thread() {
                @Override
                public void run() {
                    String data = storage.readString();
                    Assert.assertTrue(data.equals(s1 + s2) || data.equals(s2 + s1) ||
                            data.equals(s1) || data.equals(s2) || data.equals(""));
                }
            };

            writeThread1.start();
            writeThread2.start();
            readThread.start();
            latch.await(); // wait for both threads
            String data = storage.readString();
            Assert.assertTrue(data.equals(s1 + s2) || data.equals(s2 + s1));
            storage.clear();
        }
    }

    @Test
    public void concurrencyWorksForStreams() throws InterruptedException {
        final DataStorage delegate = new MemoryFileStorage(100 * 8 * 1024); // any implementation will do
        final DataStorage storage = new SynchronizedDataStorage(delegate);

        final int CONCURRENT_TRIES = 1000; // larger value for better check while lower for better speed
        for (int i = 0; i < CONCURRENT_TRIES; i++) {
            final CountDownLatch latch = new CountDownLatch(2);
            final String s1 = generateLongString('X', 10 * 8 * 1024); // something over default buffer size
            final String s2 = generateLongString('Y', 10 * 8 * 1024); // something over default buffer size

            final AtomicBoolean everythingOk = new AtomicBoolean(true);
            Thread writeThread1 = new Thread() {
                @Override
                public void run() {
                    OutputStream os = storage.getOutputStream();
                    try {
                        os.write(s1.getBytes());
                    } catch (IOException e) {
                        everythingOk.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            };
            Thread writeThread2 = new Thread() {
                @Override
                public void run() {
                    OutputStream os = storage.getOutputStream();
                    try {
                        os.write(s2.getBytes());
                    } catch (IOException e) {
                        everythingOk.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            };
            Thread readThread = new Thread() {
                @Override
                public void run() {
                    String data = storage.readString();
                    if (!data.equals(s1 + s2) && !data.equals(s2 + s1) &&
                            !data.equals(s1) && !data.equals(s2) && !data.equals("")) {
                        everythingOk.set(false);
                    }
                }
            };

            writeThread1.start();
            writeThread2.start();
            readThread.start();
            latch.await(); // wait for both threads
            String data = storage.readString();
            Assert.assertTrue(data.equals(s1 + s2) || data.equals(s2 + s1));
            storage.clear();
        }
    }

    private String generateLongString(char character, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(character);
        }
        return sb.toString();
    }
}
