#include "Matrix3x3d.h"


void set_Matrix3x3d(pMatrix3x3d des, double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22)
{
	des[0] = m00;
	des[1] = m01;
	des[2] = m02;
	des[3] = m10;
	des[4] = m11;
	des[5] = m12;
	des[6] = m20;
	des[7] = m21;
	des[8] = m22;
}

void copy_Matrix3x3d(pMatrix3x3d des, pMatrix3x3d source)
{
	des[0] = source[0];
	des[1] = source[1];
	des[2] = source[2];
	des[3] = source[3];
	des[4] = source[4];
	des[5] = source[5];
	des[6] = source[6];
	des[7] = source[7];
	des[8] = source[8];
}

void setZero_Matrix3x3d(pMatrix3x3d des)
{
	des[0] = 0;
	des[1] = 0;
	des[2] = 0;
	des[3] = 0;
	des[4] = 0;
	des[5] = 0;
	des[6] = 0;
	des[7] = 0;
	des[8] = 0;
}

void setIdentity_Matrix3x3d(pMatrix3x3d des)
{
	des[1] = 0;
	des[2] = 0;
	des[3] = 0;
	des[5] = 0;
	des[6] = 0;
	des[7] = 0;

	des[0] = 1;
	des[4] = 1;
	des[8] = 1;
}

void setSameDiagonal_Matrix3x3d(pMatrix3x3d des, double d)
{
	des[0] = d;
	des[4] = d;
	des[8] = d;
}

double get_element_Matrix3x3d(pMatrix3x3d des, int row, int col)
{
	return des[3 * row + col];
}

void set_element_Matrix3x3d(pMatrix3x3d des, int row, int col, double value)
{
	des[3 * row + col] = value;
}

void getColumn_Matrix3x3d(pMatrix3x3d des, int col, pVector3d v)
{
	v->x = des[col];
	v->y = des[(col + 3)];
	v->z = des[(col + 6)];
}

void setColumn_Matrix3x3d(pMatrix3x3d des, int col, pVector3d v)
{
	des[col] = v->x;
	des[(col + 3)] = v->y;
	des[(col + 6)] = v->z;
}

void scale_Matrix3x3d(pMatrix3x3d des, double s)
{
	des[0] *= s;
	des[1] *= s;
	des[2] *= s;
	des[3] *= s;
	des[4] *= s;
	des[5] *= s;
	des[6] *= s;
	des[7] *= s;
	des[8] *= s;
}



void transpose_Matrix3x3d(pMatrix3x3d des)
{
	double tmp = des[1];
	des[1] = des[3];
	des[3] = tmp;

	tmp = des[2];
	des[2] = des[6];
	des[6] = tmp;

	tmp = des[5];
	des[5] = des[7];
	des[7] = tmp;
}

void Get_transpose_Matrix3x3d(pMatrix3x3d des, pMatrix3x3d source)
{
	double m1 = source[1];
	double m2 = source[2];
	double m5 = source[5];
	des[0] = source[0];
	des[1] = source[3];
	des[2] = source[6];
	des[3] = m1;
	des[4] = source[4];
	des[5] = source[7];
	des[6] = m2;
	des[7] = m5;
	des[8] = source[8];
}

void add_Matrix3x3d(pMatrix3x3d a, pMatrix3x3d b,pMatrix3x3d result)
{
	result[0]=a[0] + b[0];
	result[1]=a[1] + b[1];
	result[2]=a[2] + b[2];
	result[3]=a[3] + b[3];
	result[4]=a[4] + b[4];
	result[5]=a[5] + b[5];
	result[6]=a[6] + b[6];
	result[7]=a[7] + b[7];
	result[8]=a[8] + b[8];
}

void minus_Matrix3x3d(pMatrix3x3d a, pMatrix3x3d b,pMatrix3x3d result)
{
	result[0]=a[0] - b[0];
	result[1]=a[1] - b[1];
	result[2]=a[2] - b[2];
	result[3]=a[3] - b[3];
	result[4]=a[4] - b[4];
	result[5]=a[5] - b[5];
	result[6]=a[6] - b[6];
	result[7]=a[7] - b[7];
	result[8]=a[8] - b[8];
}

void mult_Matrix3x3d(pMatrix3x3d a, pMatrix3x3d b, pMatrix3x3d result)
{
double tmpresult[9] = {0};
	set_Matrix3x3d(tmpresult,
		a[0] * b[0] + a[1] * b[3] + a[2] * b[6],
		a[0] * b[1] + a[1] * b[4] + a[2] * b[7],
		a[0] * b[2] + a[1] * b[5] + a[2] * b[8],
		a[3] * b[0] + a[4] * b[3] + a[5] * b[6],
		a[3] * b[1] + a[4] * b[4] + a[5] * b[7],
		a[3] * b[2] + a[4] * b[5] + a[5] * b[8],
		a[6] * b[0] + a[7] * b[3] + a[8] * b[6],
		a[6] * b[1] + a[7] * b[4] + a[8] * b[7],
		a[6] * b[2] + a[7] * b[5] + a[8] * b[8]);
	copy_Matrix3x3d(result,tmpresult);
}

void mult_Vector_Matrix3x3d(pMatrix3x3d a, pVector3d v, pVector3d result)
{
	double x = a[0] * v->x + a[1] * v->y + a[2] * v->z;
	double y = a[3] * v->x + a[4] * v->y + a[5] * v->z;
	double z = a[6] * v->x + a[7] * v->y + a[8] * v->z;
	result->x = x;
	result->y = y;
	result->z = z;
}

//行列式的值
double determinant_Matrix3x3d(pMatrix3x3d des)
{
	return    des[0] * des[4] * des[8] - des[0] * des[7] * des[5] \
		+ des[1] * des[5] * des[6] - des[1] * des[3] * des[8]  \
		+ des[2] * des[3] * des[7] - des[2] * des[4] * des[6];
}

//逆矩阵
int inverse_Matrix3x3d(pMatrix3x3d des, pMatrix3x3d source)
{
	double d = determinant_Matrix3x3d(source);
	if (d == 0) {
		return 0;
	}
	double invdet = 1 / d;
	set_Matrix3x3d(des, (source[4] * source[8] - source[7] * source[5]) * invdet,
		-(source[1] * source[8] - source[2] * source[7]) * invdet,
		(source[1] * source[5] - source[2] * source[4]) * invdet,
		-(source[3] * source[8] - source[5] * source[6]) * invdet,
		(source[0] * source[8] - source[2] * source[6]) * invdet,
		-(source[0] * source[5] - source[3] * source[2]) * invdet,
		(source[3] * source[7] - source[6] * source[4]) * invdet,
		-(source[0] * source[7] - source[6] * source[1]) * invdet,
		(source[0] * source[4] - source[3] * source[1]) * invdet);

	return 1;
}

double maxNorm_Matrix3x3d(pMatrix3x3d des)
{
	int i;
	double maxVal = des[0] > 0 ? des[0] : -(des[0]);
	double tmp;
	for (i = 1; i < 9; i++) {
		tmp = des[i] >0 ? des[i] : -(des[i]);
		maxVal = (maxVal > tmp ? maxVal : tmp);
	}
	return maxVal;
}
