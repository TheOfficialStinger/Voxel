/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Guerra24
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

package net.guerra24.voxel.client.graphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import net.guerra24.voxel.client.graphics.shaders.EntityShader;
import net.guerra24.voxel.client.resources.GameResources;
import net.guerra24.voxel.client.resources.models.RawModel;
import net.guerra24.voxel.client.resources.models.TexturedModel;
import net.guerra24.voxel.client.util.Maths;
import net.guerra24.voxel.client.world.block.BlockEntity;
import net.guerra24.voxel.client.world.entities.GameEntity;
import net.guerra24.voxel.universal.util.vector.Matrix4f;

/**
 * Entity Rendering
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @category Rendering
 */
public class EntityRenderer {
	/**
	 * Entity Shader
	 */
	private EntityShader shader;

	/**
	 * Constructor, initializes the shaders and the projection matrix
	 * 
	 * @param shader
	 *            Entity Shader
	 * @param projectionMatrix
	 *            A Matrix4f Projection
	 */
	public EntityRenderer(EntityShader shader, GameResources gm, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.loadBiasMatrix(gm);
		shader.stop();
	}

	/**
	 * Render the block entity's in the list
	 * 
	 * @param blockEntities
	 *            A List of entity's
	 */
	public void renderBlockEntity(Map<TexturedModel, List<BlockEntity>> blockEntities, GameResources gm) {
		for (TexturedModel model : blockEntities.keySet()) {
			prepareTexturedModel(model, gm);
			List<BlockEntity> batch = blockEntities.get(model);
			for (BlockEntity entity : batch) {
				shader.loadEntityLight(entity.getLocalLight());
				prepareInstance(entity);
				glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	/**
	 * Render the entity's in the list
	 * 
	 * @param blockEntities
	 *            A List of entity's
	 */
	public void renderEntity(Map<TexturedModel, List<GameEntity>> blockEntities, GameResources gm) {
		for (TexturedModel model : blockEntities.keySet()) {
			prepareTexturedModel(model, gm);
			List<GameEntity> batch = blockEntities.get(model);
			for (GameEntity entity : batch) {
				shader.loadEntityLight(1);
				prepareInstance(entity);
				glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}

	}

	/**
	 * Prepares the Entity Textured Model and binds the VAOs
	 * 
	 * @param model
	 */
	private void prepareTexturedModel(TexturedModel model, GameResources gm) {
		RawModel rawmodel = model.getRawModel();
		glBindVertexArray(rawmodel.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, model.getTexture().getID());
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, gm.getMasterShadowRenderer().getFbo().getTexture());
	}

	/**
	 * UnBinds the VAOs
	 * 
	 */
	private void unbindTexturedModel() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
	}

	/**
	 * Prepares the Textured Model Translation, Rotation and Scale
	 * 
	 * @param entity
	 */
	private void prepareInstance(BlockEntity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);

	}

	/**
	 * Prepares the Textured Model Translation, Rotation and Scale
	 * 
	 * @param entity
	 */
	private void prepareInstance(GameEntity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
