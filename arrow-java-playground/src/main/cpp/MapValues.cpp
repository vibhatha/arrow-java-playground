#include <iostream>
#include <jni.h>

#include <arrow/api.h>
#include <arrow/c/bridge.h>
#include "io_arrow_playground_cpp_MapValues.h"


arrow::Result<std::shared_ptr<arrow::Array>> MakeDictionary() {
    arrow::DictionaryBuilder<arrow::Int8Type> builder;
    builder.Append(static_cast<int8_t>(1));
    builder.Append(static_cast<int8_t>(2));
    builder.Append(static_cast<int8_t>(1));
    builder.AppendNull();

    builder.length();
    builder.null_count();

    auto value_type = std::make_shared<arrow::Int8Type>();
    auto dict_type = arrow::dictionary(arrow::int8(), value_type);

    std::shared_ptr<arrow::Array> result;
    builder.Finish(&result);
    return result;
}

// Now modify the following method to accept a ptr to ArrowArray and ArrowSchema, use that to recreate the dictionary here, 
// then try to edit it and update values
JNIEXPORT void JNICALL Java_io_arrow_playground_cpp_MapValues_helloDictionary(JNIEnv* env, jobject obj) {
    auto maybe_array = MakeDictionary();
    if(maybe_array.status().ok()) {
        auto array = maybe_array.ValueOrDie();
        std::cout << "Dictionary Created In C++ : " << array->ToString() << std::endl; 
    }
}

JNIEXPORT void JNICALL Java_io_arrow_playground_cpp_MapValues_updateDictionary(JNIEnv* env, jobject obj, jlong j_array_ptr, jlong j_schema_ptr) {
    std::cout << "Update Dictionary" << std::endl;
    uintptr_t c_schema_ptr = static_cast<uintptr_t>(j_schema_ptr);
    uintptr_t c_array_ptr = static_cast<uintptr_t>(j_array_ptr);
    struct ArrowSchema* c_schema = reinterpret_cast<struct ArrowSchema*>(c_schema_ptr);
    struct ArrowArray* c_array = reinterpret_cast<struct ArrowArray*>(c_array_ptr);
    auto resultImportArray = arrow::ImportArray(c_array, c_schema);
    std::shared_ptr<arrow::Array> array = resultImportArray.ValueOrDie();
    std::cout << "[C++] Array: " << array->ToString() << std::endl;
}
