#include <time.h>
#include <string.h>
#include "HeadTracker.h"
/**
 * Created by 333 on 2016/7/6.
 */

const float DEFAULT_NECK_HORIZONTAL_OFFSET = 0.08F;
const float DEFAULT_NECK_VERTICAL_OFFSET = 0.075F;
const float DEFAULT_NECK_MODEL_FACTOR = 1.0F;
const float PREDICTION_TIME_IN_SECONDS = 0.058F;

//static HeadTracker createFromContext(Context context)
//{
//	SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
//	Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//	return new HeadTracker(new DeviceSensorLooper(sensorManager), new SystemClock(), display);
//}


//HeadTracker(SensorEventProvider sensorEventProvider, Clock clock, Display display)
void init_HeadTracker(pHeadTracker des)
{
	memset(des, 0, sizeof(HeadTracker));
	des->displayRotation = 0.0F;
	des->neckModelFactor = 1.0F;
	des->firstGyroValue = 1;
	init_OrientationEKF(&des->tracker);
	init_GyroscopeBiasEstimator(&des->gyroBiasEstimator);
	setIdentityM(des->neckModelTranslation);
}
/*
sensor_type:event.sensor.getType()
timestamp:event.timestamp
nano_time: clock.nanoTime()
event_values:event.values
length_values: the size of values 3 or 6
*/
void onSensorChanged(pHeadTracker des, int sensor_type, float* event_values, int length_values, long timestamp) {
	//Object var2;
	if (sensor_type == 1)
	{
		set_Vector3d(&des->latestAcc, (double)event_values[0], (double)event_values[1], (double)event_values[2]);
		processAcc(&des->tracker, &des->latestAcc, timestamp);
		//var2 = gyroBiasEstimatorMutex;
		//synchronized(gyroBiasEstimatorMutex) {
		if (&des->gyroBiasEstimator != NULL)
		{
			processAccelerometer(&des->gyroBiasEstimator, &des->latestAcc, timestamp);
		}
		//}
	}
	else if (sensor_type == 4 || sensor_type == 16) {
		//des->latestGyroEventClockTimeNs = nano_time;
		if (sensor_type == 16) {
			if (des->firstGyroValue && length_values == 6) {
				des->initialSystemGyroBias[0] = event_values[3];
				des->initialSystemGyroBias[1] = event_values[4];
				des->initialSystemGyroBias[2] = event_values[5];
			}
			set_Vector3d(&des->latestGyro, (double)(event_values[0] - des->initialSystemGyroBias[0]),
				(double)(event_values[1] - des->initialSystemGyroBias[1]),
				(double)(event_values[2] - des->initialSystemGyroBias[2]));
		}
		else {
			set_Vector3d(&des->latestGyro, (double)event_values[0], (double)event_values[1], (double)event_values[2]);
		}

		des->firstGyroValue = 0;

		if (&des->gyroBiasEstimator != NULL)
		{
			processGyroscope(&des->gyroBiasEstimator, &des->latestGyro, timestamp);
			getGyroBias(&des->gyroBiasEstimator, &des->gyroBias);
			sub_Vector3d(&des->latestGyro, &des->gyroBias, &des->latestGyro);
		}
	}
	processGyro(&des->tracker, &des->latestGyro, timestamp);
}

//void onAccuracyChanged(Sensor sensor, int accuracy) {
//}


void startTracking(pHeadTracker des)
{
	if (!des->tracking)
	{
		init_OrientationEKF(&des->tracker);
		//Object var1 = gyroBiasEstimatorMutex;
		//synchronized(gyroBiasEstimatorMutex) {
		if (&des->gyroBiasEstimator != NULL) {
			init_GyroscopeBiasEstimator(&des->gyroBiasEstimator);
		}
	}

	des->firstGyroValue = 1;
	//sensorEventProvider.registerListener(this);
	//sensorEventProvider.start();
	des->tracking = 1;
}

void resetTracker(pHeadTracker des)
{
	init_OrientationEKF(&des->tracker);
}

void stopTracking(pHeadTracker des) {
	if (des->tracking) {
		//sensorEventProvider.unregisterListener();
		//sensorEventProvider.stop();
		des->tracking = 0;
	}
}

void setNeckModelEnabled(pHeadTracker des,int enabled) {
	if (enabled)
	{
		setNeckModelFactor(des,1.0F);
	}
	else
	{
		setNeckModelFactor(des,0.0F);
	}

}

float getNeckModelFactor(pHeadTracker des) {
	//Object var1 = neckModelFactorMutex;
	//synchronized(neckModelFactorMutex) {
	return des->neckModelFactor;
	//}
}

void setNeckModelFactor(pHeadTracker des, float factor) {
	//Object var2 = neckModelFactorMutex;
	//synchronized(neckModelFactorMutex) {
	if (factor >= 0.0F && factor <= 1.0F) {
		des->neckModelFactor = factor;
	}
	/*else
	{
		throw new IllegalArgumentException("factor should be within [0.0, 1.0]");
	}*/
}
/*
display_rotation:display.getRotation()
nano_time:clock.nanoTime()

*/
void getLastHeadView(pHeadTracker des,int display_rotation,float headView[16],double secondsSinceLastGyroEvent)
{
	float rotation = 0.0F;
	switch (display_rotation) 
		{
	case 0:
		rotation = 0.0F;
		break;
	case 1:
		rotation = 90.0F;
		break;
	case 2:
		rotation = 180.0F;
		break;
	case 3:
		rotation = 270.0F;
	}

	if (rotation != des->displayRotation) {
		des->displayRotation = rotation;
		setRotateEulerM(des->sensorToDisplay, 0.0F, 0.0F, -rotation);
		setRotateEulerM(des->ekfToHeadTracker, -90.0F, 0.0F, rotation);
	}

	if (!isReady(&des->tracker))
	{
		return;
	}
		//double secondsSinceLastGyroEvent = (double)TimeUnit.NANOSECONDS.toSeconds(clock.nanoTime() - latestGyroEventClockTimeNs);

		double secondsToPredictForward = secondsSinceLastGyroEvent + 0.057999998331069946;
		double mat[16];
		getPredictedGLMatrix(mat, &des->tracker, secondsToPredictForward);
		int i;
		for (i = 0;i<16; ++i)
		{
			des->tmpHeadView[i] = (float)mat[i];
		}
		multiplyMM(des->tmpHeadView2, des->sensorToDisplay, des->tmpHeadView);
		multiplyMM(headView, des->tmpHeadView2,des->ekfToHeadTracker);
		setIdentityM(des->neckModelTranslation);
		translateM(des->neckModelTranslation, 0.0F, -(des->neckModelFactor * 0.075F), des->neckModelFactor * 0.08F);
		multiplyMM(des->tmpHeadView, des->neckModelTranslation, headView);
		translateMM(headView,  des->tmpHeadView,  0.0F, des->neckModelFactor * 0.075F, 0.0F);
}

//Matrix3x3d getCurrentPoseForTest()
//{
//	return new Matrix3x3d(tracker.getRotationMatrix());
//}


//void setGyroBiasEstimator(GyroscopeBiasEstimator estimator) {
//	Object var2 = gyroBiasEstimatorMutex;
//	synchronized(gyroBiasEstimatorMutex) {
//		gyroBiasEstimator = estimator;
//	}
//}

