uniform mat4 u_MVPMatrix;
uniform vec4 u_eyePos;

attribute vec3 a_Normal;
attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;

varying float v_eyeDist;
varying vec3 v_Normal;
varying vec2 v_TextureCoordinates;
varying vec3 v_Position;

void main()
{
    v_Position = vec3(u_MVPMatrix * a_Position);
    v_Normal = vec3(u_MVPMatrix * vec4(a_Normal, 0.0));
    v_TextureCoordinates = a_TextureCoordinates;

    vec4 vViewPos = u_MVPMatrix * a_Position;
    v_eyeDist = sqrt(
        (vViewPos.x - u_eyePos.x)*(vViewPos.x - u_eyePos.x) +
        (vViewPos.y - u_eyePos.y)*(vViewPos.y - u_eyePos.y) +
        (vViewPos.z - u_eyePos.z)*(vViewPos.z - u_eyePos.z)
    );

    gl_Position = u_MVPMatrix*a_Position;
}