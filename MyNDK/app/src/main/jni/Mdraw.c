#include <jni.h>
#include <android/log.h>
#include <GLES2/gl2.h>
#include <stdio.h>
#include <stdlib.h>
#include "Mdraw.h"
#include "shader_code.h"
#include "Util/HeadTracker.h"
#include "Ball.h"
#define  LOG_TAG    "libgl2jni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static void printGLString(const char *name, GLenum s) {
	const char *v = (const char *)glGetString(s);
	LOGI("GL %s = %s\n", name, v);
}
static void checkGlError(const char* op) {
	for (GLint error = glGetError(); error; error
		= glGetError()) {
		LOGI("after %s() glError (0x%x)\n", op, error);
	}
}
GLuint loadShader(GLenum shaderType, const char* pSource) {
	GLuint shader = glCreateShader(shaderType);
	if (shader) {
		glShaderSource(shader, 1, &pSource, NULL);
		glCompileShader(shader);
		GLint compiled = 0;
		glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
		if (!compiled) {
			GLint infoLen = 0;
			glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
			if (infoLen) {
				char* buf = (char*)malloc(infoLen);
				if (buf) {
					glGetShaderInfoLog(shader, infoLen, NULL, buf);
					LOGE("Could not compile shader %d:\n%s\n",
						shaderType, buf);
					free(buf);
				}
				glDeleteShader(shader);
				shader = 0;
			}
		}
	}
	return shader;
}
GLuint createProgram(const char* pVertexSource, const char* pFragmentSource) {
	GLuint vertexShader = loadShader(GL_VERTEX_SHADER, pVertexSource);
	if (!vertexShader) {
		return 0;
	}

	GLuint pixelShader = loadShader(GL_FRAGMENT_SHADER, pFragmentSource);
	if (!pixelShader) {
		return 0;
	}

	GLuint program = glCreateProgram();
	if (program) {
		glAttachShader(program, vertexShader);
		checkGlError("glAttachShader");
		glAttachShader(program, pixelShader);
		checkGlError("glAttachShader");
		glLinkProgram(program);
		GLint linkStatus = GL_FALSE;
		glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
		if (linkStatus != GL_TRUE) {
			GLint bufLength = 0;
			glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
			if (bufLength) {
				char* buf = (char*)malloc(bufLength);
				if (buf) {
					glGetProgramInfoLog(program, bufLength, NULL, buf);
					LOGE("Could not link program:\n%s\n", buf);
					free(buf);
				}
			}
			glDeleteProgram(program);
			program = 0;
		}
	}
	return program;
}
BALL *myball = NULL;
GLuint gProgram;
GLuint gvPositionHandle;
GLuint gvTexCoorHandle;
GLuint gvMVPMatrixHandle;
GLuint* mTexture;
ImageInfo *mypng;
int num_png;
void set_png(ImageInfo *png, int numofpng)
{
	mypng = png;
	num_png = numofpng;
    mTexture =(GLuint*)malloc(sizeof(GLuint)*num_png);
	memset(mTexture,0,sizeof(GLuint)*num_png);
}
GLMmodel* mymodel = NULL;
void set_model(GLMmodel* model)
{
	mymodel = model;
}
void initGraphics()
{
	printGLString("Version", GL_VERSION);
	printGLString("Vendor", GL_VENDOR);
	printGLString("Renderer", GL_RENDERER);
	printGLString("Extensions", GL_EXTENSIONS);

	gProgram = createProgram(gVertexShader, gFragmentShader);
	if (!gProgram) {
		LOGE("Could not create program.");
		return ;
	}
	gvPositionHandle = glGetAttribLocation(gProgram, "vPosition");
	checkGlError("glGetAttribLocation");
	LOGI("glGetAttribLocation(\"vPosition\") = %d\n", gvPositionHandle);
	gvTexCoorHandle = glGetAttribLocation(gProgram, "vTexCoords");
	checkGlError("glGetAttribLocation");
	LOGI("glGetAttribLocation(\"vTexCoords\") = %d\n", gvTexCoorHandle);
	gvMVPMatrixHandle = glGetUniformLocation(gProgram, "uMVPMatrix");

	glEnable(GL_TEXTURE_2D);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glGenTextures(num_png, mTexture);
	int i;
	for(i = 0;i<num_png;i++) {
		glBindTexture(GL_TEXTURE_2D, mTexture[i]);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, (mypng+i)->imageWidth, (mypng+i)->imageHeight,
					 0, GL_RGBA, GL_UNSIGNED_BYTE, (mypng+i)->pixelData);
		free((mypng+i)->pixelData);
	}
	free(mypng);

	myball = esGenSphere();
}
GLfloat mMVPMatrix[16];
GLfloat mProjMatrix[16];// 4x4矩阵 投影用
GLfloat mCamMatrix[16]; // 摄像机位置朝向9参数矩阵
GLfloat mMMatrix[16]; // 3D变换矩阵
static float headView[16] = {1,0,0,0,
							   0,1,0,0,
							   0,0,1,0,
							   0,0,0,1};


HeadTracker myheadtracker;

static float width,height;
void setupGraphics(int w, int h) {
	float ratio = (float)w / (float)h;
	glViewport(0, 0, w, h);
	setIdentityM(mMMatrix);
	//scaleM(mMMatrix,2,2,2);
	setLookAtM(mCamMatrix, 0,400,1200, 0,0, 0, 0,1 ,0);
	perspectiveM(mProjMatrix,75.0f, ratio, 0.1, 1000.0f);
	//frustumM(mProjMatrix, -ratio*0.8f, ratio*1.2f, -1, 1, 0.1, 1000);
	//glEnable(GL_DEPTH_TEST);
	//glDisable(GL_CULL_FACE);
	width = w;
	height = h;
}
void renderFrame() {
	static float grey;
	grey += 0.01f;
	if (grey > 1.0f) {
		grey = 0.0f;
	}
	glClearColor(grey, grey, grey, 1.0f);
	checkGlError("glClearColor");
	glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	checkGlError("glClear");

	float tmpMatrix[16] = {0};
	multiplyMM(tmpMatrix,headView,mCamMatrix);
	multiplyMM(tmpMatrix,tmpMatrix,mMMatrix);
	//multiplyMM(tmpMatrix,mCamMatrix,mMMatrix);
	multiplyMM(mMVPMatrix,mProjMatrix,tmpMatrix);

	glUseProgram(gProgram);
	checkGlError("glUseProgram");
	//frustumM(tmpMatrix,0,800,0,600,0,1000);
	glUniformMatrix4fv(gvMVPMatrixHandle, 1, GL_FALSE, mMVPMatrix);
	glVertexAttribPointer(gvPositionHandle, 3, GL_FLOAT, GL_FALSE, 0, mymodel->vertices);
	glVertexAttribPointer(gvTexCoorHandle, 2, GL_FLOAT, GL_FALSE, 0, mymodel->texcoords);

	glEnableVertexAttribArray(gvPositionHandle);
	glEnableVertexAttribArray(gvTexCoorHandle);
	glActiveTexture(GL_TEXTURE0);
	GLMgroup * mygroup = mymodel->groups;
	int i = 0;
	while (mygroup)
	{
		glBindTexture(GL_TEXTURE_2D, mTexture[i++]);
		glViewport(0,0,width,height);
		glDrawElements(GL_TRIANGLES, mygroup->numtriangles*3, GL_UNSIGNED_INT, (GLvoid*)mygroup->index_texcoords);
		//glViewport(width/2,0,width/2,height);
		//glDrawElements(GL_TRIANGLES, mygroup->numtriangles*3, GL_UNSIGNED_INT, (GLvoid*)mygroup->index_texcoords);
		mygroup = mygroup->next;
	}
#if 0
	{
		glUniformMatrix4fv(gvMVPMatrixHandle, 1, GL_FALSE, mMVPMatrix);
		glVertexAttribPointer(gvPositionHandle, 3, GL_FLOAT, GL_FALSE, 0, myball->vertices);
		glVertexAttribPointer(gvTexCoorHandle, 2, GL_FLOAT, GL_FALSE, 0, myball->texCoords);

		glEnableVertexAttribArray(gvPositionHandle);
		glEnableVertexAttribArray(gvTexCoorHandle);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, mTexture[i]);
		glDrawElements(GL_TRIANGLES, myball->sum, GL_UNSIGNED_INT, (GLvoid*)myball->indices);
	}
#endif
}


JNIEXPORT void JNICALL
Java_a_myndk_Mndk_created(JNIEnv *env, jclass type, jint width, jint height)
{
	// TODO
	parse_apk();
	initGraphics();

}
JNIEXPORT void JNICALL
Java_a_myndk_Mndk_changed(JNIEnv *env, jclass type, jint width, jint height)
{
	// TODO
	setupGraphics(width, height);
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_step(JNIEnv *env, jclass type)
{
	// TODO
	renderFrame();
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_releaseAll(JNIEnv *env, jclass type) {

	// TODO
	glmDelete(mymodel);
	DeleteBall(myball);
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_init_1headtracker_1a(JNIEnv *env, jclass type) {

	// TODO
	init_HeadTracker(&myheadtracker);
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_getLastHeadView_1a(JNIEnv *env, jclass type, jint display_rotation,
									 jdouble secondsSinceLastGyroEvent) {

	// TODO
	getLastHeadView(&myheadtracker, display_rotation,headView, secondsSinceLastGyroEvent);
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_stopTracking_1a(JNIEnv *env, jclass type) {

	// TODO
	stopTracking(&myheadtracker);
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_resetTracker_1a(JNIEnv *env, jclass type) {

	// TODO
	resetTracker(&myheadtracker);
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_startTracking_1a(JNIEnv *env, jclass type) {

	// TODO
	startTracking(&myheadtracker);
}

JNIEXPORT void JNICALL
Java_a_myndk_Mndk_onSensorChanged_1a(JNIEnv *env, jclass type, jint sensor_type,
									 jfloatArray event_values_, jint length_values,
									 jlong timestamp) {
	jfloat *event_values = (*env)->GetFloatArrayElements(env, event_values_, NULL);

	// TODO
	onSensorChanged(&myheadtracker ,sensor_type,event_values,length_values,timestamp);
	(*env)->ReleaseFloatArrayElements(env, event_values_, event_values, 0);
}