package net.voxel.world.tiles;

import java.util.HashMap;

import net.voxel.world.tiles.types.TileAir;
import net.voxel.world.tiles.types.TileCrackedStone;
import net.voxel.world.tiles.types.TileGrass;
import net.voxel.world.tiles.types.TileVoid;

import com.nishu.utils.Color4f;

public abstract class Tile {
	
	/* 
	 * Texture coords for multi-textured tiles
	 * bottom - first
	 * top - second
	 * front - third
	 * back - fourth
	 * left - fifth
	 * right - sixth
	 */
	
	public static HashMap<Byte, Tile> tileMap = new HashMap<Byte, Tile>();
	
	public static Tile Air = new TileAir();
	public static Tile Void = new TileVoid();
	public static Tile Grass = new TileGrass();
	public static Tile CrackedStone = new TileCrackedStone();

	public abstract byte getId();
	public abstract Color4f getColor();
	public abstract float[] getTexCoords();
	
	public static Tile getTile(byte id) {
		return tileMap.get(id);
	}
	
	public static void createTileMap() {
		tileMap.put((byte) -1, Air);
		tileMap.put((byte) 0, Void);
		tileMap.put((byte) 1, Grass);
		tileMap.put((byte) 2, CrackedStone);
	}
}
