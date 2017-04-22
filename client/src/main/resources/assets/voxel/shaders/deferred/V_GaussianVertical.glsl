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

in vec2 position;

out vec2 blurTexCoords[17];

uniform vec2 resolution;

void main(void){
	gl_Position = vec4(position, -0.8, 1.0);
	vec2 textureCoords = vec2((position.x+1.0)/2.0, (position.y+1.0)/2.0);
	
	vec2 pixelSize = 1.0 / resolution;
	
	for(int i = -9; i <= 9; i++){
		blurTexCoords[i+9] = textureCoords + vec2(0.0, pixelSize.y * i);
	}
}