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

package net.luxvacuos.voxel.client.ui;

import net.luxvacuos.voxel.client.input.Mouse;
import net.luxvacuos.voxel.client.rendering.api.glfw.Window;
import net.luxvacuos.voxel.client.rendering.api.nanovg.NRendering;

public class EditBox extends Component {
	private String text, font = "Poppins-Medium";
	private float fontSize = 20f;
	private boolean selected = false;

	public EditBox(float x, float y, float width, float height, String text) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
		this.text = text;
	}

	@Override
	public void render(Window window) {
		NRendering.renderEditBox(window.getNVGID(), text, font, rootComponent.rootX + alignedX,
				window.getHeight() - rootComponent.rootY - alignedY - h, w, h, fontSize, selected);
	}

	@Override
	public void update(float delta, Window window) {
		if (Mouse.isButtonDown(0)) {
			if (insideBox()) {
				window.getKeyboardHandler().enableTextInput();
				window.getKeyboardHandler().clearInputData();
				selected = true;
			} else {
				selected = false;
			}
		}
		if (selected)
			text = window.getKeyboardHandler().handleInput(text);
		super.update(delta, window);
	}

	public boolean insideBox() {
		return Mouse.getX() > rootComponent.rootX + alignedX && Mouse.getY() > rootComponent.rootY + alignedY
				&& Mouse.getX() < rootComponent.rootX + alignedX + w
				&& Mouse.getY() < rootComponent.rootY + alignedY + h;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
