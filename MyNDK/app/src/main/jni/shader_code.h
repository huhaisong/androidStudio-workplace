//
// Created by 333 on 2016/7/25.
//

#ifndef MYNDK_SHADER_CODE_H
#define MYNDK_SHADER_CODE_H

#if 0
static const char gVertexShader[] =
        "attribute vec4 vPosition;\n"
                "attribute vec2 vTexCoords;\n"
                "varying vec2 colorVarying;\n"
                "void main() {\n"
                "  gl_Position = vPosition;\n"
                "  colorVarying = vTexCoords;\n"
                "}\n";

static const char gFragmentShader[] =
        "precision mediump float;\n"
                "varying vec2 colorVarying;\n"
                "uniform sampler2D sampler;\n"
                "void main() {\n"
                "  //gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n"
                "gl_FragColor = texture2D(sampler,colorVarying);\n"
                "}\n";
#else
static const char gVertexShader[] =
                "uniform mat4 uMVPMatrix;\n"
                "attribute vec3 vPosition;\n"
                "attribute vec2 vTexCoords;\n"
                "varying vec2 colorVarying;\n"
                "void main() {\n"
                "  gl_Position = uMVPMatrix * vec4(vPosition,1);\n"
                "  colorVarying = vTexCoords;\n"
                "}\n";

static const char gFragmentShader[] =
        "precision mediump float;\n"
                "varying vec2 colorVarying;\n"
                "uniform sampler2D sampler;\n"
                "void main() {\n"
               // " gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n"
                "gl_FragColor = texture2D(sampler,colorVarying);\n"
                "}\n";
#endif
#endif //MYNDK_SHADER_CODE_H
