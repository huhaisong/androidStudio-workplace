#ifndef OrientationEKF_h
#define OrientationEKF_h

#include "So3Util.h"


typedef struct OrientationEKF_
{
	double rotationMatrix[16];
	Matrix3x3d so3SensorFromWorld;
	Matrix3x3d so3LastMotion;
	Matrix3x3d mP;
	Matrix3x3d mQ;
	Matrix3x3d mR;
	Matrix3x3d mRaccel;
	Matrix3x3d mS;
	Matrix3x3d mH;
	Matrix3x3d mK;
	Vector3d mNu;
	Vector3d mz;
	Vector3d mh;
	Vector3d mu;
	Vector3d mx;
	Vector3d down;
	Vector3d north;
	long sensorTimeStampGyro;
	Vector3d lastGyro;
	double previousAccelNorm;
	double movingAverageAccelNormChange;
	float filteredGyroTimestep;
	int timestepFilterInit;
	int numGyroTimestepSamples;
	int gyroFilterValid;
	Matrix3x3d getPredictedGLMatrixTempM1;
	Matrix3x3d getPredictedGLMatrixTempM2;
	Vector3d getPredictedGLMatrixTempV1;
	Matrix3x3d setHeadingDegreesTempM1;
	Matrix3x3d processGyroTempM1;
	Matrix3x3d processGyroTempM2;
	Matrix3x3d processAccTempM1;
	Matrix3x3d processAccTempM2;
	Matrix3x3d processAccTempM3;
	Matrix3x3d processAccTempM4;
	Matrix3x3d processAccTempM5;
	Vector3d processAccTempV1;
	Vector3d processAccTempV2;
	Vector3d processAccVDelta;
	Vector3d processMagTempV1;
	Vector3d processMagTempV2;
	Vector3d processMagTempV3;
	Vector3d processMagTempV4;
	Vector3d processMagTempV5;
	Matrix3x3d processMagTempM1;
	Matrix3x3d processMagTempM2;
	Matrix3x3d processMagTempM4;
	Matrix3x3d processMagTempM5;
	Matrix3x3d processMagTempM6;
	Matrix3x3d updateCovariancesAfterMotionTempM1;
	Matrix3x3d updateCovariancesAfterMotionTempM2;
	Matrix3x3d accObservationFunctionForNumericalJacobianTempM;
	Matrix3x3d magObservationFunctionForNumericalJacobianTempM;
	int alignedToGravity;
	int alignedToNorth;
}OrientationEKF, *pOrientationEKF;

void init_OrientationEKF(pOrientationEKF des);
int isReady(pOrientationEKF des);
double getHeadingDegrees(pOrientationEKF des);
void setHeadingDegrees(pOrientationEKF des, double heading);
void getGLMatrix(double * result, pOrientationEKF des);

void getPredictedGLMatrix(double * result, pOrientationEKF des, double secondsAfterLastGyroEvent);
void getRotationMatrix(pMatrix3x3d result, pOrientationEKF des);
void arrayAssign(double data[3][3], pMatrix3x3d m);
int isAlignedToGravity(pOrientationEKF des);
int isAlignedToNorth(pOrientationEKF des);
void processGyro(pOrientationEKF des, pVector3d gyro, long sensorTimeStamp);
void updateAccelCovariance(pOrientationEKF des, double currentAccelNorm);
void processAcc(pOrientationEKF des, pVector3d acc, long sensorTimeStamp);
void processMag(pOrientationEKF des, float mag[], long sensorTimeStamp);
void glMatrixFromSo3(double * result, pOrientationEKF des, pMatrix3x3d so3);
void filterGyroTimestep(pOrientationEKF des, float timeStep);
void updateCovariancesAfterMotion(pOrientationEKF des);
void accObservationFunctionForNumericalJacobian(pOrientationEKF des, pMatrix3x3d so3SensorFromWorldPred, pVector3d result);
void magObservationFunctionForNumericalJacobian(pOrientationEKF des, pMatrix3x3d so3SensorFromWorldPred, pVector3d result);


#endif

