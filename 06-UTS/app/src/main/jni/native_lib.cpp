//
// Created by Toshiba on 10/18/2015.
//

#include <jni.h>
#include <string>
#include "image_processor.h"
#include "image_utils.h"

extern "C" {
JNIEXPORT void JNICALL
        Java_gatel_uts_NativeLib__1registerBitmap(JNIEnv *env, jclass type, jintArray pixels_,
                                                  jint width, jint height);

JNIEXPORT jintArray JNICALL
        Java_gatel_uts_NativeLib__1getFrequency(JNIEnv *env, jclass type);

JNIEXPORT jintArray JNICALL
        Java_gatel_uts_NativeLib__1getEqualizedImage(JNIEnv *env, jclass type);

JNIEXPORT jintArray JNICALL
        Java_gatel_uts_NativeLib__1getGrayscaleImage(JNIEnv *env, jclass type);

JNIEXPORT jintArray JNICALL
        Java_gatel_uts_NativeLib__1getBinaryImage(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
        Java_gatel_uts_NativeLib__1equalize(JNIEnv *env, jclass type,
                                            jint lowerThreshold, jint upperThreshold);

JNIEXPORT jintArray JNICALL
        Java_gatel_uts_NativeLib__1getEqualizedFrequency(JNIEnv *env, jclass type);
}

JNIEXPORT void JNICALL
Java_gatel_uts_NativeLib__1registerBitmap(JNIEnv *env, jclass type, jintArray pixels_, jint width,
                                        jint height) {
    jint *pixels = env->GetIntArrayElements(pixels_, NULL);

    processor::registerBitmap(pixels, width, height);

    env->ReleaseIntArrayElements(pixels_, pixels, 0);
}

JNIEXPORT jintArray JNICALL
Java_gatel_uts_NativeLib__1getFrequency(JNIEnv *env, jclass type) {
    jintArray jresult = env->NewIntArray(256);
    env->SetIntArrayRegion(jresult, 0, 256, processor::getFrequency());
    return jresult;
}

JNIEXPORT jintArray JNICALL
Java_gatel_uts_NativeLib__1getEqualizedImage(JNIEnv *env, jclass type) {
    int size = processor::getSize();
    int* result = (int*)malloc(size * sizeof(int));
    processor::getEqualizedImage(result);

    jintArray jresult = env->NewIntArray(size);
    env->SetIntArrayRegion(jresult, 0, size, result);
    return jresult;
}

JNIEXPORT jintArray JNICALL
Java_gatel_uts_NativeLib__1getGrayscaleImage(JNIEnv *env, jclass type) {
    int size = processor::getSize();
    int* result = (int*)malloc(size * sizeof(int));
    processor::getGrayscaleImage(result);

    jintArray jresult = env->NewIntArray(size);
    env->SetIntArrayRegion(jresult, 0, size, result);
    return jresult;
}

JNIEXPORT jintArray JNICALL
Java_gatel_uts_NativeLib__1getBinaryImage(JNIEnv *env, jclass type) {
    int size = processor::getSize();
    int* result = (int*)malloc(size * sizeof(int));
    processor::getBinaryImage(result);

    jintArray jresult = env->NewIntArray(size);
    env->SetIntArrayRegion(jresult, 0, size, result);
    return jresult;
}

JNIEXPORT void JNICALL
Java_gatel_uts_NativeLib__1equalize(JNIEnv *env, jclass type, jint lowerThreshold,
                                    jint upperThreshold) {
    processor::equalize((unsigned char)lowerThreshold, (unsigned char)upperThreshold);
}

JNIEXPORT jintArray JNICALL
Java_gatel_uts_NativeLib__1getEqualizedFrequency(JNIEnv *env, jclass type) {
    jintArray jresult = env->NewIntArray(256);
    env->SetIntArrayRegion(jresult, 0, 256, processor::getEqualizedFrequency());
    return jresult;
}