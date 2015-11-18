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

package net.guerra24.voxel.client.resources;

import net.guerra24.voxel.client.resources.models.GuiTexture;
import net.guerra24.voxel.universal.util.vector.Vector2f;

/**
 * Gui Resources
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @category Assets
 */
public class GuiResources {

	/**
	 * GuiResources Data
	 */
	private GuiTexture worldLoading;
	private GuiTexture loadBar;
	public GuiTexture button5;

	/**
	 * Constructor
	 * 
	 */
	public GuiResources(GameResources gm) {
		loadGuiTexture(gm);
		addGuiTextures(gm);
	}

	/**
	 * Load all Gui Assets
	 * 
	 * @param gm
	 *            Game Resources
	 */
	public void loadGuiTexture(GameResources gm) {
		worldLoading = new GuiTexture(gm.getLoader().loadTextureGui("WorldLoading"), new Vector2f(0.6f, -0.425f),
				new Vector2f(1.6f, 1.425f));
		loadBar = new GuiTexture(gm.getLoader().loadTextureGui("LoadBar"), new Vector2f(-0.3f, 0.15f),
				new Vector2f(0.0f, 0.1f));
		button5 = new GuiTexture(gm.getLoader().loadTextureGui("ButtonExit"), new Vector2f(0.0f, -0.7f),
				new Vector2f(0.2f, 0.12f));
	}

	/**
	 * Sends GUIs to the List
	 * 
	 * @param gm
	 *            Game Resources
	 */
	public void addGuiTextures(GameResources gm) {
		gm.guis3.add(worldLoading);
		gm.guis3.add(loadBar);
		gm.guis4.add(button5);
	}
}
