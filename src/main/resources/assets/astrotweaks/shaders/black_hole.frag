#version 120
varying vec3 vPos;
varying vec3 vView;
varying vec3 vNormal;
uniform float uTime;
uniform float uHorizon;
uniform float uGravityRange;
uniform float uMass;
uniform float uMode;
void main(){
  vec3 n = normalize(vNormal);
  vec3 viewDir = normalize(-vView + vec3(0.0001, 0.0, 0.0));
  float ndv = max(0.0, dot(n, viewDir));
  float fresnel = pow(1.0 - ndv, 2.5);
  if (uMode < 0.5) {
    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    return;
  } else if (uMode < 1.5) {
    float alpha = pow(fresnel, 2.0);
    float shimmer = 0.9 + 0.1 * sin(uTime * 1.1);
    alpha *= 0.44 * shimmer;
    if (alpha < 0.003) discard;
    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);
    return;
  } else if (uMode < 2.5) {
    float alpha = pow(fresnel, 2.0);
    float ang = atan(vPos.z, vPos.x);
    float shimmer = 0.9 + 0.1 * sin(uTime * 1.1 + ang * 2.0 + 1.5);
    alpha *= 0.24 * shimmer;
    if (alpha < 0.002) discard;
    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);
    return;
  } else {
    // Outer halo: widest fresnel band (pow 1.5) so the rim survives the discard cutoff
    float alpha = pow(fresnel, 1.5);
    float ang = atan(vPos.z, vPos.x);
    float shimmer = 0.9 + 0.1 * sin(uTime * 1.1 + ang * 2.0 + 2.5);
    alpha *= 0.17 * shimmer;
    if (alpha < 0.0005) discard;
    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);
    return;
  }
}
