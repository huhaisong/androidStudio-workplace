//
// Created by 333 on 2016/7/21.
//

#ifndef MYNDK_PARSE_OBJ_H
#define MYNDK_PARSE_OBJ_H

#include <GLES2/gl2.h>
#include <android/log.h>

#define  LOG_TAG    "libobj"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/* GLMmaterial: Structure that defines a material in a model.*/
typedef struct _GLMmaterial
{
    char* name;                   /* name of material */
    // GLfloat diffuse[4];           /* diffuse component 固有色-Kd*/
    // GLfloat ambient[4];           /* ambient component 材质的阴影色- Ka*/
    //  GLfloat specular[4];          /* specular component 高光色-用Ks*/
    //  GLfloat shininess;            /* specular exponent 带权高光色则用高光系数   Ns0-1000*/
    //  GLfloat  filter[3];           /*Filter transmittance 滤光透射率 Tf*/
    //  GLint lignting ;              /*lighting model 光照模型 illum*/
    //  GLfloat factor;               /*渐隐指数描述物体融入背景的数量d factor0.0-1.1*/
    //  GLfloat Sharpness_value;      /*本地反射贴图的清晰度Sharpness value0-1000*/
    //  GLfloat ptical_density;       /*ptical density材质表面的光密度，即折射值 Ni 0.001-10*/
    /*3.map_Ks -options args filename 
    为镜反射指定颜色纹理文件(.mpc)或程序纹理文件(.cxc)，
    或是一个位图文件。作用原理与可选参数与map_Ka同*/
    // a png file
} GLMmaterial;

/* GLMtriangle: Structure that defines a triangle in a model.*/

typedef struct _GLMtexture {
    char *name;
    GLuint id;                    /* OpenGL texture ID */
    GLfloat width;		/* width and height for texture coordinates */
    GLfloat height;
} GLMtexture;

/* GLMgroup: Structure that defines a group in a model.
 */
typedef struct _GLMgroup {
    char*   name;           /* name of this group */
    GLuint   numtriangles;   /* number of triangles in this group */

    GLuint* index_vertices;            /*index array of vertices  */
    GLuint* index_normals;             /*index array of normals */
    GLuint* index_texcoords;           /*index array of texture coordinates */
    GLfloat* face_noraml;               /*normals of the face*/
    GLMmaterial*   pmaterial;           /* index to material for group */
    struct _GLMgroup* next;             /* pointer to next group in model */
} GLMgroup;

/* GLMmodel: Structure that defines a model.
 */
typedef struct _GLMmodel {
    char*    pathname;            /* path to this model */
    char*    mtllibname;          /* name of the material library */

    GLuint   numvertices;         /* number of vertices in model */
    GLfloat* vertices;            /* array of vertices  */

    GLuint	 numtexcoords;		  /* number of texcoords in model */
    GLfloat* texcoords; 		  /* array of texture coordinates */

    GLuint   numnormals;          /* number of normals in model */
    GLfloat* normals;             /* array of normals */

    GLuint   numtriangles;    /* number of triangles in model */

    GLuint   nummaterials;    /* number of materials in model */
    GLMmaterial* materials;       /* array of materials */

    GLuint       numgroups;       /* number of groups in model */
    GLMgroup*    groups;          /* linked list of groups */

    GLfloat position[3];          /* position of the model */
} GLMmodel;


GLMmodel* glmReadOBJ(const char* buf, long size, char* filename);
GLvoid glmDelete(GLMmodel* model);
#endif //MYNDK_PARSE_OBJ_H
