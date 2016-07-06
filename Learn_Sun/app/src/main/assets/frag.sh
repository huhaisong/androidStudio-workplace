precision mediump float;
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
varying vec3 vPosition;
uniform sampler2D sTextureFloor;//纹理内容数据
uniform sampler2D sTextureWall;//纹理内容数据
uniform sampler2D sTextureRoof;//纹理内容数据
void main()
{

  vec4 finalColorFloor;
  vec4 finalColorWall;
  vec4 finalColorRoof;

  finalColorFloor= texture2D(sTextureFloor, vTextureCoord);
  finalColorWall= texture2D(sTextureWall, vTextureCoord);
  finalColorRoof= texture2D(sTextureRoof, vTextureCoord);

    if(vPosition.z ==1.5){
        gl_FragColor = finalColorRoof;
    }else if(vPosition.z== -1.5){
        gl_FragColor = finalColorFloor;
    }else {
        gl_FragColor = finalColorWall;
    }
}