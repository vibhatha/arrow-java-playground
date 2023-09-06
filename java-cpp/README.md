# Java-CPP

## Compile Programme

Compile the configuration classes

```bash
javac -cp ~/.m2/repository/org/bytedeco/javacpp/1.5.7/javacpp-1.5.7.jar CDataJavaConfig.java
java -jar ~/.m2/repository/org/bytedeco/javacpp/1.5.7/javacpp-1.5.7.jar CDataJavaConfig.java
java -jar ~/.m2/repository/org/bytedeco/javacpp/1.5.7/javacpp-1.5.7.jar CDataJavaToCppExample.java
```

Now let's compile the executable programme

```bash
javac -cp ".:../arrow-java-playground/target/arrow-java-playground-1.0-SNAPSHOT.jar" TestCDataInterface.java
java -cp ".:../arrow-java-playground/target/arrow-java-playground-1.0-SNAPSHOT.jar" TestCDataInterface
```