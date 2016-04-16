package cz.d1x.dxutils.storage;

import cz.d1x.dxutils.io.stream.SynchronizedInputStream;
import cz.d1x.dxutils.io.stream.SynchronizedOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Decorator for any {@link DataStorage} that makes all its read/write operations synchronized so any concurrent
 * threads won't interfere.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see DataStorage
 */
public class SynchronizedDataStorage implements DataStorage {

    private final DataStorage delegate;
    private final Object mutex;

    /**
     * Creates a new decorator that synchronizes given {@link DataStorage}.
     *
     * @param delegate storage to be synchronized
     */
    public SynchronizedDataStorage(DataStorage delegate) {
        this.delegate = delegate;
        this.mutex = this;
    }

    @Override
    public OutputStream getOutputStream() {
        return new SynchronizedOutputStream(delegate.getOutputStream(), mutex);
    }

    @Override
    public InputStream getInputStream() {
        return new SynchronizedInputStream(delegate.getInputStream(), mutex);
    }

    @Override
    public void write(byte[] bytes) {
        synchronized (mutex) {
            delegate.write(bytes);
        }
    }

    @Override
    public void write(String data) {
        synchronized (mutex) {
            delegate.write(data);
        }
    }

    @Override
    public void write(String data, String encoding) {
        synchronized (mutex) {
            delegate.write(data, encoding);
        }
    }

    @Override
    public void write(InputStream is) {
        synchronized (mutex) {
            delegate.write(is);
        }
    }

    @Override
    public String readString() {
        synchronized (mutex) {
            return delegate.readString();
        }
    }

    @Override
    public String readString(String encoding) {
        synchronized (mutex) {
            return delegate.readString(encoding);
        }
    }

    @Override
    public byte[] readBytes() {
        synchronized (mutex) {
            return delegate.readBytes();
        }
    }

    @Override
    public void clear() {
        synchronized (mutex) {
            delegate.clear();
        }
    }

    @Override
    public void close() {
        synchronized (mutex) {
            delegate.close();
        }
    }
}
