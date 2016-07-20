uniform mat4 uMVPMatrix;
uniform mat4 uMMatrix;
uniform vec3 uLightLocation;
uniform vec3 uCamera;
attribute vec3 aPosition;
attribute vec3 aNormal;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying float vFogFactor;

void pointLight(
  in vec3 normal,
  inout vec4 ambient,
  inout vec4 diffuse,
  inout vec4 specular,
  in vec3 lightLocation,
  in vec4 lightAmbient,
  in vec4 lightDiffuse,
  in vec4 lightSpecular
){
  ambient=lightAmbient;
  vec3 normalTarget=aPosition+normal;
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal);

  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);  

  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);  
  vp=normalize(vp);
  vec3 halfVector=normalize(vp+eye);
  float shininess=50.0;
  float nDotViewPosition=max(0.0,dot(newNormal,vp));
  diffuse=lightDiffuse*nDotViewPosition;
  float nDotViewHalfVector=dot(newNormal,halfVector);
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess));
  specular=lightSpecular*powerFactor;
}
float computeFogFactor(){
   float tmpFactor;
   float fogDistance = length(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
   const float end = 450.0;
   const float start = 350.0;
   tmpFactor = max(min((end- fogDistance)/(end  -  start),1.0),0.0);
   return tmpFactor;
}
void main()     
{
   gl_Position = uMVPMatrix * vec4(aPosition,1);
   pointLight(normalize(aNormal),ambient,diffuse,specular,uLightLocation,vec4(0.4,0.4,0.4,1.0),vec4(0.7,0.7,0.7,1.0),vec4(0.3,0.3,0.3,1.0));
   vFogFactor = computeFogFactor();
}