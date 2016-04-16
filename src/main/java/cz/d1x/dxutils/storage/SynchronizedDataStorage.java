package cz.d1x.dxutils.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO
 */
public class SynchronizedDataStorage implements DataStorage {

    private final DataStorage delegate;
    private final Object mutex;

    public SynchronizedDataStorage(DataStorage delegate) {
        this.delegate = delegate;
        this.mutex = this;
    }

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream() {

            private OutputStream delegateOutputStream = delegate.getOutputStream();

            @Override
            public void write(int b) throws IOException {
                synchronized (mutex) {
                    delegateOutputStream.write(b);
                }
            }

            @Override
            public void write(byte[] b) throws IOException {
                synchronized (mutex) {
                    delegateOutputStream.write(b);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                synchronized (mutex) {
                    delegateOutputStream.write(b, off, len);
                }
            }

            @Override
            public void flush() throws IOException {
                synchronized (mutex) {
                    delegateOutputStream.flush();
                }
            }

            @Override
            public void close() throws IOException {
                synchronized (mutex) {
                    delegateOutputStream.close();
                }
            }
        };
    }

    @Override
    public InputStream getInputStream() {
        return new InputStream() {

            private InputStream delegateInputStream = delegate.getInputStream();

            @Override
            public int read() throws IOException {
                synchronized (mutex) {
                    return delegateInputStream.read();
                }
            }

            @Override
            public int read(byte[] b) throws IOException {
                synchronized (mutex) {
                    return delegateInputStream.read(b);
                }
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                synchronized (mutex) {
                    return delegateInputStream.read(b, off, len);
                }
            }

            @Override
            public long skip(long n) throws IOException {
                synchronized (mutex) {
                    return delegateInputStream.skip(n);
                }
            }

            @Override
            public int available() throws IOException {
                synchronized (mutex) {
                    return delegateInputStream.available();
                }
            }

            @Override
            public void close() throws IOException {
                synchronized (mutex) {
                    delegateInputStream.close();
                }
            }

            @Override
            public void mark(int readlimit) {
                synchronized (mutex) {
                    delegateInputStream.mark(readlimit);
                }
            }

            @Override
            public void reset() throws IOException {
                synchronized (mutex) {
                    delegateInputStream.reset();
                }
            }

            @Override
            public boolean markSupported() {
                synchronized (mutex) {
                    return delegateInputStream.markSupported();
                }
            }
        };
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
