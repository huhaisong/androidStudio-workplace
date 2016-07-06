precision mediump float;
varying  vec3 vPosition;
void main() {
   vec4 bColor=vec4(0.678,0.231,0.129,1);
   vec4 mColor=vec4(0.763,0.657,0.614,1);
   float y=vPosition.y;
   y=mod((y+100.0)*4.0,4.0);
   gl_FragColor = mColor;
}