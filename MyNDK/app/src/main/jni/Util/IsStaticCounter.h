#ifndef IsStaticCounter_h
#define IsStaticCounter_h

typedef struct _IsStaticCounter
{
	int minStaticFrames;
	int consecutiveIsStatic;
}IsStaticCounter, *pIsStaticCounter;

void inti_IsStaticCounter(pIsStaticCounter des, int tmp_minStaticFrames);
void appendFrame(pIsStaticCounter des, int isStatic);
int isRecentlyStatic(pIsStaticCounter des);


#endif
