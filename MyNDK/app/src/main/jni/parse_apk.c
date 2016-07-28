//
// Created by 333 on 2016/7/21.
//
#include <jni.h>
#include <string.h>
#include "Zip/zip.h"
#include "parse_apk.h"
#include "parse_png.h"
#include "parse_obj.h"
#include "Mdraw.h"

#define NUM_PNG 7

char png_name[7][100] = {
	"assets/0000.png",
	"assets/0001.png",
	"assets/0002.png",
	"assets/0003.png",
	"assets/0004.png",
	"assets/0005.png",
	"assets/bg.png"
};
char obj_name[] = "assets/st1.obj";
char *apkpath = NULL;


JNIEXPORT void JNICALL
Java_a_myndk_Mndk_Apkpath(JNIEnv *env, jclass type, jstring apkpath_) {
	const char *apk_path = (*env)->GetStringUTFChars(env, apkpath_, 0);
	// TODO
	apkpath = (char*)malloc(strlen(apk_path));
	memcpy(apkpath,apk_path,strlen(apk_path));
	(*env)->ReleaseStringUTFChars(env, apkpath_, apk_path);
}
void parse_apk()
{
	int i = 0;
	while(apkpath == NULL)
	{
		for(i = 0;i<1000;i++);
	}
	struct zip* apkArchive = zip_open(apkpath, 0, NULL);
	struct zip_stat fstat;
	zip_stat_init(&fstat);
	unsigned char * buffer = NULL;
	struct zip_file* file = NULL;
	//parse png begin
	ImageInfo *mypng = (ImageInfo*)malloc(sizeof(ImageInfo)*NUM_PNG);
	memset(mypng, 0, sizeof(ImageInfo)*NUM_PNG);
	for (i = 0; i < NUM_PNG; i++)
	{
		file = zip_fopen(apkArchive, png_name[i], 0);
		if (!file) {
			LOGE("Error opening %s from APK", NULL);
			return;
		}
		zip_stat(apkArchive, png_name[i], 0, &fstat);
		buffer = (unsigned char *)malloc(fstat.size + 1);
		int numBytesRead = zip_fread(file, buffer, fstat.size);
		zip_fclose(file);

		decodePNGFromStream(mypng + i, buffer, fstat.size);
		free(buffer);
	}
	set_png(mypng, NUM_PNG);
	//parse png end

	/* parse obj begin*/
	file = zip_fopen(apkArchive, obj_name, 0);
	if (!file) {
		LOGE("Error opening %s from APK", NULL);
		return;
	}
	zip_stat(apkArchive,obj_name, 0, &fstat);
	buffer = (unsigned char *)malloc(fstat.size + 1);
	buffer[fstat.size] = 0;
	int numBytesRead = zip_fread(file, buffer, fstat.size);
	// /r/n in windows ,change /r/r
	i = 0;
	while (i++ <= fstat.size)
	{
		if (buffer[i] == '\r') buffer[i] = '\n';
	}
	GLMmodel* model;
	model = glmReadOBJ(buffer, fstat.size + 1, obj_name);
	free(buffer);
	zip_fclose(file);
	set_model(model);
	//parse obj end

	zip_close(apkArchive);
	free(apkpath);
}
