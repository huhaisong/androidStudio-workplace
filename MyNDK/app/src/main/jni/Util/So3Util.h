#ifndef So3Util_h
#define So3Util_h

#include "Vector3d.h"
#include "Matrix3x3d.h"

typedef struct _So3Util
{
	Vector3d temp31;
	Vector3d sO3FromTwoVecN;
	Vector3d sO3FromTwoVecA;
	Vector3d sO3FromTwoVecB;
	Vector3d sO3FromTwoVecRotationAxis;
	Matrix3x3d sO3FromTwoVec33R1;
	Matrix3x3d sO3FromTwoVec33R2;
	Vector3d muFromSO3R2;
	Vector3d rotationPiAboutAxisTemp;
}So3Util, *pSo3Util;

void init_So3Util(pSo3Util des);
void sO3FromTwoVec(pSo3Util des, pVector3d a, pVector3d b, pMatrix3x3d result);
void rotationPiAboutAxis(pSo3Util des, pVector3d v, pMatrix3x3d result);
void sO3FromMu(pVector3d w, pMatrix3x3d result);
void muFromSO3(pSo3Util des, pMatrix3x3d so3, pVector3d result);
void rodriguesSo3Exp(pVector3d w, double kA, double kB, pMatrix3x3d result);
void generatorField(int i, pMatrix3x3d pos, pMatrix3x3d result);

#endif 
