package cz.d1x.dxutils.io.stream;

import cz.d1x.dxutils.io.IORuntimeException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Simple decorator for {@link OutputStream} that synchronizes all its methods via given mutex.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see SynchronizedInputStream
 */
public class SynchronizedOutputStream extends OutputStream {

    private final OutputStream delegate;
    private final Object mutex;

    /**
     * Creates a new decorator that synchronizes all methods of given output stream by given mutex.
     *
     * @param delegate output stream to be synchronized
     * @param mutex    mutex used for synchronization
     */
    public SynchronizedOutputStream(OutputStream delegate, Object mutex) {
        this.delegate = delegate;
        this.mutex = mutex;
    }

    @Override
    public void write(int b) throws IORuntimeException {
        synchronized (mutex) {
            try {
                delegate.write(b);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public void write(byte[] b) throws IORuntimeException {
        synchronized (mutex) {
            try {
                delegate.write(b);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IORuntimeException {
        synchronized (mutex) {
            try {
                delegate.write(b, off, len);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public void flush() throws IORuntimeException {
        synchronized (mutex) {
            try {
                delegate.flush();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws IORuntimeException {
        synchronized (mutex) {
            try {
                delegate.close();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }
}
