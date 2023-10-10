#include <iostream>
#include <jni.h>

#include <arrow/api.h>
#include <arrow/c/bridge.h>

JNIEnv *CreateVM(JavaVM **jvm) {
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options[2];
    options[0].optionString = "-Djava.class.path=cpptojava.jar";
    options[1].optionString = "-DXcheck:jni:pedantic";
    vm_args.version = JNI_VERSION_1_8;
    vm_args.nOptions = 2;
    vm_args.options = options;
    int status = JNI_CreateJavaVM(jvm, (void **) &env, &vm_args);
    if (status < 0) {
        std::cerr << "\n<<<<< Unable to Launch JVM >>>>>\n" << std::endl;
        return nullptr;
    }
    return env;
}

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



int main() {
    JNIEnv *env;
    JavaVM *jvm;
    env = CreateVM(&jvm);
    if (env == nullptr) return EXIT_FAILURE;
    auto maybe_array = MakeDictionary();
    std::shared_ptr<arrow::Array> array;
    if (maybe_array.status().ok()) {
        array = maybe_array.ValueOrDie();
    }
    jclass javaClassMapValuesConsumer = env->FindClass("io/arrow/playground/python/MapValuesConsumer");
    if (javaClassMapValuesConsumer != nullptr) {
        jmethodID fillVector = env->GetStaticMethodID(javaClassMapValuesConsumer,
                                                      "update",
                                                      "(JJ)V");
        if (fillVector != nullptr) {
            struct ArrowSchema arrowSchema;
            struct ArrowArray arrowArray;
            auto status = arrow::ExportArray(*array, &arrowArray, &arrowSchema);
            if(!status.ok()) {
                std::cerr << "Failed to export Arrow Array" << std::endl;
            }
            std::cout << "\n<<<<< C++ to Java for Arrays >>>>>\n" << std::endl;
            env->CallStaticVoidMethod(javaClassMapValuesConsumer, fillVector,
                                      static_cast<jlong>(reinterpret_cast<uintptr_t>(&arrowSchema)),
                                      static_cast<jlong>(reinterpret_cast<uintptr_t>(&arrowArray)));
            auto resultImportArray = arrow::ImportArray(&arrowArray, &arrowSchema);
            std::shared_ptr<arrow::Array> array = resultImportArray.ValueOrDie();
            std::cout << "[C++] Array: " << array->ToString() << std::endl;
        } else {
            std::cerr << "Could not find update method\n" << std::endl;
            return EXIT_FAILURE;
        }
    } else {
        std::cout << "Could not find MapValuesConsumer class\n" << std::endl;
        return EXIT_FAILURE;
    }
    jvm->DestroyJavaVM();
    return EXIT_SUCCESS;
}