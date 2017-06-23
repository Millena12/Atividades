#version 330

out vec4 outColor;

in vec3 vColor;


void main(){

outColor = vec4(vColor,1.0f);

}