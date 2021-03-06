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

package net.luxvacuos.voxel.client.ui.menus;

import static net.luxvacuos.voxel.universal.core.GlobalVariables.REGISTRY;

import net.luxvacuos.voxel.client.core.ClientVariables;
import net.luxvacuos.voxel.client.input.Mouse;
import net.luxvacuos.voxel.client.rendering.api.glfw.Window;
import net.luxvacuos.voxel.client.rendering.api.nanovg.WM;
import net.luxvacuos.voxel.client.ui.Alignment;
import net.luxvacuos.voxel.client.ui.Button;
import net.luxvacuos.voxel.client.ui.RootComponent;

public class PauseWindow extends RootComponent {

	public PauseWindow(float x, float y, float w, float h) {
		super(x, y, w, h, "Pause");
	}

	@Override
	public void initApp(Window window) {
		super.setBackgroundColor("#1F1F1F78");
		super.setAsBackground(true);

		Button backButton = new Button(0, 40, 200, 40, "Back to Main Menu");
		backButton.setAlignment(Alignment.CENTER);
		backButton.setWindowAlignment(Alignment.BOTTOM);
		backButton.setOnButtonPress(() -> {
			super.closeWindow();
			float borderSize = (float) REGISTRY.getRegistryItem("/Voxel/Settings/WindowManager/borderSize");
			float titleBarHeight = (float) REGISTRY.getRegistryItem("/Voxel/Settings/WindowManager/titleBarHeight");
			RootComponent mainMenu = new MainMenu(borderSize + 10, ClientVariables.HEIGHT - titleBarHeight - 10,
					ClientVariables.WIDTH - borderSize * 2f - 20,
					ClientVariables.HEIGHT - titleBarHeight - borderSize - 20);
			WM.getWM().addWindow(mainMenu);
			ClientVariables.exitWorld = true;
		});

		Button optionsButton = new Button(0, 100, 200, 40, "Options");
		optionsButton.setAlignment(Alignment.CENTER);
		optionsButton.setWindowAlignment(Alignment.BOTTOM);
		optionsButton.setOnButtonPress(() -> {
			WM.getWM().addWindow(new OptionsMenu(w / 2 - 420 + x, y - 40, 840, 600));
		});

		super.addComponent(backButton);
		super.addComponent(optionsButton);

		super.initApp(window);
	}

	@Override
	public void onClose() {
		ClientVariables.paused = false;
		Mouse.setGrabbed(true);
	}

}
