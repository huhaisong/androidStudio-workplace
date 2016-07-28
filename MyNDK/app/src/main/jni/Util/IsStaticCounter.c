#include "IsStaticCounter.h"
#include <string.h>
void inti_IsStaticCounter(pIsStaticCounter des, int tmp_minStaticFrames)
{
	memset(des, 0, sizeof(IsStaticCounter));
	des->minStaticFrames = tmp_minStaticFrames;
}

void appendFrame(pIsStaticCounter des, int isStatic)
{
	if (isStatic == 0) {
		des->consecutiveIsStatic = 0;
	}
	else {
		des->consecutiveIsStatic += 1;
	}
}

int isRecentlyStatic(pIsStaticCounter des)
{
	return des->consecutiveIsStatic >= des->minStaticFrames ? 1 : 0;
}

