/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Guerra24
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

package net.guerra24.voxel.client.core;

import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

import net.guerra24.voxel.client.api.API;
import net.guerra24.voxel.client.bootstrap.Bootstrap;
import net.guerra24.voxel.client.graphics.opengl.Display;
import net.guerra24.voxel.client.network.DedicatedClient;
import net.guerra24.voxel.client.resources.GameResources;
import net.guerra24.voxel.client.resources.GuiResources;
import net.guerra24.voxel.client.util.Logger;
import net.guerra24.voxel.client.world.InfinityWorld;
import net.guerra24.voxel.client.world.WorldsHandler;
import net.guerra24.voxel.client.world.block.BlocksResources;

/**
 * The Kernel, Game Engine Core
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @category Kernel
 */
public class Voxel {

	/**
	 * Game Threads
	 */
	public static UpdateThread worldThread2;

	/**
	 * Game Data
	 */
	private GameResources gameResources;
	private GuiResources guiResources;
	private WorldsHandler worldsHandler;
	private Display display;
	private API api;
	private DedicatedClient client;

	/**
	 * Constructor of the Kernel, Initializes the Game and starts the loop
	 */
	public Voxel() {
		mainLoop();
	}

	/**
	 * PreInit phase, initialize the display and runs the API PreInit
	 */
	public void preInit() {
		display = new Display();
		display.initDsiplay(VoxelVariables.WIDTH, VoxelVariables.HEIGHT);
		display.startUp();
		Logger.log("Loading");
		Logger.log("Voxel Version: " + VoxelVariables.version);
		Logger.log("Build: " + VoxelVariables.build);
		Logger.log("Running on: " + Bootstrap.getPlatform());
		Logger.log("LWJGL Version: " + Version.getVersion());
		Logger.log("GLFW Version: " + GLFW.glfwGetVersionString());
		Logger.log("OpenGL Version: " + glGetString(GL_VERSION));
		Logger.log("Vendor: " + glGetString(GL_VENDOR));
		Logger.log("Renderer: " + glGetString(GL_RENDERER));
		if (Bootstrap.getPlatform() == Bootstrap.Platform.MACOSX) {
			VoxelVariables.runningOnMac = true;
			VoxelVariables.useHQWater = false;
			VoxelVariables.useShadows = false;
		}
		gameResources = new GameResources();
		api = new API();
		api.preInit();
	}

	/**
	 * Init phase, initialize the game data (models,textures,music,etc) and runs
	 * the API Init
	 */
	private void init() {
		gameResources.init();
		guiResources = new GuiResources(gameResources);
		BlocksResources.createBlocks(gameResources.getLoader());
		gameResources.addRes();
		gameResources.music();
		worldsHandler = new WorldsHandler();
		InfinityWorld world = new InfinityWorld();
		worldsHandler.registerWorld(world.getCodeName(), world);
		worldsHandler.setActiveWorld("Infinity");
		Logger.log("Initializing Threads");
		worldThread2 = new UpdateThread(this);
		worldThread2.setName("Voxel World 1");
		worldThread2.start();
		/*
		 * new Thread(new Runnable() { public void run() {
		 * Thread.currentThread().setName("Voxel-Client"); client = new
		 * DedicatedClient(gameResources); } }).start();
		 */api.setMobManager(gameResources.getPhysics().getMobManager());
		api.init();
	}

	/**
	 * PostInit phase, starts music and runs the API PostInit
	 */
	private void postInit() {
		api.postInit();
		gameResources.getSoundSystem().play("menu1");
	}

	/**
	 * Voxel Main Loop
	 */
	public void mainLoop() {
		preInit();
		init();
		postInit();
		float delta = 0;
		while (gameResources.getGlobalStates().loop) {
			if (Display.timeCountRender > 1f) {
				Logger.log("FPS: " + Display.fps);
				Logger.log("UPS: " + Display.ups);
				Display.fps = Display.fpsCount;
				Display.fpsCount = 0;
				Display.ups = Display.upsCount;
				Display.upsCount = 0;
				Display.timeCountRender -= 1f;
			}
			delta = Display.getDeltaRender();
			render(delta);
		}
		dispose();
	}

	/**
	 * Handles all render calls
	 * 
	 * @param delta
	 *            Delta value from Render Thread
	 */
	private void render(float delta) {
		Display.fpsCount++;
		gameResources.getGlobalStates().doRender(this, delta);
		gameResources.getTextMasterRenderer().render();
		display.updateDisplay(VoxelVariables.FPS, gameResources);
	}

	/**
	 * Handles all update calls
	 * 
	 * @param delta
	 *            Delta value from Update Thread
	 */
	public void update(float delta) {
		Display.upsCount++;
		gameResources.getGlobalStates().doUpdate(this, delta);
	}

	/**
	 * Disposes all game data
	 */
	public void dispose() {
		Logger.log("Closing Game");
		gameResources.cleanUp();
		api.dispose();
		display.closeDisplay();
	}

	public GameResources getGameResources() {
		return gameResources;
	}

	public GuiResources getGuiResources() {
		return guiResources;
	}

	public API getApi() {
		return api;
	}

	public DedicatedClient getClient() {
		return client;
	}

	public WorldsHandler getWorldsHandler() {
		return worldsHandler;
	}

	public Display getDisplay() {
		return display;
	}

}