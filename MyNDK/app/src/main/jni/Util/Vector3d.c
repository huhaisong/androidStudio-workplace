#include "Vector3d.h"
#include <math.h>
#include <stdio.h>
void set_Vector3d(pVector3d des, double xx, double yy, double zz)
{
	des->x = xx;
	des->y = yy;
	des->z = zz;
}

void setComponent_Vector3d(pVector3d des, int i, double val)
{
	if (i == 0)
	{
		des->x = val;
	}
	else if (i == 1)
	{
		des->y = val;
	}
	else
	{
		des->z = val;
	}
}

void setZero_Vector3d(pVector3d des)
{
	des->x = 0;
	des->y = 0;
	des->z = 0;
}

void copy_Vector3d(pVector3d des, const pVector3d sorce)
{
	des->x = sorce->x;
	des->y = sorce->y;
	des->z = sorce->z;
}

void scale_Vector3d(pVector3d des, double s)
{
	des->x *= s;
	des->y *= s;
	des->z *= s;
}

double length_Vector3d(pVector3d des)
{
	return sqrt(des->x *  des->x + des->y *  des->y + des->z *  des->z);
}

void normalize_Vector3d(pVector3d des)
{
	double d = length_Vector3d(des);
	if (d != 0)
	{
		scale_Vector3d(des, 1 / d);
	}
}

double dot_Vector3d(pVector3d a, pVector3d b)
{
	return a->x * b->x + a->y * b->y + a->z * b->z;
}

int sameValues_Vector3d(const pVector3d a, const pVector3d b)
{
	return (a->x == b->x) && (a->y == b->y) && (a->z == b->z);
}

void add_Vector3d(pVector3d a, pVector3d b, pVector3d result)
{
	set_Vector3d(result, a->x + b->x, a->y + b->y, a->z + b->z);
}

void sub_Vector3d(pVector3d a, pVector3d b, pVector3d result)
{
	set_Vector3d(result, a->x - b->x, a->y - b->y, a->z - b->z);
}

void cross_Vector3d(pVector3d a, pVector3d b, pVector3d result)
{
	set_Vector3d(result, a->y * b->z - a->z * b->y, a->z * b->x - a->x * b->z, a->x * b->y - a->y * b->x);
}

int largestAbsComponent_Vector3d(pVector3d v)
{
	double xAbs = (v->x) > 0 ? v->x : -(v->x);
	double yAbs = (v->y) > 0 ? v->y : -(v->y);
	double zAbs = (v->z) > 0 ? v->z : -(v->z);
	if (xAbs > yAbs)
	{
		if (xAbs > zAbs) {
			return 0;
		}
		return 2;
	}
	if (yAbs > zAbs) {
		return 1;
	}
	return 2;
}
void ortho_Vector3d(pVector3d v, pVector3d result)
{
	int k = largestAbsComponent_Vector3d(v) - 1;
	if (k < 0) {
		k = 2;
	}
	setZero_Vector3d(result);
	setComponent_Vector3d(result, k, 1);
	cross_Vector3d(v, result, result);
	normalize_Vector3d(result);
}

double maxNorm_Vector3d(const pVector3d v)
{
	double xAbs = (v->x) > 0 ? v->x : -(v->x);
	double yAbs = (v->y) > 0 ? v->y : -(v->y);
	double zAbs = (v->z) > 0 ? v->z : -(v->z);
	if (xAbs > yAbs)
	{
		if (xAbs > zAbs) {
			return xAbs;
		}
		return zAbs;
	}
	if (yAbs > zAbs) {
		return yAbs;
	}
	return zAbs;
}

void  toString_Vector3d(const pVector3d v)
{
	printf("x:%lf y:%lf z:%lf", v->x, v->y, v->z);
}