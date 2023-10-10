# arrow-java-playground

A sandbox to play with Arrow Java features.

## Compile 

```bash
mvn clean install
```

## Run a Sample Application with Arrow-Java

```bash
java -cp target/arrow-java-playground-1.0-SNAPSHOT.jar io.arrow.playground.App
```

## Run Memory Examples

### With debug mode to enable logs

#### No Memory Leak

```bash
java -Darrow.memory.debug.allocator=true -cp target/arrow-java-playground-1.0-SNAPSHOT.jar io.arrow.playground.MemoryApp --mode no-leak
```

#### Memory Leak

```bash
java -Darrow.memory.debug.allocator=true -cp target/arrow-java-playground-1.0-SNAPSHOT.jar io.arrow.playground.MemoryApp --mode leak
```

## Java-Python Roundtripping

### Dictionary Data

`MapValuesV2.java` and `map_values_v2.py` are the relevant interfaces. 

To see it in action, first build the project `mvn clean install` and then `cd java-python`. `python map_values_v2.py`

### JNI Example

```bash
mvn clean install
```

```bash
java -Djava.library.path=target -cp target/classes io.arrow.playground.cpp.NativeExample
```

Or

```bash
java -Djava.library.path=target -cp target/arrow-java-playground.jar io.arrow.playground.cpp.NativeExample
```

### JNI Arrow Example

```bash
mvn clean install
```

```bash
java -Djava.library.path=target -cp target/arrow-java-playground.jar io.arrow.playground.cpp.MapValues
```
