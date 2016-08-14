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

package net.luxvacuos.voxel.server.core;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.voxel.server.bootstrap.Bootstrap;
import net.luxvacuos.voxel.server.core.states.GameMPState;
import net.luxvacuos.voxel.server.resources.ServerGameResources;
import net.luxvacuos.voxel.server.ui.UserInterface;
import net.luxvacuos.voxel.server.world.DefaultWorld;
import net.luxvacuos.voxel.universal.api.ModInitialization;
import net.luxvacuos.voxel.universal.api.MoltenAPI;
import net.luxvacuos.voxel.universal.core.AbstractVoxel;
import net.luxvacuos.voxel.universal.core.RunningSide;
import net.luxvacuos.voxel.universal.core.states.StateMachine;
import net.luxvacuos.voxel.universal.network.packets.WorldTime;

public class Voxel extends AbstractVoxel {

	private int port;

	private ModInitialization api;

	public Voxel(int port) throws Exception {
		this.port = port;
		this.prefix = "";
		loop();
	}

	@Override
	public void preInit() throws Exception {
		try {
			Manifest manifest = new Manifest(getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
			Attributes attr = manifest.getMainAttributes();
			String t = attr.getValue("Specification-Version");
			if (t != null)
				VoxelVariables.version = t;
		} catch (IOException E) {
			E.printStackTrace();
		}
		
		StateMachine.registerState(new GameMPState());
		
		gameResources = ServerGameResources.getInstance();
		getGameResources().construct(this, port);
		getGameResources().getUserInterface().getThreadUI().start();
		while (!getGameResources().getUserInterface().isStarted())
			Thread.sleep(100);
		Logger.log("Starting Server");
		Logger.log("Voxel Server Version: " + VoxelVariables.version);
		Logger.log("Molten API Version: " + MoltenAPI.apiVersion);
		Logger.log("Running on: " + Bootstrap.getPlatform());

		getGameResources().preInit();
		api = new ModInitialization(this);
		api.preInit();
	}

	@Override
	public void init() throws Exception {
		this.gameResources.init(this);
		api.init();
	}

	@Override
	public void postInit() throws Exception {
		this.gameResources.postInit();
		api.postInit();
		getGameResources().getVoxelServer().connect();
		getGameResources().getUserInterface();
		UserInterface.setReady(true);
		ServerGameResources.getInstance().getWorldsHandler().registerWorld(new DefaultWorld("world"));
		ServerGameResources.getInstance().getWorldsHandler().setActiveWorld("world");
		ServerGameResources.getInstance().getWorldsHandler().getActiveWorld().init();
	}

	@Override
	public void loop() {
		try {
			preInit();
			init();
			postInit();
			float delta = 0;
			float accumulator = 0f;
			float interval = 1f / VoxelVariables.UPS;
			StateMachine.setCurrentState("GameMPState");
			StateMachine.run();
			while (StateMachine.isRunning()) {
				if (getGameResources().getCoreUtils().getTimeCount() > 1f) {
					CoreInfo.ups = CoreInfo.upsCount;
					CoreInfo.upsCount = 0;
					getGameResources().getCoreUtils()
							.setTimeCount(getGameResources().getCoreUtils().getTimeCount() - 1f);
				}
				delta = getGameResources().getCoreUtils().getDelta();
				accumulator += delta;
				while (accumulator >= interval) {
					update(interval);
					accumulator -= interval;
				}
				getGameResources().getVoxelServer().getServer()
						.sendToAllTCP(new WorldTime(getGameResources().getWorldSimulation().getTime()));
				getGameResources().getCoreUtils().sync(VoxelVariables.UPS);
			}
			dispose();
		} catch (Throwable t) {
			handleError(t);
		}
	}

	@Override
	public void handleError(Throwable e) {
		// TODO: Implement This
	}

	@Override
	public void update(float delta) {
		CoreInfo.upsCount++;
		StateMachine.update(this, delta);
	}

	@Override
	public void dispose() {
		Logger.log("Stopping Server");
		this.getGameResources().getWorldsHandler().getActiveWorld().dispose();
		this.gameResources.dispose();
	}

	public ModInitialization getApi() {
		return api;
	}

	@Override
	public ServerGameResources getGameResources() {
		return ((ServerGameResources) gameResources);
	}

	@Override
	public RunningSide getSide() {
		return RunningSide.SERVER;
	}

}
