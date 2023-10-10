// Targeted by JavaCPP version 1.5.7: DO NOT EDIT THIS FILE

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

public class CDataDictionaryJavaToCppExample extends CDataDictionaryJavaConfig {
    static { Loader.load(); }

// Parsed from CDataCppBridge.h

// #include <iostream>
// #include <arrow/api.h>
// #include <arrow/c/bridge.h>

public static native void FillInt64Array(@Cast("const uintptr_t") long c_schema_ptr, @Cast("const uintptr_t") long c_array_ptr);

public static native void UpdateDictionary(@Cast("const uintptr_t") long c_schema_ptr, @Cast("const uintptr_t") long c_array_ptr);


}
