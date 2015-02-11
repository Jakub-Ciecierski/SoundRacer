precision mediump float;

uniform vec4 u_fogColor;
uniform float u_fogMaxDist;
uniform vec3 u_LightPosition;
uniform float u_fogMinDist;
uniform sampler2D u_TextureUnit;

varying vec2 v_TextureCoordinates;
varying vec3 v_Normal;
varying float v_eyeDist;
varying vec3 v_Position;

float computeLinearFogFactor()
{
    float factor;
    factor = (u_fogMaxDist - v_eyeDist) / (u_fogMaxDist - u_fogMinDist);
    factor = clamp(factor, 0.0,1.0);
    return factor;
}

void main()
{
    float fogFactor = computeLinearFogFactor();
    vec4 fogColor = fogFactor * u_fogColor;

    vec3 lightVector = normalize(u_LightPosition - v_Position);
    float diffuse = max(dot(v_Normal, lightVector),0.0);
    gl_FragColor = diffuse * texture2D(u_TextureUnit,v_TextureCoordinates)*fogFactor + fogColor*(1.0 - fogFactor);
}