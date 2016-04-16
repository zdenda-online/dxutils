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

OutputStream os = storage.getOutputStream();
// write data to storage, if over 1kB it gets automatically stored to /tmp/backing.tmp
os.close();
// optionally you can storage.getOutputStream() again, writing to it will append new data

InputStream is = storage.getInputStream();
// read data from storage
is.close();

storage.destroy(); // releases memory or deletes /tmp/backing.tmp if created

clear
try (DataStorage storage = new MemoryFileStorage(...)) {
   // there are also byte[] and String based methods for read/write oprations
   storage.write("This will also append data to storage");
}
```

**SynchronizedDataStorage**
```java
DataStorage storage = ... // any implementation (e.g. MemoryFileStorage)
DataStorage synchronizedStorage = new SynchronizedDataStorage(storage);
```
