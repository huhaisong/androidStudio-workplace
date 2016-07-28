#ifndef LowPassFilter_h
#define LowPassFilter_h

#include "Vector3d.h"

 
typedef struct _LowPassFilter
{
	int numSamples;
	double timeConstantSecs;
	long lastTimestampNs;

	Vector3d filteredData;
	Vector3d temp;
}LowPassFilter, *pLowPassFilter;

void init_LowPassFilter(pLowPassFilter des, double cutoffFrequency);
int getNumSamples(pLowPassFilter des);
void addWeightedSample(pLowPassFilter des, pVector3d sampleData, long timestampNs, double weight);

void addSample(pLowPassFilter des, pVector3d sampleData, long timestampNs);
Vector3d getFilteredData(pLowPassFilter des);
#endif
