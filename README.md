DXUtils: Utilities
==================
Simple Java library with various utilities built purely on Java SE without any dependencies.

Maven dependency
----------------

```xml
<dependency>
   <groupId>cz.d1x</groupId>
   <artifactId>dxutils</artifactId>
   <version>0.1</version>
</dependency>
```

Features
--------

- **MemoryFileStorage** for storing data in memory if their size does not exceeds given threshold. If threshold is
reached, the data are automatically stored in backing file. Useful for applications that want to store data in memory
but may receive larger data occasionally.

Examples
--------
**MemoryFileStorage**
```java
DataStorage storage = new MemoryFileStorage(1000, new File("/tmp/backing.tmp"));

OutputStream os = storage.getOutputStream();
// write data to storage via os, if over 1kB it gets automatically stored to /tmp/backing.tmp
os.close();

InputStream is = storage.getInputStream();
// read data from storage
is.close();

storage.destroy(); // release resource when finished with storage

// optionally you can use try-with-resources that will automatically call destroy()
try (DataStorage storage = new MemoryFileStorage(...)) {
   // do work
}
```
