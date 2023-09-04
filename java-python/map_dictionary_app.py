import jpype
import jpype.imports
from jpype.types import *

# Start a JVM making available all dependencies we collected
# and our class from ../arrow-java-playground/target/arrow-java-playground-1.0-SNAPSHOT.jar
jpype.startJVM(classpath=[ "../arrow-java-playground/target/*"])

MapValues = JClass('io.arrow.playground.python.MapValues')

map_values = MapValues()

map_values.createDictionary()
dictionary = map_values.getDictionary()
print("Dictionary", type(dictionary), dictionary)

from org.apache.arrow.vector.dictionary import Dictionary
from org.apache.arrow.vector.types.pojo import DictionaryEncoding;

field_vector = dictionary.getVector()
encoding = dictionary.getEncoding()    

import pyarrow.jvm
dict_values = pyarrow.jvm.array(field_vector)

import pyarrow as pa

def wrap_dictionary_encoding(encoding: DictionaryEncoding, value_type):
    index_type = pyarrow.jvm._from_jvm_int_type(encoding.getIndexType())
    return pa.dictionary(index_type, value_type)

pyarrow_dict_encoding = wrap_dictionary_encoding(encoding, dict_values.type)

pyarrow_dict = pa.array(dict_values.to_pylist(), pyarrow_dict_encoding)
print("PyArrow Dictionary: ", pyarrow_dict)

# Export the Python array through C Data
from pyarrow.cffi import ffi as arrow_c
c_array = arrow_c.new("struct ArrowArray*")
c_array_ptr = int(arrow_c.cast("uintptr_t", c_array))
pyarrow_dict.dictionary._export_to_c(c_array_ptr)

# Export the Schema of the Array through C Data
c_schema = arrow_c.new("struct ArrowSchema*")
c_schema_ptr = int(arrow_c.cast("uintptr_t", c_schema))
pyarrow_dict.dictionary.type._export_to_c(c_schema_ptr)


assert map_values.compareWithPython(c_array_ptr, c_schema_ptr)

del dict_values
