import jpype
import jpype.imports
from jpype.types import JClass
import pyarrow as pa
from pyarrow.cffi import ffi as arrow_c

# Init the JVM and make MapValuesConsumer class available to Python.
jpype.startJVM(classpath=[ "../arrow-java-playground/target/*"])
java_c_package = jpype.JPackage("org").apache.arrow.c
MapValuesConsumer = JClass('io.arrow.playground.python.MapValuesConsumer')
CDataDictionaryProvider = JClass('org.apache.arrow.c.CDataDictionaryProvider')
RootAllocator = JClass('org.apache.arrow.memory.RootAllocator')

# Starting from Python and generating data

# Create a Python DictionaryArray

dictionary = pa.dictionary(pa.int64(), pa.utf8())
array = pa.array(["A", "B", "C", "A", "D"], dictionary)
print("From Python")
print("Dictionary Created: ", array)

# create the CDataDictionaryProvider instance which is
# required to create dictionary array precisely
c_provider = CDataDictionaryProvider()
root_allocator = RootAllocator()
consumer = MapValuesConsumer(c_provider, root_allocator)

# Export the Python array through C Data
c_array = arrow_c.new("struct ArrowArray*")
c_array_ptr = int(arrow_c.cast("uintptr_t", c_array))
array._export_to_c(c_array_ptr)

# Export the Schema of the Array through C Data
c_schema = arrow_c.new("struct ArrowSchema*")
c_schema_ptr = int(arrow_c.cast("uintptr_t", c_schema))
array.type._export_to_c(c_schema_ptr)

# Send Array and its Schema to the Java function
consumer.callToJava(c_array_ptr, c_schema_ptr)

# Importing values from Java to Python

# Export the Python array through C Data
c_array_from_java = arrow_c.new("struct ArrowArray*")
c_array_ptr_from_java = int(arrow_c.cast("uintptr_t", c_array_from_java))

# Export the Schema of the Array through C Data
c_schema_from_java = arrow_c.new("struct ArrowSchema*")
c_schema_ptr_from_java = int(arrow_c.cast("uintptr_t", c_schema_from_java))

java_wrapped_array = java_c_package.ArrowArray.wrap(c_array_ptr_from_java)
java_wrapped_schema = java_c_package.ArrowSchema.wrap(c_schema_ptr_from_java)

java_c_package.Data.exportVector(
    consumer.getAllocatorForJavaConsumer(),
    consumer.getVector(),
    c_provider,
    java_wrapped_array,
    java_wrapped_schema
)

print("From Java back to Python")
array_from_java = pa.Array._import_from_c(c_array_ptr_from_java, c_schema_ptr_from_java)

# In Java and Python, the same memory is being accessed through the C Data interface.
# Since the array from Java and array created in Python should have same data. 
assert array_from_java.equals(array)
print("Array from Java: ", array_from_java)

del array_from_java

consumer.close()
jpype.shutdownJVM()
