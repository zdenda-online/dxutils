DXUtils: Utilities
==================
Simple Java library with various utilities built purely on Java SE without any dependencies.

...todo...

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

todo

- Immutable structures of algorithms for thread safety

- Extensible for custom implementations of algorithms or only specific parts of existing ones (e.g. key derivation
for encryption, custom combination of input text and salt prior to hashing...etc)

- Detailed javadoc for understanding what is happening under the hood

- Hashing algorithms **MD5**, **SHA1**, **SHA256**, **SHA512** and additional operations of **repeated hashing** 
and **salting** 

- Symmetric encryption algorithms **AES** and **Triple DES** with CBC, PKCS#5 padding and PBKDF2 for key derivation.
Both algorithms generate a new random initialization vector for every message and combine it with cipher text
into the output.

- Asymmetric encryption algorithm **RSA** with ECB and OAEP padding

- **SecureProperties** that extend *java.util.Properties* by adding possibility to store/read encrypted values

Examples
--------
**Hashing**
```java
HashingAlgorithm sha256 = HashingAlgorithms.sha256()
    .encoding("UTF-8") // optional, defaults to UTF-8
    .bytesRepresentation(...) // optional, defaults to lower-cased HEX
    .build();

// byte[] or String based methods
byte[] asBytes = sha256.hash(new byte[] {'h', 'e', 'l', 'l', 'o'});
String asString = sha256.hash("hello"); // 2cf24dba5fb0a...

// repeated hashing
HashingAlgorithm repeatedSha512 = HashingAlgorithms.sha512()
    .repeated(27)
    .build();
String repeated = repeatedSha512.hash("hello"); // hash(hash("hello")) ~ 27x

// salting (with default combining of input text and salt)
SaltedHashingAlgorithm saltedSha256 = HashingAlgorithms.sha256()
    .salted()
    .build();
String salted = saltedSha256.hash("your input text", "your salt");

// salting with custom combining of input text and salt
CombineAlgorithm combineAlg = ...; // your implementation
SaltedHashingAlgorithm customSaltedSha256 = HashingAlgorithms.sha256()
    .salted(combineAlg)
    .build();
```

**Symmetric Encryption**
```java
// AES
EncryptionAlgorithm aes = EncryptionAlgorithms.aes("secretPassphrase")
    .keySalt("saltForKeyDerivation") // optional
    .keyHashIterations(4096) // optional
    .combineSplitAlgorithm(...) // optional, how to combine/split IV and input
    .bytesRepresentation(...) // optional, defaults to lower-cased HEX
    .build();

byte[] asBytes = aes.encrypt(new byte[] {'h', 'e', 'l', 'l', 'o'});
byte[] andBack = aes.decrypt(asBytes);

// DES
EncryptionAlgorithm des = EncryptionAlgorithms.tripleDes("secret")
    .build(); // default key salt, iterations count and combine/split alg.

String asString = des.encrypt("hello");
String andBack = des.decrypt(asString);
```

**Asymmetric Encryption**
```java
// custom keys
BigInteger modulus = ...; // your modulus (n)
BigInteger publicExponent = ...; // your public exponent (e)
BigInteger privateExponent = ...; // your private exponent (d)
EncryptionAlgorithm customRsa = EncryptionAlgorithms.rsa()
        .publicKey(modulus, publicExponent)
        .privateKey(modulus, privateExponent)
        .build();
        
// generated keys
RSAKeysGenerator keysGen = new RSAKeysGenerator();
KeyPair keys = keysGen.generateKeys();
EncryptionAlgorithm genRsa = EncryptionAlgorithms.rsa()
        .keyPair(keys)
        .build();
```

**Secure Properties**
```java
EncryptionAlgorithm algorithm = ...; // your algorithm
SecureProperties props = new SecureProperties(algorithm);
props.setProperty("plainProperty", "imGoodBoy");
props.setEncryptedProperty("encryptedProperty", "myDirtySecret");

props.store(...);
// plainProperty=imGoodBoy
// encryptedProperty=bf165faf5067...

// automatic decryption of values
String decrypted = props.getProperty("encryptedProperty"); // "myDirtySecret"
String original = props.getOriginalProperty("encryptedProperty"); // bf165...
```