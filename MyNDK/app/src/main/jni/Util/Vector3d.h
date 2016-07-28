#ifndef INC_123_VECTOR3D_H
#define INC_123_VECTOR3D_H

typedef struct _Vector3d
{
	double x;
	double y;
	double z;
}Vector3d, *pVector3d;

void set_Vector3d(pVector3d des, double xx, double yy, double zz);

void setComponent_Vector3d(pVector3d des, int i, double val);

void setZero_Vector3d(pVector3d des);

void copy_Vector3d(pVector3d des, const pVector3d sorce);

void scale_Vector3d(pVector3d des, double s);

double length_Vector3d(pVector3d des);

void normalize_Vector3d(pVector3d des);

double dot_Vector3d(pVector3d a, pVector3d b);

int sameValues_Vector3d(const pVector3d a, const pVector3d b);

void add_Vector3d(pVector3d a, pVector3d b, pVector3d result);

void sub_Vector3d(pVector3d a, pVector3d b, pVector3d result);

void cross_Vector3d(pVector3d a, pVector3d b, pVector3d result);

int largestAbsComponent_Vector3d(pVector3d v);

void ortho_Vector3d(pVector3d v, pVector3d result);

double maxNorm_Vector3d(const pVector3d v);

void  toString_Vector3d(const pVector3d v);

#endif
