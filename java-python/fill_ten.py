import jpype
import jpype.imports
from jpype.types import *

# Start a JVM making available all dependencies we collected
# and our class from ../arrow-java-playground/target/arrow-java-playground-1.0-SNAPSHOT.jar
jpype.startJVM(classpath=[ "../arrow-java-playground/target/*"])

FillTen = JClass('io.arrow.playground.python.FillTen')

array = FillTen.createArray()
print("ARRAY", type(array), array)

# Convert the proxied BigIntVector to an actual pyarrow array
import pyarrow.jvm
pyarray = pyarrow.jvm.array(array)
print("ARRAY", type(pyarray), pyarray)
del pyarray