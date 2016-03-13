/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Lux Vacuos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.luxvacuos.voxel.client.rendering.api.opengl;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.Queue;

import net.luxvacuos.voxel.client.core.VoxelVariables;
import net.luxvacuos.voxel.client.rendering.api.opengl.shaders.WaterBasicShader;
import net.luxvacuos.voxel.client.rendering.api.opengl.shaders.WaterShader;
import net.luxvacuos.voxel.client.resources.GameResources;
import net.luxvacuos.voxel.client.resources.Loader;
import net.luxvacuos.voxel.client.resources.models.RawModel;
import net.luxvacuos.voxel.client.resources.models.WaterTile;
import net.luxvacuos.voxel.client.util.Maths;
import net.luxvacuos.voxel.universal.util.vector.Matrix4f;
import net.luxvacuos.voxel.universal.util.vector.Vector3f;

/**
 * Water Renderer
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @category Rendering
 */
public class WaterRenderer {
	/**
	 * Water Data
	 */
	private RawModel quad;
	private WaterShader shader;
	private WaterBasicShader basicShader;
	private float moveFactor = 0;

	/**
	 * Constructor, Initializes the Water Shaders, Textures and VAOs
	 * 
	 * @param loader
	 *            Game Loader
	 * @param shader
	 *            Water Shader
	 * @param projectionMatrix
	 *            A Matrix4f Projection
	 */
	public WaterRenderer(GameResources gm, Matrix4f projectionMatrix) {
		shader = new WaterShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadBiasMatrix(gm);
		shader.stop();
		basicShader = new WaterBasicShader();
		basicShader.start();
		basicShader.loadProjectionMatrix(projectionMatrix);
		basicShader.stop();
		setUpVAO(gm.getLoader());
	}

	/**
	 * Renders the Water Tiles in the List
	 * 
	 * @param waters
	 *            A list of Water Tiles
	 * @param camera
	 *            A Camera
	 */
	public void render(Queue<WaterTile> waters, GameResources gm) {
		prepareRender(gm);
		for (WaterTile tile : waters) {
			Matrix4f modelMatrix = Maths.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE);
			shader.loadModelMatrix(modelMatrix);
			glDrawArrays(GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbind();
	}

	/**
	 * Renders the Water Tiles in the List
	 * 
	 * @param waters
	 *            A list of Water Tiles
	 * @param camera
	 *            A Camera
	 */
	public void renderOcclusion(Queue<WaterTile> waters, GameResources gm) {
		prepareRenderOcclusion(gm);
		for (WaterTile tile : waters) {
			Matrix4f modelMatrix = Maths.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE);
			basicShader.loadModelMatrix(modelMatrix);
			glDrawArrays(GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbindOcclusion();
	}

	/**
	 * Water Tile Prepare PipeLine
	 * 
	 * @param camera
	 *            A Camera
	 */
	private void prepareRender(GameResources gm) {
		shader.start();
		shader.loadProjectionMatrix(gm.getRenderer().getProjectionMatrix());
		shader.loadViewMatrix(gm.getCamera());
		shader.loadMoveFactor(moveFactor);
		shader.loadLightMatrix(gm);
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, gm.getMasterShadowRenderer().getFbo().getTexture());

	}

	/**
	 * Water Tile Prepare PipeLine
	 * 
	 * @param camera
	 *            A Camera
	 */
	private void prepareRenderOcclusion(GameResources gm) {
		basicShader.start();
		basicShader.loadViewMatrix(gm.getCamera());
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
	}

	/**
	 * Updates the water
	 * 
	 * @param delta
	 *            Delta
	 */
	public void update(float delta) {
		moveFactor += VoxelVariables.WAVE_SPEED * delta;
		moveFactor %= 6.3f;
	}

	/**
	 * Unbinds the VAOs
	 * 
	 */
	private void unbind() {
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
	}

	private void unbindOcclusion() {
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		basicShader.stop();
	}

	/**
	 * Creates the VAOs
	 * 
	 * @param loader
	 *            Game Loader
	 */
	private void setUpVAO(Loader loader) {
		float[] vertices = { -1, -1, -1, 0, 0, -1,

				-1, 0, 0, 0, 0, -1,

				-1, 0, -1, 1, 0, 0,

				-1, 1, 0, 1, 0, 0,

				0, 0, 0, 1, 1, 0,

				0, 1, 1, 1, 1, 0,

				0, -1, 0, 0, 1, -1,

				0, 0, 1, 0, 1, -1

		};
		quad = loader.loadToVAO(vertices, 2);
	}

	public float getMoveFactor() {
		return moveFactor;
	}

}