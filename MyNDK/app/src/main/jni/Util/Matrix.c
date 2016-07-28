//
// Created by 333 on 2016/6/29.
//
#include "Matrix.h"
#include <math.h>
#include <string.h>

#define PI 3.1415926
#define I(_i, _j) ((_j)+ 4*(_i))
/**
 * Matrix math utilities. These methods operate on OpenGL ES format
 * matrices and vectors stored in float arrays.
 * <p>
 * Matrices are 4 x 4 column-vector matrices stored in column-major
 * order:
 * <pre>
 *  m[offset +  0] m[offset +  4] m[offset +  8] m[offset + 12]
 *  m[offset +  1] m[offset +  5] m[offset +  9] m[offset + 13]
 *  m[offset +  2] m[offset +  6] m[offset + 10] m[offset + 14]
 *  m[offset +  3] m[offset +  7] m[offset + 11] m[offset + 15]</pre>
 *
 * Vectors are 4 x 1 column vectors stored in order:
 * <pre>
 * v[offset + 0]
 * v[offset + 1]
 * v[offset + 2]
 * v[offset + 3]</pre>
 */

/**
 * Multiplies two 4x4 matrices together and stores the result in a third 4x4
 * matrix. In matrix notation: result = lhs x rhs. Due to the way
 * matrix multiplication works, the result matrix will have the same
 * effect as first multiplying by the rhs matrix, then multiplying by
 * the lhs matrix. This is the opposite of what you might expect.
 * <p>
 * The same float array may be passed for result, lhs, and/or rhs. However,
 * the result element values are undefined if the result elements overlap
 * either the lhs or rhs elements.
 *
 * @param result The float array that holds the result.
 * @param resultOffset The offset into the result array where the result is
 *        stored.
 * @param lhs The float array that holds the left-hand-side matrix.
 * @param lhsOffset The offset into the lhs array where the lhs is stored
 * @param rhs The float array that holds the right-hand-side matrix.
 * @param rhsOffset The offset into the rhs array where the rhs is stored.
 *
 * @throws IllegalArgumentException if result, lhs, or rhs are null, or if
 * resultOffset + 16 > result.length or lhsOffset + 16 > lhs.length or
 * rhsOffset + 16 > rhs.length.
 */
void multiplyMM(float* r, float* lhs, float* rhs)
{
	int i, j;
	for (i = 0; i < 4; i++)
	{
		const float rhs_i0 = rhs[I(i, 0)];
		float ri0 = lhs[I(0, 0)] * rhs_i0;
		float ri1 = lhs[I(0, 1)] * rhs_i0;
		float ri2 = lhs[I(0, 2)] * rhs_i0;
		float ri3 = lhs[I(0, 3)] * rhs_i0;
		for (j = 1; j < 4; j++)
		{
			const float rhs_ij = rhs[I(i, j)];
			ri0 += lhs[I(j, 0)] * rhs_ij;
			ri1 += lhs[I(j, 1)] * rhs_ij;
			ri2 += lhs[I(j, 2)] * rhs_ij;
			ri3 += lhs[I(j, 3)] * rhs_ij;
		}
		r[I(i, 0)] = ri0;
		r[I(i, 1)] = ri1;
		r[I(i, 2)] = ri2;
		r[I(i, 3)] = ri3;
	}
}


/**
 * Multiplies a 4 element vector by a 4x4 matrix and stores the result in a
 * 4-element column vector. In matrix notation: result = lhs x rhs
 * <p>
 * The same float array may be passed for resultVec, lhsMat, and/or rhsVec.
 * However, the resultVec element values are undefined if the resultVec
 * elements overlap either the lhsMat or rhsVec elements.
 *
 * @param resultVec The float array that holds the result vector.
 * @param resultVecOffset The offset into the result array where the result
 *        vector is stored.
 * @param lhsMat The float array that holds the left-hand-side matrix.
 * @param lhsMatOffset The offset into the lhs array where the lhs is stored
 * @param rhsVec The float array that holds the right-hand-side vector.
 * @param rhsVecOffset The offset into the rhs vector where the rhs vector
 *        is stored.
 *
 * @throws IllegalArgumentException if resultVec, lhsMat,
 * or rhsVec are null, or if resultVecOffset + 4 > resultVec.length
 * or lhsMatOffset + 16 > lhsMat.length or
 * rhsVecOffset + 4 > rhsVec.length.
 */
void multiplyMV(float* resultVec, float* lhsMat, float* rhsVec)
{
	int i, j;
	memset(resultVec, 0, sizeof(float)* 4);
	for (i = 0; i < 4; i++)
	{
		for (j = 0; j < 4; j++)
		{
			resultVec[i] += rhsVec[j] * lhsMat[i + j * 4];
		}
	}
}

/**
 * Transposes a 4 x 4 matrix.
 * <p>
 * mTrans and m must not overlap.
 *
 * @param mTrans the array that holds the output transposed matrix
 * @param mTransOffset an offset into mTrans where the transposed matrix is
 *        stored.
 * @param m the input array
 * @param mOffset an offset into m where the input matrix is stored.
 */
void transposeM(float* mTrans, float* m)
{
	int i;
	int mBase;
	for (i = 0; i < 4; i++)
	{
		mBase = i * 4;
		mTrans[i] = m[mBase];
		mTrans[i + 4] = m[mBase + 1];
		mTrans[i + 8] = m[mBase + 2];
		mTrans[i + 12] = m[mBase + 3];
	}
}

/**
 * Inverts a 4 x 4 matrix.
 * <p>
 * mInv and m must not overlap.
 *
 * @param mInv the array that holds the output inverted matrix
 * @param mInvOffset an offset into mInv where the inverted matrix is
 *        stored.
 * @param m the input array
 * @param mOffset an offset into m where the input matrix is stored.
 * @return true if the matrix could be inverted, false if it could not.
 */
int invertM(float* mInv, float* m) {
	// Invert a 4 x 4 matrix using Cramer's Rule
	float src0, src1, src2, src3, src4, src5, src6, src7, src8, src9, src10, src11, src12, src13, src14, src15;
	float atmp0, atmp1, atmp2, atmp3, atmp4, atmp5, atmp6, atmp7, atmp8, atmp9, atmp10, atmp11;
	float dst0, dst1, dst2, dst3, dst4, dst5, dst6, dst7, dst8, dst9, dst10, dst11, dst12, dst13, dst14, dst15;
	float btmp0, btmp1, btmp2, btmp3, btmp4, btmp5, btmp6, btmp7, btmp8, btmp9, btmp10, btmp11;

	float det;
	float invdet;

	// transpose matrix
	src0 = m[0];
	src4 = m[1];
	src8 = m[2];
	src12 = m[3];

	src1 = m[4];
	src5 = m[5];
	src9 = m[6];
	src13 = m[7];

	src2 = m[8];
	src6 = m[9];
	src10 = m[10];
	src14 = m[11];

	src3 = m[12];
	src7 = m[13];
	src11 = m[14];
	src15 = m[15];

	// calculate pairs for first 8 elements (cofactors)
	atmp0 = src10 * src15;
	atmp1 = src11 * src14;
	atmp2 = src9  * src15;
	atmp3 = src11 * src13;
	atmp4 = src9  * src14;
	atmp5 = src10 * src13;
	atmp6 = src8  * src15;
	atmp7 = src11 * src12;
	atmp8 = src8  * src14;
	atmp9 = src10 * src12;
	atmp10 = src8  * src13;
	atmp11 = src9  * src12;

	// calculate first 8 elements (cofactors)
	dst0 = (atmp0 * src5 + atmp3 * src6 + atmp4  * src7)
		- (atmp1 * src5 + atmp2 * src6 + atmp5  * src7);
	dst1 = (atmp1 * src4 + atmp6 * src6 + atmp9  * src7)
		- (atmp0 * src4 + atmp7 * src6 + atmp8  * src7);
	dst2 = (atmp2 * src4 + atmp7 * src5 + atmp10 * src7)
		- (atmp3 * src4 + atmp6 * src5 + atmp11 * src7);
	dst3 = (atmp5 * src4 + atmp8 * src5 + atmp11 * src6)
		- (atmp4 * src4 + atmp9 * src5 + atmp10 * src6);
	dst4 = (atmp1 * src1 + atmp2 * src2 + atmp5  * src3)
		- (atmp0 * src1 + atmp3 * src2 + atmp4  * src3);
	dst5 = (atmp0 * src0 + atmp7 * src2 + atmp8  * src3)
		- (atmp1 * src0 + atmp6 * src2 + atmp9  * src3);
	dst6 = (atmp3 * src0 + atmp6 * src1 + atmp11 * src3)
		- (atmp2 * src0 + atmp7 * src1 + atmp10 * src3);
	dst7 = (atmp4 * src0 + atmp9 * src1 + atmp10 * src2)
		- (atmp5 * src0 + atmp8 * src1 + atmp11 * src2);

	// calculate pairs for second 8 elements (cofactors)
	btmp0 = src2 * src7;
	btmp1 = src3 * src6;
	btmp2 = src1 * src7;
	btmp3 = src3 * src5;
	btmp4 = src1 * src6;
	btmp5 = src2 * src5;
	btmp6 = src0 * src7;
	btmp7 = src3 * src4;
	btmp8 = src0 * src6;
	btmp9 = src2 * src4;
	btmp10 = src0 * src5;
	btmp11 = src1 * src4;

	// calculate second 8 elements (cofactors)
	dst8 = (btmp0  * src13 + btmp3  * src14 + btmp4  * src15)
		- (btmp1  * src13 + btmp2  * src14 + btmp5  * src15);
	dst9 = (btmp1  * src12 + btmp6  * src14 + btmp9  * src15)
		- (btmp0  * src12 + btmp7  * src14 + btmp8  * src15);
	dst10 = (btmp2  * src12 + btmp7  * src13 + btmp10 * src15)
		- (btmp3  * src12 + btmp6  * src13 + btmp11 * src15);
	dst11 = (btmp5  * src12 + btmp8  * src13 + btmp11 * src14)
		- (btmp4  * src12 + btmp9  * src13 + btmp10 * src14);
	dst12 = (btmp2  * src10 + btmp5  * src11 + btmp1  * src9)
		- (btmp4  * src11 + btmp0  * src9 + btmp3  * src10);
	dst13 = (btmp8  * src11 + btmp0  * src8 + btmp7  * src10)
		- (btmp6  * src10 + btmp9  * src11 + btmp1  * src8);
	dst14 = (btmp6  * src9 + btmp11 * src11 + btmp3  * src8)
		- (btmp10 * src11 + btmp2  * src8 + btmp7  * src9);
	dst15 = (btmp10 * src10 + btmp4  * src8 + btmp9  * src9)
		- (btmp8  * src9 + btmp11 * src10 + btmp5  * src8);

	// calculate determinant
	det = src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3;

	if (det == 0.0f) {
		return 0;
	}

	// calculate matrix inverse
	invdet = 1.0f / det;
	mInv[0] = dst0  * invdet;
	mInv[1] = dst1  * invdet;
	mInv[2] = dst2  * invdet;
	mInv[3] = dst3  * invdet;

	mInv[4] = dst4  * invdet;
	mInv[5] = dst5  * invdet;
	mInv[6] = dst6  * invdet;
	mInv[7] = dst7  * invdet;

	mInv[8] = dst8  * invdet;
	mInv[9] = dst9  * invdet;
	mInv[10] = dst10 * invdet;
	mInv[11] = dst11 * invdet;

	mInv[12] = dst12 * invdet;
	mInv[13] = dst13 * invdet;
	mInv[14] = dst14 * invdet;
	mInv[15] = dst15 * invdet;

	return 1;
}

/**
 * Computes an orthographic projection matrix.
 *
 * @param m returns the result
 * @param mOffset
 * @param left
 * @param right
 * @param bottom
 * @param top
 * @param near
 * @param far
 */
void orthoM(float* m, float left, float right, float bottom, float top, float near, float far)
{
	/*
		if (left == right) {
		throw new IllegalArgumentException("left == right");
		}
		if (bottom == top) {
		throw new IllegalArgumentException("bottom == top");
		}
		if (near == far) {
		throw new IllegalArgumentException("near == far");
		}
		*/
	float r_width, r_height, r_depth, x, y, z, tx, ty, tz;

	r_width = 1.0f / (right - left);
	r_height = 1.0f / (top - bottom);
	r_depth = 1.0f / (far - near);
	x = 2.0f * (r_width);
	y = 2.0f * (r_height);
	z = -2.0f * (r_depth);
	tx = -(right + left) * r_width;
	ty = -(top + bottom) * r_height;
	tz = -(far + near) * r_depth;
	m[0] = x;
	m[5] = y;
	m[10] = z;
	m[12] = tx;
	m[13] = ty;
	m[14] = tz;
	m[15] = 1.0f;
	m[1] = 0.0f;
	m[2] = 0.0f;
	m[3] = 0.0f;
	m[4] = 0.0f;
	m[6] = 0.0f;
	m[7] = 0.0f;
	m[8] = 0.0f;
	m[9] = 0.0f;
	m[11] = 0.0f;
}


/**
 * Defines a projection matrix in terms of six clip planes.
 *
 * @param m the float array that holds the output perspective matrix
 * @param offset the offset into float array m where the perspective
 *        matrix data is written
 * @param left
 * @param right
 * @param bottom
 * @param top
 * @param near
 * @param far
 */
void frustumM(float* m, float left, float right, float bottom, float top, float near, float far)
{
	/*
if (left == right) {
throw new IllegalArgumentException("left == right");
}
if (top == bottom) {
throw new IllegalArgumentException("top == bottom");
}
if (near == far) {
throw new IllegalArgumentException("near == far");
}
if (near <= 0.0f) {
throw new IllegalArgumentException("near <= 0.0f");
}
if (far <= 0.0f) {
throw new IllegalArgumentException("far <= 0.0f");
}
*/
	float r_width, r_height, r_depth, x, y, A, B, C, D;
	r_width = 1.0f / (right - left);
	r_height = 1.0f / (top - bottom);
	r_depth = 1.0f / (near - far);
	x = 2.0f * (near * r_width);
	y = 2.0f * (near * r_height);
	A = (right + left) * r_width;
	B = (top + bottom) * r_height;
	C = (far + near) * r_depth;
	D = 2.0f * (far * near * r_depth);
	m[0] = x;
	m[5] = y;
	m[8] = A;
	m[9] = B;
	m[10] = C;
	m[14] = D;
	m[11] = -1.0f;
	m[1] = 0.0f;
	m[2] = 0.0f;
	m[3] = 0.0f;
	m[4] = 0.0f;
	m[6] = 0.0f;
	m[7] = 0.0f;
	m[12] = 0.0f;
	m[13] = 0.0f;
	m[15] = 0.0f;
}

/**
 * Defines a projection matrix in terms of a field of view angle, an
 * aspect ratio, and z clip planes.
 *
 * @param m the float array that holds the perspective matrix
 * @param offset the offset into float array m where the perspective
 *        matrix data is written
 * @param fovy field of view in y direction, in degrees
 * @param aspect width to height aspect ratio of the viewport
 * @param zNear
 * @param zFar
 */
void perspectiveM(float* m, float fovy, float aspect, float zNear, float zFar)
{
	float f;
	float rangeReciprocal;
	f = 1.0f / (float)tan(fovy * (PI / 360.0));
	rangeReciprocal = 1.0f / (zNear - zFar);

	m[0] = f / aspect;
	m[1] = 0.0f;
	m[2] = 0.0f;
	m[3] = 0.0f;

	m[4] = 0.0f;
	m[5] = f;
	m[6] = 0.0f;
	m[7] = 0.0f;

	m[8] = 0.0f;
	m[9] = 0.0f;
	m[10] = (zFar + zNear) * rangeReciprocal;
	m[11] = -1.0f;

	m[12] = 0.0f;
	m[13] = 0.0f;
	m[14] = 2.0f * zFar * zNear * rangeReciprocal;
	m[15] = 0.0f;
}

/**
 * Computes the length of a vector.
 *
 * @param x x coordinate of a vector
 * @param y y coordinate of a vector
 * @param z z coordinate of a vector
 * @return the length of a vector
 */
float length(float x, float y, float z)
{
	return (float)sqrt(x * x + y * y + z * z);
}

/**
 * Sets matrix m to the identity matrix.
 *
 * @param sm returns the result
 * @param smOffset index into sm where the result matrix starts
 */
void setIdentityM(float* sm) {
	int i;
	for (i = 0; i < 16; i++)
	{
		sm[i] = 0;
	}
	for (i = 0; i < 16; i += 5)
	{
		sm[i] = 1.0f;
	}
}

/**
 * Scales matrix m by x, y, and z, putting the result in sm.
 * <p>
 * m and sm must not overlap.
 *
 * @param sm returns the result
 * @param smOffset index into sm where the result matrix starts
 * @param m source matrix
 * @param mOffset index into m where the source matrix starts
 * @param x scale factor x
 * @param y scale factor y
 * @param z scale factor z
 */
void scaleMM(float* sm, float*m, float x, float y, float z)
{
	int i;
	for (i = 0; i < 4; i++)
	{
		int smi = i;
		int mi = i;
		sm[smi] = m[mi] * x;
		sm[4 + smi] = m[4 + mi] * y;
		sm[8 + smi] = m[8 + mi] * z;
		sm[12 + smi] = m[12 + mi];
	}
}

/**
 * Scales matrix m in place by sx, sy, and sz.
 *
 * @param m matrix to scale
 * @param mOffset index into m where the matrix starts
 * @param x scale factor x
 * @param y scale factor y
 * @param z scale factor z
 */
void scaleM(float* m, float x, float y, float z) {
	int i;
	int mi;
	for (i = 0; i < 4; i++)
	{
		mi = i;
		m[mi] *= x;
		m[4 + mi] *= y;
		m[8 + mi] *= z;
	}
}

/**
 * Translates matrix m by x, y, and z, putting the result in tm.
 * <p>
 * m and tm must not overlap.
 *
 * @param tm returns the result
 * @param tmOffset index into sm where the result matrix starts
 * @param m source matrix
 * @param mOffset index into m where the source matrix starts
 * @param x translation factor x
 * @param y translation factor y
 * @param z translation factor z
 */
void translateMM(float* tm, float* m, float x, float y, float z)
{
	int i;
	int mi;
	int tmi;
	for (i = 0; i < 12; i++)
	{
		tm[i] = m[i];
	}
	for (i = 0; i < 4; i++)
	{
		tmi = i;
		mi = i;
		tm[12 + tmi] = m[mi] * x + m[4 + mi] * y + m[8 + mi] * z + m[12 + mi];
	}
}

/**
 * Translates matrix m by x, y, and z in place.
 *
 * @param m matrix
 * @param mOffset index into m where the matrix starts
 * @param x translation factor x
 * @param y translation factor y
 * @param z translation factor z
 */
void translateM(float* m, float x, float y, float z)
{
	int i;
	for (i = 0; i < 4; i++)
	{
		int mi = +i;
		m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
	}
}

/**
 * Rotates matrix m by angle a (in degrees) around the axis (x, y, z).
 * <p>
 * m and rm must not overlap.
 *
 * @param rm returns the result
 * @param rmOffset index into rm where the result matrix starts
 * @param m source matrix
 * @param mOffset index into m where the source matrix starts
 * @param a angle to rotate in degrees
 * @param x X axis component
 * @param y Y axis component
 * @param z Z axis component
 */
void rotateMM(float* rm, float* m, float a, float x, float y, float z)
{
	float sTemp[16];

	// synchronized(sTemp) {
	setRotateM(sTemp, a, x, y, z);
	multiplyMM(rm, m, sTemp);
	// }
}

/**
 * Rotates matrix m in place by angle a (in degrees)
 * around the axis (x, y, z).
 *
 * @param m source matrix
 * @param mOffset index into m where the matrix starts
 * @param a angle to rotate in degrees
 * @param x X axis component
 * @param y Y axis component
 * @param z Z axis component
 */
void rotateM(float* m, float a, float x, float y, float z)
{
	float sTemp[16];
	float temp[16];
	// synchronized(sTemp) {
	setRotateM(sTemp, a, x, y, z);
	multiplyMM(temp, m, sTemp);
	// }
	memcpy(m, temp, sizeof(float)* 16);
}

/**
 * Creates a matrix for rotation by angle a (in degrees)
 * around the axis (x, y, z).
 * <p>
 * An optimized path will be used for rotation about a major axis
 * (e.g. x=1.0f y=0.0f z=0.0f).
 *
 * @param rm returns the result
 * @param rmOffset index into rm where the result matrix starts
 * @param a angle to rotate in degrees
 * @param x X axis component
 * @param y Y axis component
 * @param z Z axis component
 */
void setRotateM(float* rm, float a, float x, float y, float z)
{
	float s, c;
	rm[3] = 0;
	rm[7] = 0;
	rm[11] = 0;
	rm[12] = 0;
	rm[13] = 0;
	rm[14] = 0;
	rm[15] = 1;
	a *= (float)(PI / 180.0f);
	s = (float)sin(a);
	c = (float)cos(a);
	if (1.0f == x && 0.0f == y && 0.0f == z) {
		rm[5] = c;   rm[10] = c;
		rm[6] = s;   rm[9] = -s;
		rm[1] = 0;   rm[2] = 0;
		rm[4] = 0;   rm[8] = 0;
		rm[0] = 1;
	}
	else if (0.0f == x && 1.0f == y && 0.0f == z) {
		rm[0] = c;   rm[10] = c;
		rm[8] = s;   rm[2] = -s;
		rm[1] = 0;   rm[4] = 0;
		rm[6] = 0;   rm[9] = 0;
		rm[5] = 1;
	}
	else if (0.0f == x && 0.0f == y && 1.0f == z) {
		rm[0] = c;   rm[5] = c;
		rm[1] = s;   rm[4] = -s;
		rm[2] = 0;   rm[6] = 0;
		rm[8] = 0;   rm[9] = 0;
		rm[10] = 1;
	}
	else {
		float len = length(x, y, z);
		if (1.0f != len) {
			float recipLen = 1.0f / len;
			x *= recipLen;
			y *= recipLen;
			z *= recipLen;
		}
		float nc = 1.0f - c;
		float xy = x * y;
		float yz = y * z;
		float zx = z * x;
		float xs = x * s;
		float ys = y * s;
		float zs = z * s;
		rm[0] = x*x*nc + c;
		rm[4] = xy*nc - zs;
		rm[8] = zx*nc + ys;
		rm[1] = xy*nc + zs;
		rm[5] = y*y*nc + c;
		rm[9] = yz*nc - xs;
		rm[2] = zx*nc - ys;
		rm[6] = yz*nc + xs;
		rm[10] = z*z*nc + c;
	}
}

/**
 * Converts Euler angles to a rotation matrix.
 *
 * @param rm returns the result
 * @param rmOffset index into rm where the result matrix starts
 * @param x angle of rotation, in degrees
 * @param y angle of rotation, in degrees
 * @param z angle of rotation, in degrees
 */
void setRotateEulerM(float* rm, float x, float y, float z) {
	float cx, sx, cy, sy, cz, sz, cxsy, sxsy;
	x *= (float)(PI / 180.0f);
	y *= (float)(PI / 180.0f);
	z *= (float)(PI / 180.0f);
	cx = (float)cos(x);
	sx = (float)sin(x);
	cy = (float)cos(y);
	sy = (float)sin(y);
	cz = (float)cos(z);
	sz = (float)sin(z);
	cxsy = cx * sy;
	sxsy = sx * sy;

	rm[0] = cy * cz;
	rm[1] = -cy * sz;
	rm[2] = sy;
	rm[3] = 0.0f;

	rm[4] = cxsy * cz + cx * sz;
	rm[5] = -cxsy * sz + cx * cz;
	rm[6] = -sx * cy;
	rm[7] = 0.0f;

	rm[8] = -sxsy * cz + sx * sz;
	rm[9] = sxsy * sz + sx * cz;
	rm[10] = cx * cy;
	rm[11] = 0.0f;

	rm[12] = 0.0f;
	rm[13] = 0.0f;
	rm[14] = 0.0f;
	rm[15] = 1.0f;
}

/**
 * Defines a viewing transformation in terms of an eye point, a center of
 * view, and an up vector.
 *
 * @param rm returns the result
 * @param rmOffset index into rm where the result matrix starts
 * @param eyeX eye point X
 * @param eyeY eye point Y
 * @param eyeZ eye point Z
 * @param centerX center of view X
 * @param centerY center of view Y
 * @param centerZ center of view Z
 * @param upX up vector X
 * @param upY up vector Y
 * @param upZ up vector Z
 */
void setLookAtM(float* rm, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
{
	float fx, fy, fz;
	float rlf, rls;
	float sx, sy, sz;
	float ux, uy, uz;
	// See the OpenGL GLUT documentation for gluLookAt for a description
	// of the algorithm. We implement it in a straightforward way:

	fx = centerX - eyeX;
	fy = centerY - eyeY;
	fz = centerZ - eyeZ;

	// Normalize f
	rlf = 1.0f / length(fx, fy, fz);
	fx *= rlf;
	fy *= rlf;
	fz *= rlf;

	// compute s = f x up (x means "cross product")
	sx = fy * upZ - fz * upY;
	sy = fz * upX - fx * upZ;
	sz = fx * upY - fy * upX;

	// and normalize s
	rls = 1.0f / length(sx, sy, sz);
	sx *= rls;
	sy *= rls;
	sz *= rls;

	// compute u = s x f
	ux = sy * fz - sz * fy;
	uy = sz * fx - sx * fz;
	uz = sx * fy - sy * fx;

	rm[0] = sx;
	rm[1] = ux;
	rm[2] = -fx;
	rm[3] = 0.0f;

	rm[4] = sy;
	rm[5] = uy;
	rm[6] = -fy;
	rm[7] = 0.0f;

	rm[8] = sz;
	rm[9] = uz;
	rm[10] = -fz;
	rm[11] = 0.0f;

	rm[12] = 0.0f;
	rm[13] = 0.0f;
	rm[14] = 0.0f;
	rm[15] = 1.0f;

	translateM(rm, -eyeX, -eyeY, -eyeZ);
}

