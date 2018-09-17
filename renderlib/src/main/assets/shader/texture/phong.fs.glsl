#version 300 es
precision mediump float;

uniform vec3 uDiffuseColor;
uniform sampler2D uTexture2DSamplers[4];

in vec2 vTexCoord;

out vec4 fragColor;

void main() {
	fragColor = texture(uTexture2DSamplers[0], vTexCoord);
}
