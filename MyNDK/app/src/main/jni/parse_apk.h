//
// Created by 333 on 2016/7/21.
//

#ifndef MYNDK_PARSE_APK_H
#define MYNDK_PARSE_APK_H

#include <android/log.h>

#define  LOG_TAG    "libapk"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


void parse_apk();
#endif //MYNDK_PARSE_APK_H
