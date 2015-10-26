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


JNIEXPORT void JNICALL
        Java_gatel_uts_NativeLib__1setBinaryThreshold(JNIEnv *env, jclass type, jint threshold);

JNIEXPORT jobjectArray JNICALL
        Java_gatel_uts_NativeLib__1getBoundaries(JNIEnv *env, jclass type);

JNIEXPORT jint JNICALL
        Java_gatel_uts_NativeLib__1getBinaryThreshold(JNIEnv *env, jclass type);

JNIEXPORT jobjectArray JNICALL
        Java_gatel_uts_NativeLib__1getGrids(JNIEnv *env, jclass type);

JNIEXPORT jstring JNICALL
        Java_gatel_uts_NativeLib__1recognizePattern(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
        Java_gatel_uts_NativeLib__1registerPattern(JNIEnv *env, jclass type, jstring value_);
}

void Java_gatel_uts_NativeLib__1registerBitmap(JNIEnv *env, jclass type, jintArray pixels_, jint width,
                                        jint height) {
    jint *pixels = env->GetIntArrayElements(pixels_, NULL);

    processor::registerBitmap(pixels, width, height);

    env->ReleaseIntArrayElements(pixels_, pixels, 0);
}

jintArray Java_gatel_uts_NativeLib__1getFrequency(JNIEnv *env, jclass type) {
    jintArray jresult = env->NewIntArray(256);
    env->SetIntArrayRegion(jresult, 0, 256, processor::getFrequency());
    return jresult;
}

jintArray Java_gatel_uts_NativeLib__1getEqualizedImage(JNIEnv *env, jclass type) {
    int size = processor::getSize();
    int* result = (int*)malloc(size * sizeof(int));
    processor::getEqualizedImage(result);

    jintArray jresult = env->NewIntArray(size);
    env->SetIntArrayRegion(jresult, 0, size, result);
    return jresult;
}

jintArray Java_gatel_uts_NativeLib__1getGrayscaleImage(JNIEnv *env, jclass type) {
    int size = processor::getSize();
    int* result = (int*)malloc(size * sizeof(int));
    processor::getGrayscaleImage(result);

    jintArray jresult = env->NewIntArray(size);
    env->SetIntArrayRegion(jresult, 0, size, result);
    return jresult;
}

jintArray Java_gatel_uts_NativeLib__1getBinaryImage(JNIEnv *env, jclass type) {
    int size = processor::getSize();
    int* result = (int*)malloc(size * sizeof(int));
    processor::getBinaryImage(result);

    jintArray jresult = env->NewIntArray(size);
    env->SetIntArrayRegion(jresult, 0, size, result);
    return jresult;
}

void Java_gatel_uts_NativeLib__1equalize(JNIEnv *env, jclass type, jint lowerThreshold,
                                    jint upperThreshold) {
    processor::equalize((unsigned char)lowerThreshold, (unsigned char)upperThreshold);
}

jintArray Java_gatel_uts_NativeLib__1getEqualizedFrequency(JNIEnv *env, jclass type) {
    jintArray jresult = env->NewIntArray(256);
    env->SetIntArrayRegion(jresult, 0, 256, processor::getEqualizedFrequency());
    return jresult;
}

void Java_gatel_uts_NativeLib__1setBinaryThreshold(JNIEnv *env, jclass type, jint threshold) {
    processor::updateBinaryThreshold((unsigned char)threshold);
}

jobjectArray Java_gatel_uts_NativeLib__1getBoundaries(JNIEnv *env, jclass type) {

    std::vector<Component> components = processor::getComponents();
    int ncomponents = components.size();

    jclass intArrayClass = env->FindClass("[I");
    if (intArrayClass == NULL) {
        return NULL;
    }

    jobjectArray boundaries = env->NewObjectArray((jsize) ncomponents, intArrayClass, NULL);

    for (int i = 0; i < ncomponents; i++) {
        jintArray boundary = env->NewIntArray(4);
        int *boundaryArray = new int[4];
        boundaryArray[0] = components[i].boundary.first.X;
        boundaryArray[1] = components[i].boundary.first.Y;
        boundaryArray[2] = components[i].boundary.second.X;
        boundaryArray[3] = components[i].boundary.second.Y;
        env->SetIntArrayRegion(boundary, (jsize) 0, (jsize) 4, (jint*) boundaryArray);
        env->SetObjectArrayElement(boundaries, (jsize) i, boundary);
        env->DeleteLocalRef(boundary);
    }

    return boundaries;
}

jint Java_gatel_uts_NativeLib__1getBinaryThreshold(JNIEnv *env, jclass type) {
    return (jint)processor::getThreshold();
}

jobjectArray Java_gatel_uts_NativeLib__1getGrids(JNIEnv *env, jclass type) {

    std::vector<Component> components = processor::getComponents();
    int ncomponents = components.size();

    jclass intArrayClass = env->FindClass("[I");
    if (intArrayClass == NULL) {
        return NULL;
    }

    jobjectArray grids = env->NewObjectArray((jsize) ncomponents, intArrayClass, NULL);

    for (int i = 0; i < ncomponents; i++) {
        jintArray grid = env->NewIntArray(GRID_SIZE * GRID_SIZE);
        int *gridArray = new int[GRID_SIZE * GRID_SIZE];
        for (int x = 0; x < GRID_SIZE; ++x) {
            for (int y = 0; y < GRID_SIZE; ++y) {
                gridArray[x + y * GRID_SIZE] = components[i].grid[x][y];
            }
        }
        env->SetIntArrayRegion(grid, (jsize) 0, (jsize) GRID_SIZE * GRID_SIZE, (jint*) gridArray);
        env->SetObjectArrayElement(grids, (jsize) i, grid);
        env->DeleteLocalRef(grid);
    }

    return grids;
}

jstring Java_gatel_uts_NativeLib__1recognizePattern(JNIEnv *env, jclass type) {
    return env->NewStringUTF(processor::recognizePattern(recognizer::CHAIN_CODE).c_str());
}

JNIEXPORT void JNICALL
Java_gatel_uts_NativeLib__1registerPattern(JNIEnv *env, jclass type, jstring value_) {
    const char *value = env->GetStringUTFChars(value_, 0);

    processor::registerPattern(value);

    env->ReleaseStringUTFChars(value_, value);
}