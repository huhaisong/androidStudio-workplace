//
// Created by 333 on 2016/7/27.
//
#include <math.h>
#include <string.h>
#include "Ball.h"
#define numSlices 200
#define PI 3.1415926
#define d 500

 BALL* esGenSphere()
 {
     BALL* myball = (BALL*) malloc(sizeof(BALL));
    int i;
    int j;
    int iidex = 0;
    int numParallels = numSlices / 2;
    int numVertices = (numParallels + 1) * (numSlices + 1);
    int numIndices = numParallels * numSlices * 6;
    float angleStep = (float) ((2.0f * PI) / ((float) numSlices));
    float* vertices = (float*)malloc(sizeof(float)*(numVertices * 3));
    float*texCoords = (float*)malloc(sizeof(float)*(numVertices * 2));
    int*indices = (int*)malloc(sizeof(int)*numIndices);

    for (i = 0; i < numParallels + 1; i++) {
        for (j = 0; j < numSlices + 1; j++) {
            int vertex = (i * (numSlices + 1) + j) * 3;
            vertices[vertex] = (float) (d * sin(angleStep * (float) i) * sin(angleStep * (float) j));
            vertices[vertex + 1] = (float) (d * cos(angleStep * (float) i));
            vertices[vertex + 2] = (float) (d * sin(angleStep * (float) i) * cos(angleStep * (float) j));

            int texIndex = (i * (numSlices + 1) + j) * 2;
            texCoords[texIndex] = 1.0f - (float) j / (float) numSlices;
            texCoords[texIndex + 1] = ((float) i / (float) numParallels);
        }
    }

    for (i = 0; i < numParallels; i++) {
        for (j = 0; j < numSlices; j++) {
            indices[iidex++] = (short) (i * (numSlices + 1) + j);
            indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + j);
            indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + (j + 1));

            indices[iidex++] = (short) (i * (numSlices + 1) + j);
            indices[iidex++] = (short) ((i + 1) * (numSlices + 1) + (j + 1));
            indices[iidex++] = (short) (i * (numSlices + 1) + (j + 1));
        }
    }
     myball->sum = numIndices;
     myball->vertices = vertices ;
     myball->texCoords = texCoords ;
     myball->indices = indices ;
     return myball;
 }
void DeleteBall(BALL * myball)
{
    if(myball)
    {
        free(myball->indices);
        free(myball->texCoords);
        free(myball->vertices);
    }
    free(myball);
}