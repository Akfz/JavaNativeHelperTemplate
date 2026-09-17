#include <jni.h>
#include "v_akfz_NativeTest.h"

extern "C" JNIEXPORT jint JNICALL
Java_v_akfz_NativeTest_add(JNIEnv* env, jobject obj, jint a, jint b) {
    return a + b;
}