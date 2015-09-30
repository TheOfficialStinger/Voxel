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

import io.github.guerra24.voxel.client.kernel.resources.GameControllers;
import io.github.guerra24.voxel.client.kernel.resources.GuiResources;
import io.github.guerra24.voxel.client.kernel.resources.models.TexturedModel;
import io.github.guerra24.voxel.client.kernel.util.vector.Vector3f;
import io.github.guerra24.voxel.client.kernel.world.DimensionalWorld;

public class Mob extends Entity implements IEntity {

	public Mob(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public Mob(Entity entity) {
		super(entity.getModel(), entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(),
				entity.getScale());
	}

	@Override
	public void update(float delta, GameControllers gm, GuiResources gi, DimensionalWorld world) {
	}

	@Override
	public Entity getEntity() {
		return this;
	}

}
