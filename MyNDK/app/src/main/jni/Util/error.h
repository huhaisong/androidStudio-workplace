//
// Created by 333 on 2016/6/30.
//

#ifndef INC_123_ERROR_H
#define INC_123_ERROR_H

#include <GLES2/gl2.h>
#include <android/log.h>

#define  LOG_TAG    "libgl2jni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static void printGLString(const char *name, GLenum s)
{
    const char *v = (const char *)glGetString(s);
    LOGI("GL %s = %s\n", name, v);
}

static void checkGlError(const char* op)
{
    GLint error = glGetError();
    for (; error; error = glGetError())
    {
        LOGI("after %s() glError (0x%x)\n", op, error);
    }
}

#endif //INC_123_ERROR_H
