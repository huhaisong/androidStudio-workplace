precision mediump float;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying float vFogFactor;
void main()                         
{
	vec4 objectColor=vec4(0.95,0.95,0.95,1.0);//������ɫ	
	vec4 fogColor = vec4(0.97,0.76,0.03,1.0);//�����ɫ	
 	if(vFogFactor != 0.0){
		objectColor = objectColor*ambient+objectColor*specular+objectColor*diffuse;
		gl_FragColor = objectColor*vFogFactor + fogColor*(1.0-vFogFactor);
    }else{
 	    gl_FragColor=fogColor;
 	}
}