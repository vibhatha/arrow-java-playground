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

## Compile C++ to Java

Here we have a simple example where we want to make sure we can execute an Arrow Java code packaged with Maven
to be used with another supportive module written in C++

The supportive module in C++ is basically going to invoke a method from the Java library. 
In this case we have `io.arrow.playground.cpp.HelloJava.java` class with a single method
`hello`. 

First we would do a maven build

```bash
mvn clean install
```

Then we need to write the C++ program which is going to use the built jar. 

In `java-cpp` folder, `CallJavaFromCpp.cpp` contains a simple program which calls the Java library. 
Once the jar is built in the previous phase, copy that jar to the `java-cpp/build`. Note that `build`
is a folder we create to compile our C++ code. 

```bash
cd java-cpp
mkdir build
cp ../arrow-java-playground/target/arrow-java-playground-1.0-SNAPSHOT.jar build/cpptojava.jar
```

`cd build`, then

```bash
cmake -DCMAKE_PREFIX_PATH=$CONDA_PREFIX ..
make
```

Run the programme

```bash
./call_java_from_cpp
```

Output

```bash
Hello
```
