import sys

import jpype
import pyarrow as pa
from pyarrow.cffi import ffi


class Bridge:
    def __init__(self):
        jpype.startJVM(classpath=[ "../arrow-java-playground/target/*"])
        self.java_allocator = jpype.JPackage(
            "org").apache.arrow.memory.RootAllocator(sys.maxsize)
        self.java_c = jpype.JPackage("org").apache.arrow.c
        
    def java_to_python_array(self, vector, dictionary_provider=None):
        print("java_to_python_array")
        print("\t Java")
        c_schema = ffi.new("struct ArrowSchema*")
        ptr_schema = int(ffi.cast("uintptr_t", c_schema))
        c_array = ffi.new("struct ArrowArray*")
        ptr_array = int(ffi.cast("uintptr_t", c_array))
        print("\t\tField Vector: ", type(vector), vector)
        self.java_c.Data.exportVector(self.java_allocator, vector, dictionary_provider, self.java_c.ArrowArray.wrap(
            ptr_array), self.java_c.ArrowSchema.wrap(ptr_schema))
        print("\t Python ")
        return pa.Array._import_from_c(ptr_array, ptr_schema)
    
    def java_to_python_schema(self, vector, dictionary_provider=None):
        print("java_to_python_schema")
        print("\t Java")
        c_schema = ffi.new("struct ArrowSchema*")
        ptr_schema = int(ffi.cast("uintptr_t", c_schema))
        c_array = ffi.new("struct ArrowArray*")
        ptr_array = int(ffi.cast("uintptr_t", c_array))
        print("\t\tField Vector: ", type(vector), vector)
        self.java_c.Data.exportVector(self.java_allocator, vector, dictionary_provider, self.java_c.ArrowArray.wrap(
            ptr_array), self.java_c.ArrowSchema.wrap(ptr_schema))
        print("\t Python ")
        new_schema = pa.Field._import_from_c(ptr_schema)
        print("\t\tnew schema ", new_schema)
        return new_schema
    
    def python_to_java_array(self, array, dictionary_provider=None):
        print("python_to_java_array")
        print("\t Python")
        c_schema = self.java_c.ArrowSchema.allocateNew(self.java_allocator)
        c_array = self.java_c.ArrowArray.allocateNew(self.java_allocator)
        array._export_to_c(c_array.memoryAddress(), c_schema.memoryAddress())
        print("\t Java")
        return self.java_c.Data.importVector(self.java_allocator, c_array, c_schema, dictionary_provider)
    
    def close(self):
        self.java_allocator.close()
    

class DictionaryRoundTrip:
    
    def __init__(self) -> None:
         self.bridge = Bridge()
    
    def round_trip_array(self, array_generator, check_metadata=True):
        original_arr = array_generator()
        print("Python")
        print("Original Array: ", original_arr)
        with self.bridge.java_c.CDataDictionaryProvider() as dictionary_provider, \
                self.bridge.python_to_java_array(original_arr, dictionary_provider) as vector:
            del original_arr
            new_schema = self.bridge.java_to_python_schema(vector, dictionary_provider)
            new_array = self.bridge.java_to_python_array(vector, dictionary_provider)
            print("Python")
            print("Array: ", new_array)

        expected = array_generator()
        assert expected.equals(new_array)
        if check_metadata:
            assert new_array.type.equals(expected.type, check_metadata=True)
                

dictionary_roundtrip = DictionaryRoundTrip()

dictionary_roundtrip.round_trip_array(
            lambda: pa.array(["a", "b", None, "d"], pa.dictionary(pa.int64(), pa.utf8())))
