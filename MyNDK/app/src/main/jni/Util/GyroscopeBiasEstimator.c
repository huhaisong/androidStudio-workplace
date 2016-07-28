#include <string.h>
#include "GyroscopeBiasEstimator.h"

const  float ACCEL_LOWPASS_FREQ = 1.0F;
const  float GYRO_LOWPASS_FREQ = 10.0F;
const  float GYRO_BIAS_LOWPASS_FREQ = 0.15F;

const  float ACCEL_DIFF_STATIC_THRESHOLD = 0.5F;
const  float GYRO_DIFF_STATIC_THRESHOLD = 0.008F;

const  float GYRO_FOR_BIAS_THRESHOLD = 0.35F;

const  int NUM_GYRO_BIAS_SAMPLES_THRESHOLD = 30;
const  int NUM_GYRO_BIAS_SAMPLES_INITIAL_SMOOTHING = 100;
const  int IS_STATIC_NUM_FRAMES_THRESHOLD = 10;


void init_GyroscopeBiasEstimator(pGyroscopeBiasEstimator des)
{
	memset(des, 0, sizeof(GyroscopeBiasEstimator));
	init_LowPassFilter(&des->accelLowPass, 1.0);
	init_LowPassFilter(&des->gyroLowPass, 10.0);
	init_LowPassFilter(&des->gyroBiasLowPass, 0.15000000596046448);

	inti_IsStaticCounter(&des->isAccelStatic, 10);
	inti_IsStaticCounter(&des->isGyroStatic, 10);
}

void processGyroscope(pGyroscopeBiasEstimator des, pVector3d gyro, long sensorTimestampNs)
{
	addSample(&des->gyroLowPass, gyro, sensorTimestampNs);
	Vector3d tmp = getFilteredData(&des->gyroLowPass);
	sub_Vector3d(gyro, &tmp, &des->smoothedGyroDiff);
	appendFrame(&des->isGyroStatic, length_Vector3d(&des->smoothedGyroDiff) < 0.00800000037997961);

	if ((isRecentlyStatic(&des->isGyroStatic)) && (isRecentlyStatic(&des->isAccelStatic)))
		updateGyroBias(des, gyro, sensorTimestampNs);
}

void processAccelerometer(pGyroscopeBiasEstimator des, pVector3d accel, long sensorTimestampNs)
{
	addSample(&des->accelLowPass, accel, sensorTimestampNs);
	Vector3d tmpvertor = getFilteredData(&des->accelLowPass);
	sub_Vector3d(accel, &tmpvertor, &des->smoothedAccelDiff);
	appendFrame(&des->isAccelStatic, length_Vector3d(&des->smoothedAccelDiff) < 0.5);
}

void getGyroBias(pGyroscopeBiasEstimator des, pVector3d result)
{
	if (getNumSamples(&des->gyroBiasLowPass) < 30)
	{
		setZero_Vector3d(result);
	}
	else
	{
		Vector3d tmpvector = getFilteredData(&des->gyroBiasLowPass);
		copy_Vector3d(result, &tmpvector);
		double tmp = getNumSamples(&des->gyroBiasLowPass);
		double rampUpRatio = 1.0 > ((tmp - 30) / 100) ? ((tmp - 30) / 100) : 1.0;
		scale_Vector3d(result, rampUpRatio);
	}
}

void updateGyroBias(pGyroscopeBiasEstimator des, pVector3d gyro, long sensorTimestampNs)
{
	double tmp = length_Vector3d(gyro);
	if (tmp< 0.3499999940395355)
	{
		double updateWeight = 0.0 > 1.0 - tmp / 0.3499999940395355 ? 0.0 : 1.0 - tmp / 0.3499999940395355;
		Vector3d sampleData = getFilteredData(&des->gyroLowPass);
		updateWeight *= updateWeight;
		addWeightedSample(&des->gyroBiasLowPass, &sampleData, sensorTimestampNs, updateWeight);
	}
}

