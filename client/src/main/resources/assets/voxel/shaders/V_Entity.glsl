//
// This file is part of Voxel
// 
// Copyright (C) 2016-2017 Lux Vacuos
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

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoords;
layout(location = 2) in vec3 normal;
layout(location = 3) in vec3 tangent;

out vec2 pass_textureCoords;
out vec4 pass_position;
out vec4 ShadowCoord[4];
out mat3 TBN;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionLightMatrix[4];
uniform mat4 viewLightMatrix;
uniform mat4 biasMatrix;

uniform int useShadows;

void main() {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = textureCoords;
	vec3 T = normalize(vec3(transformationMatrix * vec4(tangent, 0.0)));
	vec3 N = normalize(vec3(transformationMatrix * vec4(normal, 0.0)));
	T = normalize(T - dot(T, N) * N);
	vec3 B = cross(N, T);
	TBN = mat3(T, B, N);

	pass_position = worldPosition;
	if(useShadows == 1){
		vec4 posLight = viewLightMatrix * worldPosition;
		ShadowCoord[0] = biasMatrix * projectionLightMatrix[0] * posLight;
		ShadowCoord[1] = biasMatrix * projectionLightMatrix[1] * posLight;
		ShadowCoord[2] = biasMatrix * projectionLightMatrix[2] * posLight;
		ShadowCoord[3] = biasMatrix * projectionLightMatrix[3] * posLight;
	}
}