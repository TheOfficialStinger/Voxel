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

package net.luxvacuos.voxel.universal.world.chunk;

import net.luxvacuos.voxel.universal.resources.IDisposable;
import net.luxvacuos.voxel.universal.world.block.IBlockHandle;
import net.luxvacuos.voxel.universal.world.dimension.IDimensionHandle;
import net.luxvacuos.voxel.universal.world.utils.ChunkNode;
import net.luxvacuos.voxel.universal.world.utils.IHandle;

public interface IChunkHandle extends IHandle, IDisposable {
	
	public ChunkNode getNode();

	public int getX();

	public int getZ();

	public IBlockHandle getBlockAt(int x, int y, int z);
	
	public IDimensionHandle getDimension();

}
