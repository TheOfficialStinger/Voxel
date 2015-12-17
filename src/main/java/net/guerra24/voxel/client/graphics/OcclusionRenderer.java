package net.guerra24.voxel.client.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.guerra24.voxel.client.graphics.shaders.EntityBasicShader;
import net.guerra24.voxel.client.resources.GameResources;
import net.guerra24.voxel.client.resources.models.TexturedModel;
import net.guerra24.voxel.client.world.block.BlockEntity;
import net.guerra24.voxel.universal.util.vector.Matrix4f;

public class OcclusionRenderer {
	private Map<TexturedModel, List<BlockEntity>> blockEntities = new HashMap<TexturedModel, List<BlockEntity>>();
	private EntityBasicShader shader;
	private ShadowRenderer renderer;

	/**
	 * Constructor, Initializes the OpenGL code, creates the projection matrix,
	 * entity renderer and skybox renderer
	 * 
	 * @param loader
	 *            Game Loader
	 */
	public OcclusionRenderer(Matrix4f projectionMatrix) {
		shader = new EntityBasicShader();
		renderer = new ShadowRenderer(shader, projectionMatrix);
	}

	/**
	 * Render the Chunk
	 * 
	 * @param cubes1temp
	 *            A list of BlockEntity
	 * @param lights1
	 *            A list of Lights
	 * @param camera
	 *            A Camera
	 */
	public void renderChunk(Queue<Object> cubes, GameResources gm) {
		for (Object entity : cubes) {
			if (entity instanceof BlockEntity)
				processBlockEntity((BlockEntity) entity);
		}
		renderBlocks(gm);
	}

	/**
	 * Chunk Rendering PipeLine
	 * 
	 * @param lights
	 *            A list of lights
	 * @param camera
	 *            A Camera
	 */
	private void renderBlocks(GameResources gm) {
		shader.start();
		shader.loadviewMatrix(gm.getCamera());
		shader.loadProjectionMatrix(gm.getRenderer().getProjectionMatrix());
		renderer.renderBlockEntity(blockEntities, gm);
		shader.stop();
		blockEntities.clear();
	}

	/**
	 * Add the BlockEntity to the batcher map
	 * 
	 * @param BlockEntity
	 *            An Entity
	 */
	private void processBlockEntity(BlockEntity entity) {
		TexturedModel entityModel = entity.getModel();
		List<BlockEntity> batch = blockEntities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<BlockEntity> newBatch = new ArrayList<BlockEntity>();
			newBatch.add(entity);
			blockEntities.put(entityModel, newBatch);
		}
	}

	/**
	 * Clear the Shader
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

}
