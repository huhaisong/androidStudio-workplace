//
// Created by 333 on 2016/7/27.
//

#ifndef MNDK_BALL_H
#define MNDK_BALL_H

typedef struct _ball
{
    float sum;
    float *vertices;
    float *texCoords;
    int *indices;
}BALL;
BALL * esGenSphere();
void DeleteBall(BALL * myball);
#endif //MNDK_BALL_H
