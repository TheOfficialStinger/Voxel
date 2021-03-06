/*
 * This file is part of Voxel
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
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

package net.luxvacuos.voxel.client.rendering.api.opengl.shaders;

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.voxel.client.core.ClientVariables;
import net.luxvacuos.voxel.client.ecs.entities.CameraEntity;
import net.luxvacuos.voxel.client.rendering.api.opengl.shaders.data.Attribute;
import net.luxvacuos.voxel.client.rendering.api.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.voxel.client.rendering.api.opengl.shaders.data.UniformSampler;
import net.luxvacuos.voxel.client.util.Maths;

public class EntityBasicShader extends ShaderProgram {

	private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	private UniformSampler texture0 = new UniformSampler("texture0");

	public EntityBasicShader() {
		super(ClientVariables.VERTEX_FILE_ENTITY_BASIC, ClientVariables.FRAGMENT_FILE_ENTITY_BASIC,
				new Attribute(0, "position"), new Attribute(1, "textureCoords"));
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, texture0);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		texture0.loadTexUnit(0);
		super.stop();
	}

	/**
	 * Loads Transformation Matrixd to the shader
	 * 
	 * @param matrix
	 *            Transformation Matrixd
	 */
	public void loadTransformationMatrix(Matrix4d matrix) {
		transformationMatrix.loadMatrix(matrix);
	}

	/**
	 * Loads View Matrixd to the shader
	 * 
	 * @param camera
	 *            Camera
	 */
	public void loadviewMatrix(CameraEntity camera) {
		viewMatrix.loadMatrix(Maths.createViewMatrix(camera));
	}

	/**
	 * Loads Projection Matrixd to the shader
	 * 
	 * @param projection
	 *            Projection Matrixd
	 */
	public void loadProjectionMatrix(Matrix4d projection) {
		projectionMatrix.loadMatrix(projection);
	}

}
