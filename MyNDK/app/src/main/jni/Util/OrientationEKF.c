#include "OrientationEKF.h"
#include <string.h>
#include <math.h>
const float NS2S = 1.0E-9F;
const double MIN_ACCEL_NOISE_SIGMA = 0.75;
const double MAX_ACCEL_NOISE_SIGMA = 7.0;

void init_OrientationEKF(pOrientationEKF des)
{

	memset(des, 0, sizeof(OrientationEKF));
	des->previousAccelNorm = 0;
	des->movingAverageAccelNormChange = 0;

	des->timestepFilterInit = 0;
	des->gyroFilterValid = 1;

	des->sensorTimeStampGyro = 0L;

	setIdentity_Matrix3x3d(des->so3SensorFromWorld);
	setIdentity_Matrix3x3d(des->so3LastMotion);

	double initialSigmaP = 5.0;
	setZero_Matrix3x3d(des->mP);
	setSameDiagonal_Matrix3x3d(des->mP, 25);

	double initialSigmaQ = 1.0;
	setZero_Matrix3x3d(des->mQ);
	setSameDiagonal_Matrix3x3d(des->mQ, 1);

	double initialSigmaR = 0.25;
	setZero_Matrix3x3d(des->mR);
	setSameDiagonal_Matrix3x3d(des->mR, 0.0625);

	setZero_Matrix3x3d(des->mRaccel);
	setSameDiagonal_Matrix3x3d(des->mRaccel, 0.5625);

	setZero_Matrix3x3d(des->mS);
	setZero_Matrix3x3d(des->mH);
	setZero_Matrix3x3d(des->mK);
	setZero_Vector3d(&des->mNu);
	setZero_Vector3d(&des->mz);
	setZero_Vector3d(&des->mh);
	setZero_Vector3d(&des->mu);
	setZero_Vector3d(&des->mx);
	set_Vector3d(&des->down, 0, 0, 9.81);
	set_Vector3d(&des->north, 0.0, 1.0, 0.0);
	des->alignedToGravity = 0;
	des->alignedToNorth = 0;
}

int isReady(pOrientationEKF des)
{
	return des->alignedToGravity;
}

double getHeadingDegrees(pOrientationEKF des)
{
	double x = get_element_Matrix3x3d(des->so3SensorFromWorld, 2, 0);
	double y = get_element_Matrix3x3d(des->so3SensorFromWorld, 2, 1);

	double mag = sqrt(x * x + y * y);
	if (mag < 0.1)
	{
		return 0.0;
	}
	double heading = -90.0 - atan2(y, x) / 3.141592653589793 * 180.0;
	if (heading < 0.0)
	{
		heading += 360.0;
	}
	if (heading >= 360.0)
	{
		heading -= 360.0;
	}
	return heading;
}

void setHeadingDegrees(pOrientationEKF des, double heading)
{
	double currentHeading = getHeadingDegrees(des);
	double deltaHeading = heading - currentHeading;
	double s = sin(deltaHeading / 180.0 * 3.141592653589793);
	double c = cos(deltaHeading / 180.0 * 3.141592653589793);

	double deltaHeadingRotationVals[3][3] = { { c, -s, 0.0 }, { s, c, 0.0 }, { 0.0, 0.0, 1.0 } };

	arrayAssign(deltaHeadingRotationVals, des->setHeadingDegreesTempM1);
	mult_Matrix3x3d(des->so3SensorFromWorld, des->setHeadingDegreesTempM1, des->so3SensorFromWorld);
}

void getGLMatrix(double * result, pOrientationEKF des)
{
	glMatrixFromSo3(result, des, des->so3SensorFromWorld);
}

void getPredictedGLMatrix(double * result, pOrientationEKF des, double secondsAfterLastGyroEvent)
{
	pVector3d pmu = &des->getPredictedGLMatrixTempV1;
	copy_Vector3d(pmu, &des->lastGyro);
	scale_Vector3d(pmu, -secondsAfterLastGyroEvent);
	pMatrix3x3d so3PredictedMotion = des->getPredictedGLMatrixTempM1;
	sO3FromMu(pmu, so3PredictedMotion);

	Matrix3x3d so3PredictedState;
	copy_Matrix3x3d(so3PredictedState, des->getPredictedGLMatrixTempM2);
	mult_Matrix3x3d(so3PredictedMotion, des->so3SensorFromWorld, so3PredictedState);
	glMatrixFromSo3(result, des, so3PredictedState);
}

void getRotationMatrix(pMatrix3x3d result, pOrientationEKF des)
{
	copy_Matrix3x3d(result, des->so3SensorFromWorld);
}

void arrayAssign(double data[3][3], pMatrix3x3d m)
{
	set_Matrix3x3d(m, data[0][0], data[0][1], data[0][2], data[1][0], data[1][1], data[1][2], data[2][0], data[2][1], data[2][2]);
}

int isAlignedToGravity(pOrientationEKF des)
{
	return des->alignedToGravity;
}

int isAlignedToNorth(pOrientationEKF des)
{
	return des->alignedToNorth;
}

void processGyro(pOrientationEKF des, pVector3d gyro, long sensorTimeStamp)
{
	float kTimeThreshold = 0.04F;
	float kdTdefault = 0.01F;
	if (des->sensorTimeStampGyro != 0)
	{
		float dT = (float)(sensorTimeStamp - des->sensorTimeStampGyro) * 1.0E-9;
		if (dT > 0.04F) {
			dT = des->gyroFilterValid ? des->filteredGyroTimestep : 0.01F;
		}
		else {
			filterGyroTimestep(des, dT);
		}
		copy_Vector3d(&des->mu, gyro);

		scale_Vector3d(&des->mu, -dT);
		sO3FromMu(&des->mu, des->so3LastMotion);
		copy_Matrix3x3d(des->processGyroTempM1, des->so3SensorFromWorld);
		mult_Matrix3x3d(des->so3LastMotion, des->so3SensorFromWorld, des->processGyroTempM1);
		copy_Matrix3x3d(des->so3SensorFromWorld, des->processGyroTempM1);
		updateCovariancesAfterMotion(des);
		copy_Matrix3x3d(des->processGyroTempM2, des->mQ);
		scale_Matrix3x3d(des->processGyroTempM2,(double)dT * dT);
		add_Matrix3x3d(des->mP, des->processGyroTempM2, des->mP);
	}
	des->sensorTimeStampGyro = sensorTimeStamp;
	copy_Vector3d(&des->lastGyro, gyro);
}

void updateAccelCovariance(pOrientationEKF des, double currentAccelNorm)
{
	double currentAccelNormChange = (currentAccelNorm - des->previousAccelNorm);
	currentAccelNormChange = currentAccelNormChange > 0 ? currentAccelNormChange : -currentAccelNormChange;
	des->previousAccelNorm = currentAccelNorm;

	double kSmoothingFactor = 0.5;
	des->movingAverageAccelNormChange = (0.5 * currentAccelNormChange + 0.5 * des->movingAverageAccelNormChange);

	double kMaxAccelNormChange = 0.15;

	double normChangeRatio = des->movingAverageAccelNormChange / 0.15;
	double accelNoiseSigma = 7.0 > 0.75 + normChangeRatio * 6.25 ? 0.75 + normChangeRatio * 6.25 : 7.0;
	setSameDiagonal_Matrix3x3d(des->mRaccel, accelNoiseSigma * accelNoiseSigma);
}

void processAcc(pOrientationEKF des, pVector3d acc, long sensorTimeStamp)
{
	copy_Vector3d(&des->mz, acc);
	updateAccelCovariance(des, length_Vector3d(&des->mz));
	if (des->alignedToGravity)
	{
		accObservationFunctionForNumericalJacobian(des, des->so3SensorFromWorld, &des->mNu);

		double eps = 1.0E-7;
		int dof;
		for (dof = 0; dof < 3; dof++)
		{
			pVector3d delta = &des->processAccVDelta;
			setZero_Vector3d(delta);
			setComponent_Vector3d(delta, dof, eps);
			sO3FromMu(delta, des->processAccTempM1);
			mult_Matrix3x3d(des->processAccTempM1, des->so3SensorFromWorld, des->processAccTempM2);

			accObservationFunctionForNumericalJacobian(des, des->processAccTempM2, &des->processAccTempV1);

			pVector3d withDelta = &des->processAccTempV1;

			sub_Vector3d(&des->mNu, withDelta, &des->processAccTempV2);
			scale_Vector3d(&des->processAccTempV2, 1.0 / eps);
			setColumn_Matrix3x3d(des->mH, dof, &des->processAccTempV2);
		}

		Get_transpose_Matrix3x3d(des->processAccTempM3, des->mH);
		mult_Matrix3x3d(des->mP, des->processAccTempM3, des->processAccTempM4);
		mult_Matrix3x3d(des->mH, des->processAccTempM4, des->processAccTempM5);
		add_Matrix3x3d(des->processAccTempM5, des->mRaccel, des->mS);
		inverse_Matrix3x3d(des->processAccTempM3, des->mS);
		Get_transpose_Matrix3x3d(des->processAccTempM4, des->mH);
		mult_Matrix3x3d(des->processAccTempM4, des->processAccTempM3, des->processAccTempM5);
		mult_Matrix3x3d(des->mP, des->processAccTempM5, des->mK);
		mult_Vector_Matrix3x3d(des->mK, &des->mNu, &des->mx);
		mult_Matrix3x3d(des->mK, des->mH, des->processAccTempM3);
		setIdentity_Matrix3x3d(des->processAccTempM4);
		minus_Matrix3x3d(des->processAccTempM4, des->processAccTempM3, des->processAccTempM4);
		mult_Matrix3x3d(des->processAccTempM4, des->mP, des->processAccTempM3);
		copy_Matrix3x3d(des->mP, des->processAccTempM3);
		sO3FromMu(&des->mx, des->so3LastMotion);
		mult_Matrix3x3d(des->so3LastMotion, des->so3SensorFromWorld, des->so3SensorFromWorld);
		updateCovariancesAfterMotion(des);
	}
	else
	{
		So3Util tmpSo3Util;
		init_So3Util(&tmpSo3Util);
		sO3FromTwoVec(&tmpSo3Util, &des->down, &des->mz, des->so3SensorFromWorld);
		des->alignedToGravity = 1;
	}
}

void processMag(pOrientationEKF des, float mag[], long sensorTimeStamp)
{
	if (des->alignedToGravity != 1)
	{
		return;
	}

	set_Vector3d(&des->mz, mag[0], mag[1], mag[2]);
	normalize_Vector3d(&des->mz);

	Vector3d downInSensorFrame;
	getColumn_Matrix3x3d(des->so3SensorFromWorld, 2, &downInSensorFrame);

	cross_Vector3d(&des->mz, &downInSensorFrame, &des->processMagTempV1);
	pVector3d perpToDownAndMag = &des->processMagTempV1;
	normalize_Vector3d(perpToDownAndMag);

	cross_Vector3d(&downInSensorFrame, perpToDownAndMag, &des->processMagTempV2);
	pVector3d magHorizontal = &des->processMagTempV2;

	normalize_Vector3d(magHorizontal);
	copy_Vector3d(&des->mz, magHorizontal);

	if (des->alignedToNorth)
	{
		magObservationFunctionForNumericalJacobian(des, des->so3SensorFromWorld, &des->mNu);

		double eps = 1.0E-7;
		int dof;
		for (dof = 0; dof < 3; dof++)
		{
			pVector3d delta = &des->processMagTempV3;
			setZero_Vector3d(delta);
			setComponent_Vector3d(delta, dof, eps);

			sO3FromMu(delta, des->processMagTempM1);
			mult_Matrix3x3d(des->processMagTempM1, des->so3SensorFromWorld, des->processMagTempM2);

			magObservationFunctionForNumericalJacobian(des, des->processMagTempM2, &des->processMagTempV4);

			pVector3d withDelta = &des->processMagTempV4;

			sub_Vector3d(&des->mNu, withDelta, &des->processMagTempV5);
			scale_Vector3d(&des->processMagTempV5, 1.0 / eps);
			setColumn_Matrix3x3d(des->mH, dof, &des->processMagTempV5);
		}

		Get_transpose_Matrix3x3d(des->processMagTempM4, des->mH);
		mult_Matrix3x3d(des->mP, des->processMagTempM4, des->processMagTempM5);
		mult_Matrix3x3d(des->mH, des->processMagTempM5, des->processMagTempM6);
		add_Matrix3x3d(des->processMagTempM6, des->mR, des->mS);
		inverse_Matrix3x3d(des->processMagTempM4, des->mS);
		Get_transpose_Matrix3x3d(des->processMagTempM5, des->mH);
		mult_Matrix3x3d(des->processMagTempM5, des->processMagTempM4, des->processMagTempM6);
		mult_Matrix3x3d(des->mP, des->processMagTempM6, des->mK);

		mult_Vector_Matrix3x3d(des->mK, &des->mNu, &des->mx);

		mult_Matrix3x3d(des->mK, des->mH, des->processMagTempM4);
		setIdentity_Matrix3x3d(des->processMagTempM5);
		minus_Matrix3x3d(des->processMagTempM5, des->processMagTempM4, des->processMagTempM5);
		mult_Matrix3x3d(des->processMagTempM5, des->mP, des->processMagTempM4);
		copy_Matrix3x3d(des->mP, des->processMagTempM4);
		sO3FromMu(&des->mx, des->so3LastMotion);
		mult_Matrix3x3d(des->so3LastMotion, des->so3SensorFromWorld, des->processMagTempM4);
		copy_Matrix3x3d(des->so3SensorFromWorld, des->processMagTempM4);
		updateCovariancesAfterMotion(des);
	}
	else
	{
		magObservationFunctionForNumericalJacobian(des, des->so3SensorFromWorld, &des->mNu);
		sO3FromMu(&des->mNu, des->so3LastMotion);
		mult_Matrix3x3d(des->so3LastMotion, des->so3SensorFromWorld, des->processMagTempM4);
		copy_Matrix3x3d(des->so3SensorFromWorld, des->processMagTempM4);
		updateCovariancesAfterMotion(des);
		des->alignedToNorth = 1;
	}
}

void glMatrixFromSo3(double * result, pOrientationEKF des, pMatrix3x3d so3)
{	int r,c;
	for ( r = 0; r < 3; r++)
	{
		for ( c = 0; c < 3; c++)
		{
			des->rotationMatrix[(4 * c + r)] = get_element_Matrix3x3d(so3, r, c);
		}
	}
	des->rotationMatrix[3] = 0.0;
	des->rotationMatrix[7] = 0.0;
	des->rotationMatrix[11] = 0.0;
	des->rotationMatrix[12] =0.0;
	des->rotationMatrix[13] = 0.0;
	des->rotationMatrix[14] = 0.0;
	des->rotationMatrix[15] = 1.0;
	memcpy(result, des->rotationMatrix,sizeof(double)*16);
}

void filterGyroTimestep(pOrientationEKF des, float timeStep)
{
	float kFilterCoeff = 0.95F;
	float kMinSamples = 10.0F;
	if (!des->timestepFilterInit)
	{
		des->filteredGyroTimestep = timeStep;
		des->numGyroTimestepSamples = 1;
		des->timestepFilterInit = 1;
	}
	else
	{
		des->filteredGyroTimestep = (0.95F * des->filteredGyroTimestep + 0.050000012F * timeStep);
		if (++des->numGyroTimestepSamples > 10.0F) {
			des->gyroFilterValid = 1;
		}
	}
}

void updateCovariancesAfterMotion(pOrientationEKF des)
{
	Get_transpose_Matrix3x3d(des->updateCovariancesAfterMotionTempM1, des->so3LastMotion);
	mult_Matrix3x3d(des->mP, des->updateCovariancesAfterMotionTempM1, des->updateCovariancesAfterMotionTempM2);
	mult_Matrix3x3d(des->so3LastMotion, des->updateCovariancesAfterMotionTempM2, des->mP);
	setIdentity_Matrix3x3d(des->so3LastMotion);
}

void accObservationFunctionForNumericalJacobian(pOrientationEKF des, pMatrix3x3d so3SensorFromWorldPred, pVector3d result)
{
	mult_Vector_Matrix3x3d(so3SensorFromWorldPred, &des->down, &des->mh);
	So3Util tmpSo3Util;
	init_So3Util(&tmpSo3Util);
	sO3FromTwoVec(&tmpSo3Util, &des->mh, &des->mz, des->accObservationFunctionForNumericalJacobianTempM);
	muFromSO3(&tmpSo3Util, des->accObservationFunctionForNumericalJacobianTempM, result);
}

void magObservationFunctionForNumericalJacobian(pOrientationEKF des, pMatrix3x3d so3SensorFromWorldPred, pVector3d result)
{
	mult_Vector_Matrix3x3d(so3SensorFromWorldPred, &des->north, &des->mh);
	So3Util tmpSo3Util;
	init_So3Util(&tmpSo3Util);
	sO3FromTwoVec(&tmpSo3Util, &des->mh, &des->mz, des->magObservationFunctionForNumericalJacobianTempM);
	muFromSO3(&tmpSo3Util, des->magObservationFunctionForNumericalJacobianTempM, result);
}

