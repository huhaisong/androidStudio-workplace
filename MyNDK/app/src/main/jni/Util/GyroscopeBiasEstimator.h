#ifndef GyroscopeBiasEstimator_h
#define GyroscopeBiasEstimator_h

#include "IsStaticCounter.h"
#include "LowPassFilter.h"


typedef struct _GyroscopeBiasEstimator
{
	LowPassFilter accelLowPass;
	LowPassFilter gyroLowPass;
	LowPassFilter gyroBiasLowPass;

	Vector3d smoothedGyroDiff;
	Vector3d smoothedAccelDiff;

	IsStaticCounter isAccelStatic;
	IsStaticCounter isGyroStatic;
}GyroscopeBiasEstimator, *pGyroscopeBiasEstimator;

void init_GyroscopeBiasEstimator(pGyroscopeBiasEstimator des);
void processGyroscope(pGyroscopeBiasEstimator des, pVector3d gyro, long sensorTimestampNs);

void processAccelerometer(pGyroscopeBiasEstimator des, pVector3d accel, long sensorTimestampNs);

void getGyroBias(pGyroscopeBiasEstimator des, pVector3d result);

void updateGyroBias(pGyroscopeBiasEstimator des, pVector3d gyro, long sensorTimestampNs);

#endif
