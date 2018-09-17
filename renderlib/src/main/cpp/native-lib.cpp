//
// Created by SureshkumarKV on 11/10/2017.
//
#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>

extern "C" {
    JNIEXPORT void JNICALL Java_in_sureshkumarkv_renderlib_RbInstance_setSize(JNIEnv *env, jclass clazz, jobject surface, jint width, jint height) {
        ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
        ANativeWindow_setBuffersGeometry(window, width, height, 0);
    }
}
