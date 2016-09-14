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

package net.luxvacuos.voxel.universal.resources;

import com.esotericsoftware.kryo.Kryo;

import net.luxvacuos.voxel.universal.core.AbstractGameSettings;
import net.luxvacuos.voxel.universal.core.AbstractVoxel;
import net.luxvacuos.voxel.universal.core.AbstractWorldSimulation;

public class AbstractGameResources implements IDisposable {

	protected AbstractGameSettings gameSettings;
	protected AbstractWorldSimulation worldSimulation;
	protected Kryo kryo;
	
	public void preInit() { }
	
	public void init(AbstractVoxel voxel) { }
	
	public void postInit() { }

	@Override
	public void dispose() { }
	
	public AbstractGameSettings getGameSettings() {
		return this.gameSettings;
	}
	
	public AbstractWorldSimulation getWorldSimulation() {
		return this.worldSimulation;
	}
	
	public Kryo getKryo() {
		return this.kryo;
	}

}