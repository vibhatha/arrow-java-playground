package io.arrow.playground.cpp;


public class NativeExample {
    static {
        System.loadLibrary("ArrowCpp");
    }

    public native void nativeMethod();

    public static void main(String[] args) {
        new NativeExample().nativeMethod();
    }
}

