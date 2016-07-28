#ifndef HeadTracker_H
#define HeadTracker_H
#include "GyroscopeBiasEstimator.h"
#include "OrientationEKF.h"
#include "Matrix.h"
typedef struct _HeadTracker{
	//final Display display;
	float ekfToHeadTracker[16];
	float sensorToDisplay[16];
	float displayRotation;
	float neckModelTranslation[16];
	float tmpHeadView[16];
	float tmpHeadView2[16];
	float neckModelFactor;
	//final Object neckModelFactorMutex = new Object();
	volatile int tracking;
	OrientationEKF tracker;
	//Object gyroBiasEstimatorMutex = new Object();
	GyroscopeBiasEstimator gyroBiasEstimator;
	//SensorEventProvider sensorEventProvider;
	//Clock clock;
	//long latestGyroEventClockTimeNs;
	volatile int firstGyroValue;
	float initialSystemGyroBias[3];
	Vector3d gyroBias;
	Vector3d latestGyro;
	Vector3d latestAcc;
}HeadTracker, *pHeadTracker;


void init_HeadTracker(pHeadTracker des);
void onSensorChanged(pHeadTracker des, int sensor_type, float* event_values, int length_values, long timestamp);
void startTracking(pHeadTracker des);
void resetTracker(pHeadTracker des);
void stopTracking(pHeadTracker des);
void setNeckModelEnabled(pHeadTracker des, int enabled);
float getNeckModelFactor(pHeadTracker des);
void setNeckModelFactor(pHeadTracker des, float factor);
void getLastHeadView(pHeadTracker des,int display_rotation,float headView[16],double secondsSinceLastGyroEvent);
#endif
