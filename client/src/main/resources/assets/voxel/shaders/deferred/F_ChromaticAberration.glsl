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

in vec2 textureCoords;

out vec4 out_Color;

uniform vec2 resolution;
uniform vec3 cameraPosition;
uniform vec3 previousCameraPosition;
uniform mat4 projectionMatrix;
uniform mat4 inverseProjectionMatrix;
uniform mat4 inverseViewMatrix;
uniform mat4 previousViewMatrix;
uniform sampler2D composite0;
uniform sampler2D composite1;
uniform sampler2D gDepth;

uniform int useChromaticAberration;

const float max_distort = 0.04;
const int num_iter = 12;
const float reci_num_iter_f = 1.0 / float(num_iter);

vec2 barrelDistortion(vec2 coord, float amt) {
	vec2 cc = coord - 0.5;
	float dist = dot(cc, cc);
	return coord + cc * dist * amt;
}

float sat(float t ) {
	return clamp( t, 0.0, 1.0 );
}

float linterp(float t ) {
	return sat( 1.0 - abs( 2.0*t - 1.0 ) );
}

float remap(float t, float a, float b ) {
	return sat( (t - a) / (b - a) );
}

vec4 spectrum_offset(float t ) {
	vec4 ret;
	float lo = step(t,0.5);
	float hi = 1.0-lo;
	float w = linterp( remap( t, 1.0/6.0, 5.0/6.0 ) );
	ret = vec4(lo,1.0,hi, 1.) * vec4(1.0-w, w, 1.0-w, 1.);

	return pow( ret, vec4(1.0/2.2) );
}

void main(void){
	vec2 texcoord = textureCoords;
	vec4 textureColour = vec4(0.0);
	if(useChromaticAberration == 1) {
		vec2 uv = (gl_FragCoord.xy/resolution.xy);
		vec4 sumcol = vec4(0.0);
		vec4 sumw = vec4(0.0);	
		for (int i = 0; i < num_iter; ++i) {
			float t = float(i) * reci_num_iter_f;
			vec4 w = spectrum_offset(t);
			sumw += w;
			sumcol += w * texture(composite0, barrelDistortion(uv, .6 * max_distort * t));
		}

		textureColour = sumcol / sumw;
	} else {
		textureColour = texture(composite0, texcoord);
	}
    out_Color = textureColour;
}