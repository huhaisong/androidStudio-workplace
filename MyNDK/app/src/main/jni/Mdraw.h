//
// Created by c on 16-7-23.
//

#ifndef MNDK_MDRAW_H
#define MNDK_MDRAW_H


#include <jni.h>
#include "parse_png.h"
#include "parse_obj.h"
#include "parse_apk.h"
void set_png(ImageInfo *png,int numofpng);
void set_model(GLMmodel* model);


#if 0
const GLfloat gTriangleVertices[] = { 0.0f, 0.5f, -0.5f, -0.5f,
                                      0.5f, -0.5f };
const GLfloat gTexCoor[] = { 0, 0,
                             0, 1,
                             1, 1 };
#endif
#endif //MNDK_MDRAW_H
