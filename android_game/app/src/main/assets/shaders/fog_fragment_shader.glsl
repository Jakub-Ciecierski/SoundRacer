uniform vec4 u_fogColor;
uniform float u_fogMaxDist;
varying float v_eyeDist;
uniform float u_fogMinDist;
uniform vec4 u_Color;

float computeLinearFogFactor()
{
    float factor;
    factor = (u_fogMaxDist - v_eyeDist) /
        (u_fogMaxDist - u_fogMinDist );

    // Clamp in the [0,1] range\n" +
    factor = clamp( factor, 0.0, 1.0 );

    return factor;
}

void main(void)
{
    float fogFactor = computeLinearFogFactor();
    vec4  fogColor = fogFactor * u_fogColor;

    // Compute final color as a lerp with fog factor\n" +
    gl_FragColor = u_Color * fogFactor +
    fogColor * (1.0 - fogFactor);
}