//
// This file is part of Voxel
// 
// Copyright (C) 2016 Lux Vacuos
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
// 
//

#version 330 core

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 cameraPosition;
uniform vec3 lightPosition;
uniform sampler2D gDiffuse;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gPBR; // R = roughness, G = metallic, B = specular
uniform sampler2D gMask;
uniform sampler2D composite0;
uniform int shadowDrawDistance;

#define TRANSITION_DISTANCE 2.5

float beckmannDistribution(float x, float roughness) {
	float NdotH = max(x, 0.0001);
  	float cos2Alpha = NdotH * NdotH;
	float tan2Alpha = (cos2Alpha - 1.0) / cos2Alpha;
	float roughness2 = roughness * roughness;
	float denom = 3.141592653589793 * roughness2 * cos2Alpha * cos2Alpha;
	return exp(tan2Alpha / roughness2) / denom;
}

float beckmannSpecular(vec3 lightDirection, vec3 viewDirection, vec3 surfaceNormal, float roughness) {
	return beckmannDistribution(dot(surfaceNormal, normalize(lightDirection + viewDirection)), roughness);
}

void main(void){
	vec2 texcoord = textureCoords;
	vec4 mask = texture(gMask, texcoord);
	vec4 image = texture(gDiffuse, texcoord);
	if (mask.a != 1) {
		vec4 pbr = texture(gPBR, texcoord);
    	vec4 position = texture(gPosition, texcoord);
    	vec4 normal = texture(gNormal, texcoord);
		image.rgb *= 1.0 - pbr.g;
		vec3 lightDir = lightPosition;
    	lightDir = normalize(lightDir);
    	vec3 eyeDir = normalize(cameraPosition-position.xyz);
    	normal = normalize(normal);
		float distance = length(cameraPosition-position.xyz);
    	float shadowDist = distance - (shadowDrawDistance - TRANSITION_DISTANCE);
		shadowDist = shadowDist / TRANSITION_DISTANCE;
		float fadeOut = clamp(1.0-shadowDist, 0.0, 1.0);
		float normalDotLight = max(dot(normal.xyz, lightDir), 0);
    	float finalLight = normalDotLight - (position.w * fadeOut);
    	finalLight = max(finalLight, 0.015);
    	image = finalLight * image;
    	if(position.w < 1 && normalDotLight > 0.0 && pbr.r > 0.0){
			image += beckmannSpecular(lightDir.xyz, eyeDir, normal.xyz, pbr.r) * (1-position.w) * normalDotLight;
	   	}
	}
    image += texture(composite0, texcoord);
	out_Color = image;
	
}