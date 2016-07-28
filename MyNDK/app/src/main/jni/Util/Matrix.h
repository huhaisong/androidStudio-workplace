//
// Created by 333 on 2016/6/29.
//

#ifndef INC_123_MATRIX_H
#define INC_123_MATRIX_H


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
void multiplyMM(float* result,  float* lhs, float* rhs);

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
void multiplyMV(float* resultVec, float* lhsMat, float* rhsVec);

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
void transposeM(float* mTrans,float* m);

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
int invertM(float* mInv, float* m) ;

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
void orthoM(float* m,float left, float right, float bottom, float top,float near, float far);


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
void frustumM(float* m, float left, float right, float bottom, float top,float near, float far);

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
 void perspectiveM(float* m, float fovy, float aspect, float zNear, float zFar);

 /**
  * Computes the length of a vector.
  *
  * @param x x coordinate of a vector
  * @param y y coordinate of a vector
  * @param z z coordinate of a vector
  * @return the length of a vector
  */
 float length(float x, float y, float z);

 /**
  * Sets matrix m to the identity matrix.
  *
  * @param sm returns the result
  * @param smOffset index into sm where the result matrix starts
  */
 void setIdentityM(float* sm) ;

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
 void scaleMM(float* sm, float*m, float x, float y, float z);

 /**
  * Scales matrix m in place by sx, sy, and sz.
  *
  * @param m matrix to scale
  * @param mOffset index into m where the matrix starts
  * @param x scale factor x
  * @param y scale factor y
  * @param z scale factor z
  */
 void scaleM(float* m, float x, float y, float z) ;

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
 void translateMM(float* tm, float* m, float x, float y, float z);

 /**
  * Translates matrix m by x, y, and z in place.
  *
  * @param m matrix
  * @param mOffset index into m where the matrix starts
  * @param x translation factor x
  * @param y translation factor y
  * @param z translation factor z
  */
 void translateM(float* m, float x, float y, float z);

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
void rotateMM(float* rm,float* m,float a, float x, float y, float z);

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
 void rotateM(float* m, float a, float x, float y, float z) ;

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
 void setRotateM(float* rm, float a, float x, float y, float z);

 /**
  * Converts Euler angles to a rotation matrix.
  *
  * @param rm returns the result
  * @param rmOffset index into rm where the result matrix starts
  * @param x angle of rotation, in degrees
  * @param y angle of rotation, in degrees
  * @param z angle of rotation, in degrees
  */
 void setRotateEulerM(float* rm, float x, float y, float z) ;

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
void setLookAtM(float* rm, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) ;

#endif //INC_123_MATRIX_H
