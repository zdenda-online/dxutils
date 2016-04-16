package cz.d1x.dxutils.storage;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Tests {@link SynchronizedDataStorage} implementation.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 */
public class SynchronizedDataStorageTest {

    @Test
    public void concurrencyWorks() throws InterruptedException {
        final DataStorage deletagte = new MemoryFileStorage(100 * 8 * 1024); // nay implementation will do
        final DataStorage storage = new SynchronizedDataStorage(deletagte);

        final int CONCURRENT_TRIES = 1000; // larger value for better check while lower for better speed
        for (int i = 0; i < CONCURRENT_TRIES; i++) {
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
