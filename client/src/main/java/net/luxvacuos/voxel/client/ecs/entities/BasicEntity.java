package net.luxvacuos.voxel.client.ecs.entities;

import net.luxvacuos.voxel.client.ecs.components.Renderable;
import net.luxvacuos.voxel.client.rendering.api.opengl.objects.TexturedModel;
import net.luxvacuos.voxel.universal.ecs.components.Position;
import net.luxvacuos.voxel.universal.ecs.components.Rotation;
import net.luxvacuos.voxel.universal.ecs.components.Scale;
import net.luxvacuos.voxel.universal.ecs.entities.AbstractEntity;

public class BasicEntity extends AbstractEntity {

	public BasicEntity(TexturedModel model) {
		add(new Position());
		add(new Rotation());
		add(new Renderable(model));
		add(new Scale());
	}

}
