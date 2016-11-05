/*
 * This file is part of Voxel
 * 
 * Copyright (C) 2016 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.voxel.client.rendering.api.opengl.shaders.data;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class UniformArray implements IUniform {

	protected String[] names;
	private int[] location;

	protected UniformArray(String... names) {
		this.names = names;
		location = new int[names.length];
	}

	@Override
	public void storeUniformLocation(int programID) {
		for (int x = 0; x < names.length; x++) {
			location[x] = glGetUniformLocation(programID, names[x]);
		}
	}
	
	protected int[] getLocation() {
		return location;
	}

}