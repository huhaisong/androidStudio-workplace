uniform mat4 g_mxWVP;
uniform mat4 g_mxView;
uniform mat4 g_mxWorldView;
uniform vec3 g_v3LightDir;

attribute vec3 v3Pos;
attribute vec3 v3Normal;
attribute vec2 v2UV;

varying vec3 _v3PosVS;
varying vec3 _v3NormalVS;
varying vec2 _v2UV;
varying vec3 _v3LightDirVS;

void main(void)
{
   gl_Position = g_mxWVP * vec4(v3Pos,1);
   _v3PosVS = (g_mxWorldView * vec4(v3Pos,1)).xyz;
   _v3NormalVS = (g_mxWorldView * vec4(v3Normal,0)).xyz;
   _v2UV = v2UV;   
   _v3LightDirVS = (g_mxView * vec4(g_v3LightDir,0)).xyz;
}