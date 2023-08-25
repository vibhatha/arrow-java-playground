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