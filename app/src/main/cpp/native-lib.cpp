#include <jni.h>
#include <string>

extern "C" {
int bsdiff_main(int argc, const char *argv[]);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_maple_bsdiff_MainActivity_patch(JNIEnv *env, jobject instance, jstring oldApkPath_,
                                         jstring patch_, jstring newApkPath_) {
    const char *oldApk = env->GetStringUTFChars(oldApkPath_, 0);
    const char *patch = env->GetStringUTFChars(patch_, 0);
    const char *newApk = env->GetStringUTFChars(newApkPath_, 0);
    const char *argv[] = {"", oldApk, newApk, patch};
    bsdiff_main(4, argv);
    env->ReleaseStringUTFChars(oldApkPath_, oldApk);
    env->ReleaseStringUTFChars(patch_, patch);
    env->ReleaseStringUTFChars(newApkPath_, newApk);
}