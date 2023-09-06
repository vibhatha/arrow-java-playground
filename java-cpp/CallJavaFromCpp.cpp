#include <jni.h>
#include <iostream>

int main() {
    JavaVM* jvm;
    JNIEnv* env;

    // Load and initialize the JVM
    JavaVMInitArgs vm_args;
    JavaVMOption options[2];
    options[0].optionString = "-Djava.class.path=cpptojava.jar";
    options[1].optionString = "-DXcheck:jni:pedantic";
    vm_args.version = JNI_VERSION_1_8;
    vm_args.nOptions = 2;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = JNI_FALSE;

    JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);

    // Load the Java class
    jclass myJavaClass = env->FindClass("io/arrow/playground/cpp/HelloJava");
    if (myJavaClass == nullptr) {
        std::cerr << "ERROR: class not found";
        return 1;
    }

    // Get the method ID
    jmethodID myJavaMethod = env->GetStaticMethodID(myJavaClass, "hello", "()V");
    if (myJavaMethod == nullptr) {
        std::cerr << "ERROR: method not found";
        return 1;
    }

    // Call the Java method
    jstring message = env->NewStringUTF("Hello from C++");
    env->CallStaticVoidMethod(myJavaClass, myJavaMethod, message);

    // Destroy the JVM
    jvm->DestroyJavaVM();

    return 0;
}
