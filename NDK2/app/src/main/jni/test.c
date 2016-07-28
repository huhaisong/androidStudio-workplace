#include "com_example_a111_ndk2_MyNdk.h"

JNIEXPORT jstring JNICALL Java_com_example_a111_ndk2_MyNdk_getFromNDK
        (JNIEnv *env, jobject obj){
    return (*env)->NewStringUTF(env,"I am from NDK!");
}