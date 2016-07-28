#include "LowPassFilter.h"
#include <string.h>

#define  NANOS_TO_SECONDS  1.0e-9;


void init_LowPassFilter(pLowPassFilter des, double cutoffFrequency)
{
	memset(des, 0, sizeof(LowPassFilter));

	des->timeConstantSecs = (1.0 / (6.283185307179586 * cutoffFrequency));
}

int getNumSamples(pLowPassFilter des)
{
	return des->numSamples;
}


void addWeightedSample(pLowPassFilter des, pVector3d sampleData, long timestampNs, double weight)
{
	des->numSamples += 1;
	if (des->numSamples == 1)
	{
		copy_Vector3d(&des->filteredData, sampleData);
		des->lastTimestampNs = timestampNs;
	}
	else
	{
		double weightedDeltaSecs;
		double alpha;
		weightedDeltaSecs = weight * (timestampNs - des->lastTimestampNs) * NANOS_TO_SECONDS;
		alpha = weightedDeltaSecs / (des->timeConstantSecs + weightedDeltaSecs);
		scale_Vector3d(&des->filteredData, 1.0 - alpha);
		copy_Vector3d(&des->temp, sampleData);
		scale_Vector3d(&des->temp, alpha);
		add_Vector3d(&des->temp, &des->filteredData, &des->filteredData);
		des->lastTimestampNs = timestampNs;
	}
}

void addSample(pLowPassFilter des, pVector3d sampleData, long timestampNs)
{
	addWeightedSample(des, sampleData, timestampNs, 1.0);
}

Vector3d getFilteredData(pLowPassFilter des)
{
	return (des->filteredData);
}
