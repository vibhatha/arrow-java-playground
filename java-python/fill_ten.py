import jpype
import jpype.imports
from jpype.types import *

# Start a JVM making available all dependencies we collected
# and our class from ../arrow-java-playground/target/arrow-java-playground-1.0-SNAPSHOT.jar
jpype.startJVM(classpath=[ "../arrow-java-playground/target/*"])

FillTen = JClass('io.arrow.playground.python.FillTen')

fill_ten = FillTen()

array = fill_ten.createArray()
print("ARRAY", type(array), array)

# Convert the proxied BigIntVector to an actual pyarrow array
import pyarrow.jvm
pyarray = pyarrow.jvm.array(array)
print("ARRAY", type(pyarray), pyarray)

from pyarrow.cffi import ffi as arrow_c
c_array = arrow_c.new("struct ArrowArray*")
c_array_ptr = int(arrow_c.cast("uintptr_t", c_array))
pyarray._export_to_c(c_array_ptr)

# Export the Schema of the Array through C Data
c_schema = arrow_c.new("struct ArrowSchema*")
c_schema_ptr = int(arrow_c.cast("uintptr_t", c_schema))
pyarray.type._export_to_c(c_schema_ptr)

print(fill_ten.compareArrays(c_array_ptr, c_schema_ptr))

del pyarray

