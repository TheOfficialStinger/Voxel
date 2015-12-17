package net.guerra24.voxel.client.graphics;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import net.guerra24.voxel.client.graphics.shaders.EntityBasicShader;
import net.guerra24.voxel.client.resources.GameResources;
import net.guerra24.voxel.client.resources.models.RawModel;
import net.guerra24.voxel.client.resources.models.TexturedModel;
import net.guerra24.voxel.client.util.Maths;
import net.guerra24.voxel.client.world.block.BlockEntity;
import net.guerra24.voxel.universal.util.vector.Matrix4f;

public class ShadowRenderer {

	private EntityBasicShader shader;

	/**
	 * Constructor, initializes the shaders and the projection matrix
	 * 
	 * @param shader
	 *            Entity Shader
	 * @param projectionMatrix
	 *            A Matrix4f Projection
	 */
	public ShadowRenderer(EntityBasicShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
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
	}

	/**
	 * UnBinds the VAOs
	 * 
	 */
	private void unbindTexturedModel() {
		glDisableVertexAttribArray(0);
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

}
