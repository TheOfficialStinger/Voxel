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

in vec3 textureCoords;
in vec3 pass_position;
in vec3 pass_normal;

out vec4 [5] out_Color;

uniform float time;
uniform vec3 fogColour;
uniform vec3 lightPosition;

#define LOWER_LIMIT  -0.8
#define UPPER_LIMIT  0.2

#define SUN_LOWER_LIMIT  -0.1
#define SUN_UPPER_LIMIT  0.0

#define CLOUD_COVER  0.55
#define CLOUD_SHARPNESS  0.005

float hash( float n )
{
	return fract(sin(n)*43758.5453);
}

float noise( in vec2 x )
{
	vec2 p = floor(x);
	vec2 f = fract(x);
    	f = f*f*(3.0-2.0*f);
    	float n = p.x + p.y*57.0;
    	float res = mix(mix( hash(n+  0.0), hash(n+  1.0),f.x), mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y);
    	return res;
}

float fbm( vec2 p )
{
    	float f = 0.0;
    	f += 0.50000*noise( p ); p = p*2.02;
    	f += 0.25000*noise( p ); p = p*2.03;
    	f += 0.12500*noise( p ); p = p*2.01;
    	f += 0.06250*noise( p ); p = p*2.04;
    	f += 0.03125*noise( p );
    	return f/0.984375;
}

float clouds(float tt){
	vec2 wind_vec = vec2(0.001 + tt*0.01, 0.003 + tt * 0.01);
	
	// Set up domain
	vec2 q = (textureCoords.xz);
	vec2 p = -1.0 + 3.0 * q + wind_vec;
	
	float f = fbm( 2.0*p );

	float cover = CLOUD_COVER;
	float sharpness = CLOUD_SHARPNESS;
	
	float c = f - (1.0 - cover);
	if ( c < 0.0 )
		c = 0.0;
	f = 1.0 - (pow(sharpness, c));
	return f;
}

void main(void){
/*
	float tt = time / 10;

	float f = clouds(tt);
 */   
    float factor = (LOWER_LIMIT - textureCoords.y) / (LOWER_LIMIT - UPPER_LIMIT);
    float factorSun = clamp((textureCoords.y - SUN_LOWER_LIMIT) / (SUN_UPPER_LIMIT - SUN_LOWER_LIMIT), 0.0, 1.0);
    vec4 finalColour = vec4(fogColour, 1.0);
    
    vec4 skyTop = vec4(fogColour.r *0.4, fogColour.g *0.4, fogColour.b, 0.0);
    
    finalColour = mix(finalColour, skyTop, factor);
    
 	vec3 V = normalize(pass_normal);
    vec3 L = normalize(lightPosition);

    float vl = dot(V, L);

    finalColour *=  max(dot(vec3(0,1,0),L),0.002);
	finalColour = mix (vec4(0.0), finalColour, factorSun);
	if(vl > 0.999) 
		finalColour = mix(finalColour, mix(finalColour, vec4(1.0), (0.999 - vl) / (0.999 - 0.9991)), factorSun);
	

    out_Color[0] = finalColour;
    out_Color[1] = vec4(pass_position.xyz * 10, 0);
    out_Color[2] = vec4(0.0);
    out_Color[3] = vec4(0.0);
    out_Color[4] = vec4(1 * ((0.9 - vl) / (0.9 - 0.9991)),0,0,1);
}