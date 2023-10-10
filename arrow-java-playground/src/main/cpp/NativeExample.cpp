#include <jni.h>
#include <iostream>
#include "io_arrow_playground_cpp_NativeExample.h"

JNIEXPORT void JNICALL Java_io_arrow_playground_cpp_NativeExample_nativeMethod(JNIEnv* env, jobject obj) {
    std::cout << "Called from C++" << std::endl;
}
