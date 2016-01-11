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

package net.guerra24.voxel.client.core.states;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import net.guerra24.voxel.client.core.GlobalStates;
import net.guerra24.voxel.client.core.State;
import net.guerra24.voxel.client.core.Voxel;
import net.guerra24.voxel.client.core.VoxelVariables;
import net.guerra24.voxel.client.graphics.opengl.Display;
import net.guerra24.voxel.client.particle.ParticleMaster;
import net.guerra24.voxel.client.resources.GameResources;
import net.guerra24.voxel.client.world.WorldsHandler;

/**
 * Single Player GameState
 * 
 * @author danirod
 * @category Kernel
 */
public class GameSPState implements State {

	@Override
	public void update(Voxel voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		WorldsHandler worlds = voxel.getWorldsHandler();

		gm.getCamera().update(delta, gm, worlds.getActiveWorld());
		worlds.getActiveWorld().updateChunksGeneration(gm, delta);

		gm.getPhysicsEngine().update(delta);

		gm.update(gm.getSkyboxRenderer().update(delta));
		gm.getRenderer().getWaterRenderer().update(delta);
		ParticleMaster.getInstance().update(delta, gm.getCamera());

		// if (!display.isDisplayFocused()) {
		// gm.getCamera().unlockMouse();
		// states.setState(GameState.IN_PAUSE);
		// }
	}

	@Override
	public void render(Voxel voxel, GlobalStates states, float delta) {
		GameResources gm = voxel.getGameResources();
		WorldsHandler worlds = voxel.getWorldsHandler();

		worlds.getActiveWorld().lighting();
		gm.getSun_Camera().setPosition(gm.getCamera().getPosition());
		gm.getFrustum().calculateFrustum(gm.getMasterShadowRenderer().getProjectionMatrix(), gm.getSun_Camera());
		if (VoxelVariables.useShadows) {
			gm.getMasterShadowRenderer().being();
			gm.getRenderer().prepare();
			worlds.getActiveWorld().updateChunksShadow(gm);
			gm.getMasterShadowRenderer().renderEntity(gm.getPhysicsEngine().getEntities(), gm);
			gm.getMasterShadowRenderer().end();
		}
		gm.getFrustum().calculateFrustum(gm.getRenderer().getProjectionMatrix(), gm.getCamera());
		gm.getRenderer().prepare();
		worlds.getActiveWorld().updateChunksOcclusion(gm);

		gm.getDeferredShadingRenderer().getPost_fbo().begin();
		gm.getRenderer().prepare();
		gm.getSkyboxRenderer().render(VoxelVariables.RED, VoxelVariables.GREEN, VoxelVariables.BLUE, delta, gm);
		worlds.getActiveWorld().updateChunksRender(gm);
		FloatBuffer p = BufferUtils.createFloatBuffer(1);
		glReadPixels(Display.getWidth() / 2, Display.getHeight() / 2, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, p);
		gm.getCamera().depth = p.get(0);
		gm.getRenderer().renderEntity(gm.getPhysicsEngine().getEntities(), gm);
		gm.getDeferredShadingRenderer().getPost_fbo().end();

		gm.getRenderer().prepare();
		gm.getDeferredShadingRenderer().render(gm);
		ParticleMaster.getInstance().render(gm.getCamera());

	}

}
