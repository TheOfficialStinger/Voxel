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

package io.github.guerra24.voxel.client.kernel.world.entities;

import io.github.guerra24.voxel.client.kernel.resources.GameResources;
import io.github.guerra24.voxel.client.kernel.resources.GuiResources;
import io.github.guerra24.voxel.client.kernel.resources.models.TexturedModel;
import io.github.guerra24.voxel.client.kernel.util.vector.Vector3f;

public class Player extends Entity implements IEntity {
	private static final float GRAVITY = -10;
	private static final float JUMP_POWER = 4;

	private float upwardsSpeed = 0;

	private boolean isInAir = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	@Override
	public void update(float delta, GameResources gm, GuiResources gi) {
		super.increasePosition(0, upwardsSpeed * delta, 0);
		if (false) {
			upwardsSpeed += GRAVITY * delta;
			isInAir = true;
		} else {
			upwardsSpeed = 0;
			isInAir = false;
		}
	}

	public void jump() {
		if (!isInAir) {
			try {
				if (false) {
					this.upwardsSpeed = JUMP_POWER;

					isInAir = true;
				} else {
					upwardsSpeed = 0;
					isInAir = false;
				}
			} finally {
			}
		}
	}

	@Override
	public Entity getEntity() {
		return this;
	}
}
