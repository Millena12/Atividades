#version 330

in vec3 aPosition;
in vec3 aColor;

uniform mat4 uWorld;
uniform mat4 uProjection;
uniform mat4 uView;

uniform float uTime;

out vec3 vColor;

void main(){

    vColor = aColor;
    vec4 worldPos = uWorld * vec4(aPosition.x,sin((length(aPosition.xz)/3)+uTime), aPosition.z, 1.0f);
    gl_Position =  uProjection * uView * worldPos;
}