//
// Created by Toshiba on 10/18/2015.
//

#include <jni.h>
#include <string>
#include "image_equalizer.h"

extern "C" {
JNIEXPORT jbyteArray JNICALL
        Java_gatel_uts_NativeLib_convertToGrayscale(JNIEnv *env, jclass type, jintArray pixels_);

JNIEXPORT void JNICALL
        Java_gatel_uts_NativeLib_registerBitmapPixelsForEqualizer(JNIEnv *env, jclass type,
                                                                  jintArray pixels_, jint width,
                                                                  jint height);

JNIEXPORT jintArray JNICALL
        Java_gatel_uts_NativeLib_equalize(JNIEnv *env, jclass type, jbyte lowerThreshold,
                                          jbyte upperThreshold);


JNIEXPORT jintArray JNICALL
        Java_gatel_uts_NativeLib_getFrequency(JNIEnv *env, jclass type);

}

jbyteArray Java_gatel_uts_NativeLib_convertToGrayscale(JNIEnv *env, jclass type, jintArray pixels_) {
    jint *pixels = env->GetIntArrayElements(pixels_, NULL);
    int length = env->GetArrayLength(pixels_);

    jbyte* grayscalePixels = (jbyte*)malloc(length);
    for (int i = 0; i < length; ++i) {
        jint pixel = pixels[i];
        grayscalePixels[i] = (jbyte)(((pixel & 0xFF) + ((pixel >> 8) & 0xFF) + ((pixel >> 16) & 0xFF)) / 3);
    }
    jbyteArray result = env->NewByteArray(length);
    env->SetByteArrayRegion(result, 0, length, grayscalePixels);

    env->ReleaseIntArrayElements(pixels_, pixels, 0);
    return result;
}

void Java_gatel_uts_NativeLib_registerBitmapPixelsForEqualizer(JNIEnv *env, jclass type,
                                                          jintArray pixels_, jint width,
                                                          jint height) {
    jint *pixels = env->GetIntArrayElements(pixels_, NULL);

    equalizer::registerBitmap(pixels, width, height);

    env->ReleaseIntArrayElements(pixels_, pixels, 0);
}

jintArray Java_gatel_uts_NativeLib_equalize(JNIEnv *env, jclass type, jbyte lowerThreshold,
                                  jbyte upperThreshold) {

    int size = equalizer::getSize();
    int* result = (int*)malloc(size * sizeof(int));
    equalizer::equalize(lowerThreshold, upperThreshold, result);

    jintArray jresult = env->NewIntArray(size);
    env->SetIntArrayRegion(jresult, 0, size, result);
    return jresult;
}

jintArray Java_gatel_uts_NativeLib_getFrequency(JNIEnv *env, jclass type) {

    jintArray jresult = env->NewIntArray(256);
    env->SetIntArrayRegion(jresult, 0, 256, equalizer::getFrequency());
    return jresult;
}
