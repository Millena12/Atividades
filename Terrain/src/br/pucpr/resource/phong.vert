#version 330

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uWorld;

uniform vec3 uCameraPosition;

in vec3 aPosition;
in vec3 aNormal;

out vec3 vNormal;
out vec3 vViewPath;

out float vertDist;

void main() {
    vNormal = (uWorld * vec4(aNormal, 0.0)).xyz;

    vec4 worldPos = uWorld * vec4(aPosition, 1.0f);
    gl_Position =  uProjection * uView * worldPos;

    vNormal = (uWorld * vec4(aNormal, 0.0)).xyz;
    vViewPath = uCameraPosition - worldPos.xyz;

    vertDist = distance(uCameraPosition, worldPos.xyz);
}