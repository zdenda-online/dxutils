package cz.d1x.dxutils.storage;

import cz.d1x.dxutils.io.IORuntimeException;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Base implementation that only adds few useful methods built on top of default methods from {@link DataStorage}.
 * <p>
 * The base implementation is not thread-safe but clients can use {@link SynchronizedDataStorage} to ensure thread-safety.
 *
 * @author Zdenek Obst, zdenek.obst-at-gmail.com
 * @see DataStorage
 * @see MemoryFileStorage
 */
public abstract class BaseDataStorage implements DataStorage {

    private static final int BUFFER_SIZE = 8 * 1024;

    public void write(byte[] bytes) throws IORuntimeException {
        write(new ByteArrayInputStream(bytes));
    }

    public void write(String data) throws IORuntimeException {
        write(data.getBytes(Charset.forName("UTF-8")));
    }

    public void write(String data, String encoding) throws IORuntimeException {
        write(data.getBytes(Charset.forName(encoding)));
    }

    public void write(InputStream is) throws IORuntimeException {
        try (OutputStream os = getOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public String readString() throws IORuntimeException {
        return new String(readBytes(), Charset.forName("UTF-8"));
    }

    public String readString(String encoding) throws IORuntimeException {
        return new String(readBytes(), Charset.forName(encoding));
    }

    public byte[] readBytes() throws IORuntimeException {
        try (InputStream is = getInputStream()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int len;
            byte[] data = new byte[BUFFER_SIZE];
            while ((len = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, len);
            }
            os.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public void close() {
        clear();
    }
}
