#ifndef Matrix3x3d_h
#define Matrix3x3d_h

#include "Vector3d.h"
#include <math.h>

typedef double Matrix3x3d[9];
typedef double*  pMatrix3x3d;

void set_Matrix3x3d(pMatrix3x3d des, double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22);
void copy_Matrix3x3d(pMatrix3x3d des, pMatrix3x3d source);
void setZero_Matrix3x3d(pMatrix3x3d des);
void setIdentity_Matrix3x3d(pMatrix3x3d des);
void setSameDiagonal_Matrix3x3d(pMatrix3x3d des, double d);
double get_element_Matrix3x3d(pMatrix3x3d des, int row, int col);
void set_element_Matrix3x3d(pMatrix3x3d des, int row, int col, double value);
void getColumn_Matrix3x3d(pMatrix3x3d des, int col, pVector3d v);
void setColumn_Matrix3x3d(pMatrix3x3d des, int col, pVector3d v);
void scale_Matrix3x3d(pMatrix3x3d des, double s);
void transpose_Matrix3x3d(pMatrix3x3d des);
void Get_transpose_Matrix3x3d(pMatrix3x3d des, pMatrix3x3d source);
void add_Matrix3x3d(pMatrix3x3d a, pMatrix3x3d b, pMatrix3x3d result);
void minus_Matrix3x3d(pMatrix3x3d a, pMatrix3x3d b, pMatrix3x3d result);
void mult_Matrix3x3d(pMatrix3x3d a, pMatrix3x3d b, pMatrix3x3d result);
void mult_Vector_Matrix3x3d(pMatrix3x3d a, pVector3d v, pVector3d result);
double determinant_Matrix3x3d(pMatrix3x3d des);
int inverse_Matrix3x3d(pMatrix3x3d des, pMatrix3x3d source);
double maxNorm_Matrix3x3d(pMatrix3x3d des);

#endif
