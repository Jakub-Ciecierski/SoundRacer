uniform mat4 uMVPMatrix;
varying float v_eyeDist;
attribute vec4 vPosition;
uniform vec4 u_eyePos;

void main() {
    vec4 vViewPos = uMVPMatrix*vPosition;
    v_eyeDist = sqrt( (vViewPos.x - u_eyePos.x) *
        (vViewPos.x - u_eyePos.x) +
        (vViewPos.y - u_eyePos.y) *
        (vViewPos.y - u_eyePos.y) +
        (vViewPos.z - u_eyePos.z) *
        (vViewPos.z - u_eyePos.z) );

    gl_Position = uMVPMatrix * vPosition;
}