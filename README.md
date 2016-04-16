DXUtils: Utilities
==================
Simple Java library with various utilities built purely on Java SE without any dependencies.

The utilities are mainly related to I/O streaming.

Maven dependency
----------------

```xml
<dependency>
   <groupId>cz.d1x</groupId>
   <artifactId>dxutils</artifactId>
   <version>0.2</version>
</dependency>
```

Features
--------

- **MemoryFileStorage** for storing data in memory if their size does not exceeds given threshold. If threshold is
reached, the data are automatically stored in backing file. Useful for applications that want to store data in memory
but may receive larger data occasionally.

- **SynchronizedDataStorage** for wrapping existing DataStorage implementations (e.g. MemoryFileStorage) to add
synchronization for all read/write operations.

Examples
--------
**MemoryFileStorage**
```java
DataStorage storage = new MemoryFileStorage(1000, new File("/tmp/backing.tmp"));

// write data to storage, if over 1kB it gets automatically stored to /tmp/backing.tmp
storage.getOutputStream(); // write via OutputStream, don't forget to os.close() !!
storage.write("string data"); // UTF-8
storage.write("string data", "UTF-8"); // custom encoding
storage.write(new byte[]{0x01, 0x02, 0x03});
storage.write(new ByteArrayInputStream(new byte[]{})); // consume any InputStream
// multiple writes append existing data

// read data from storage, automatically selects source (memory or file)
storage.getInputStream(); // read via InputStream, don't forget to is.close() !!
storage.readString(); // UTF-8
storage.readString("UTF-8"); // custom encoding
storage.readBytes();

// when you are finished with storage, you should clear/close it to release resources
storage.clear(); // releases memory or deletes /tmp/backing.tmp if created

// you can use try-with-resource as DataStorage implementations are AutoCloseable
try (DataStorage autoclosedStorage = new MemoryFileStorage()) {
    autoclosedStorage.write("This will also append data to storage");
}
```

**SynchronizedDataStorage**
```java
DataStorage storage = ... // any implementation (e.g. MemoryFileStorage)
DataStorage synchronizedStorage = new SynchronizedDataStorage(storage);

synchronizedStorage.write("first"); // from any thread
synchronizedStorage.write("second"); // from any different thread

String result = synchronizedStorage.readString(); // later on
// if implementation allows appending, result will be one of "firstsecond" or "secondfirst"
```
