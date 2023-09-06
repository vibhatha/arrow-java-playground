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

int main() {
    JNIEnv *env;
    JavaVM *jvm;
    env = CreateVM(&jvm);
    if (env == nullptr) return EXIT_FAILURE;
    jclass javaClassToBeCalledByCpp = env->FindClass("io.arrow.playground.cpp.ToBeCalledByCpp");
    if (javaClassToBeCalledByCpp != nullptr) {
        jmethodID fillVector = env->GetStaticMethodID(javaClassToBeCalledByCpp,
                                                      "fillVector",
                                                      "(JJ)V");
        if (fillVector != nullptr) {
            struct ArrowSchema arrowSchema;
            struct ArrowArray arrowArray;
            std::cout << "\n<<<<< C++ to Java for Arrays >>>>>\n" << std::endl;
            env->CallStaticVoidMethod(javaClassToBeCalledByCpp, fillVector,
                                      static_cast<jlong>(reinterpret_cast<uintptr_t>(&arrowSchema)),
                                      static_cast<jlong>(reinterpret_cast<uintptr_t>(&arrowArray)));
            auto resultImportArray = arrow::ImportArray(&arrowArray, &arrowSchema);
            std::shared_ptr<arrow::Array> array = resultImportArray.ValueOrDie();
            std::cout << "[C++] Array: " << array->ToString() << std::endl;
        } else {
            std::cerr << "Could not find fillVector method\n" << std::endl;
            return EXIT_FAILURE;
        }
        jmethodID fillVectorSchemaRoot = env->GetStaticMethodID(javaClassToBeCalledByCpp,
                                                                "fillVectorSchemaRoot",
                                                                "(JJ)V");
        if (fillVectorSchemaRoot != nullptr) {
            struct ArrowSchema arrowSchema;
            struct ArrowArray arrowArray;
            std::cout << "\n<<<<< C++ to Java for RecordBatch >>>>>\n" << std::endl;
            env->CallStaticVoidMethod(javaClassToBeCalledByCpp, fillVectorSchemaRoot,
                                      static_cast<jlong>(reinterpret_cast<uintptr_t>(&arrowSchema)),
                                      static_cast<jlong>(reinterpret_cast<uintptr_t>(&arrowArray)));
            auto resultImportVectorSchemaRoot = arrow::ImportRecordBatch(&arrowArray, &arrowSchema);
            std::shared_ptr<arrow::RecordBatch> recordBatch = resultImportVectorSchemaRoot.ValueOrDie();
            std::cout << "[C++] RecordBatch: " << recordBatch->ToString() << std::endl;
        } else {
            std::cerr << "Could not find fillVectorSchemaRoot method\n" << std::endl;
            return EXIT_FAILURE;
        }
    } else {
        std::cout << "Could not find ToBeCalledByCpp class\n" << std::endl;
        return EXIT_FAILURE;
    }
    jvm->DestroyJavaVM();
    return EXIT_SUCCESS;
}