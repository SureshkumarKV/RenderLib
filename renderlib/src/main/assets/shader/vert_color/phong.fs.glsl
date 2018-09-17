#version 300 es
precision mediump float;

uniform vec3 uDiffuseColor;

in vec2 vTexCoord;

out vec4 fragColor;

void main() {
	fragColor = vec4(uDiffuseColor, 1.0) * vec4(vTexCoord, 1.0, 1.0);
}
