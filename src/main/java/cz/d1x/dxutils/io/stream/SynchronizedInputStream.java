package cz.d1x.dxutils.io.stream;

import cz.d1x.dxutils.io.IORuntimeException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple decorator for {@link InputStream} that synchronizes all its methods via given mutex.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 */
public class SynchronizedInputStream extends InputStream {

    private final InputStream delegate;
    private final Object mutex;

    /**
     * Creates a new decorator that synchronizes all methods of given input stream by given mutex.
     *
     * @param delegate input stream to be synchronized
     * @param mutex    mutex used for synchronization
     */
    public SynchronizedInputStream(InputStream delegate, Object mutex) {
        this.delegate = delegate;
        this.mutex = mutex;
    }

    @Override
    public int read() throws IORuntimeException {
        synchronized (mutex) {
            try {
                return delegate.read();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public int read(byte[] b) throws IORuntimeException {
        synchronized (mutex) {
            try {
                return delegate.read(b);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IORuntimeException {
        synchronized (mutex) {
            try {
                return delegate.read(b, off, len);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public long skip(long n) throws IORuntimeException {
        synchronized (mutex) {
            try {
                return delegate.skip(n);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public int available() throws IORuntimeException {
        synchronized (mutex) {
            try {
                return delegate.available();
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

    @Override
    public void mark(int readlimit) {
        synchronized (mutex) {
            delegate.mark(readlimit);
        }
    }

    @Override
    public void reset() throws IORuntimeException {
        synchronized (mutex) {
            try {
                delegate.reset();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public boolean markSupported() {
        synchronized (mutex) {
            return delegate.markSupported();
        }
    }
}
