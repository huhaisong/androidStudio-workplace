//
// Created by 333 on 2016/7/21.
//

#ifndef MYNDK_PARSE_PNG_H
#define MYNDK_PARSE_PNG_H

#include <GLES2/gl2.h>
#include <android/log.h>
#include "Zip/zip.h"
#define  LOG_TAG    "libpng"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct {
    unsigned char* data;
    int size;
    int offset;
}ImageSource;

typedef struct {
    unsigned char* pixelData;
    int imageWidth;
    int imageHeight;
}ImageInfo;

void decodePNGFromStream(ImageInfo *imageInfo,const unsigned char* pixelData, const unsigned int dataSize);
#endif //MYNDK_PARSE_PNG_H
