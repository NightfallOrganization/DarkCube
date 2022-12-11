/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.MathHelper;
import eu.darkcube.system.commandapi.v3.Vector2f;
import eu.darkcube.system.commandapi.v3.Vector3d;
import eu.darkcube.system.commandapi.v3.arguments.EntityAnchorArgument;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.commandapi.v3.arguments.ILocationArgument;
import eu.darkcube.system.commandapi.v3.arguments.RotationArgument;
import eu.darkcube.system.commandapi.v3.arguments.Vec3Argument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class TeleportCommand extends PServerExecutor {

	public static final SimpleCommandExceptionType TOO_MANY_ENTITIES = eu.darkcube.system.commandapi.v3.Message.TOO_MANY_ENTITIES.newSimpleCommandExceptionType();

	public TeleportCommand() {
		super("teleport", new String[] {
						"tp"
		}, b -> b.then(Commands.argument("targets", EntityArgument.entities()).executes(source -> {
			// When tp @e[limit=1] for example
			return TeleportCommand.teleportToSingleEntity(source.getSource(), EntityArgument.getEntities(source, "targets"));
		}).then(Commands.argument("destination", EntityArgument.entity()).executes(source -> {
			return TeleportCommand.teleportToEntity(source.getSource(), EntityArgument.getEntities(source, "targets"), EntityArgument.getEntity(source, "destination"));
		})).then(Commands.argument("location", Vec3Argument.vec3()).executes(source -> {
			return TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, null);
		}).then(Commands.argument("rotation", RotationArgument.rotation()).executes(source -> {
			return TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), RotationArgument.getRotation(source, "rotation"), null);
		})).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("facingEntity", EntityArgument.entity()).executes(source -> {
			return TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, new Facing(
							EntityArgument.getEntity(source, "facingEntity"),
							EntityAnchorArgument.Type.FEET));
		}).then(Commands.argument("facingAnchor", EntityAnchorArgument.entityAnchor()).executes(source -> {
			return TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, new Facing(
							EntityArgument.getEntity(source, "facingEntity"),
							EntityAnchorArgument.getEntityAnchor(source, "facingAnchor")));
		})))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes(source -> {
			return TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, new Facing(
							Vec3Argument.getVec3(source, "facingLocation")));
		})))))
//						.then(Commands.argument("destination", EntityArgument.entity()).executes(source -> {
//			return teleportToEntity(source.getSource(), Collections.singleton(source.getSource().assertIsEntity()), EntityArgument.getEntity(source, "destination"));
//		}))
						.then(Commands.argument("location", Vec3Argument.vec3()).executes(source -> {
							return TeleportCommand.teleportToPos(source.getSource(), Collections.singleton(source.getSource().assertIsEntity()), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, null);
						}).then(Commands.argument("rotation", RotationArgument.rotation()).executes(source -> {
							return TeleportCommand.teleportToPos(source.getSource(), Collections.singleton(source.getSource().assertIsEntity()), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), RotationArgument.getRotation(source, "rotation"), null);
						}))));
	}

	private static int teleportToSingleEntity(CommandSource source,
					Collection<? extends Entity> destinations)
					throws CommandSyntaxException {
		if (destinations.size() > 1) {
			throw TeleportCommand.TOO_MANY_ENTITIES.create();
		}
		Entity destination = destinations.stream().findAny().get();
		source.assertIsEntity().teleport(destination);
		source.sendFeedback(Message.TELEPORTED_TO_ENTITY_SINGLE.getMessage(source, source.assertIsEntity().getName(), destination.getName()), true);
		return 0;
	}

	private static int teleportToEntity(CommandSource source,
					Collection<? extends Entity> targets, Entity destination) {
		Location loc = destination.getLocation();
		for (Entity entity : targets) {
			TeleportCommand.teleport(source, entity, destination.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), null);
		}
		if (targets.size() == 1) {
			source.sendFeedback(Message.TELEPORTED_TO_ENTITY_SINGLE.getMessage(source, targets.stream().findAny().get().getName(), destination.getName()), true);
		} else {
			source.sendFeedback(Message.TELEPORTED_TO_ENTITY_MULTIPLE.getMessage(source, targets.size(), destination.getName()), true);
		}
		return targets.size();
	}

	public static int teleportToPos(CommandSource source,
					Collection<? extends Entity> targets, World world,
					ILocationArgument position, final ILocationArgument rotation,
					Facing facing) {
		Vector3d vector3d = position.getPosition(source);
		final Vector2f vector2f = rotation == null ? null
						: rotation.getRotation(source);

		for (Entity entity : targets) {
			if (vector2f == null) {
				TeleportCommand.teleport(source, entity, world, vector3d.x, vector3d.y, vector3d.z, entity.getLocation().getYaw(), entity.getLocation().getPitch(), facing);
			} else {
				TeleportCommand.teleport(source, entity, world, vector3d.x, vector3d.y, vector3d.z, vector2f.x, vector2f.y, facing);
			}
		}

		if (targets.size() == 1) {
			source.sendFeedback(Message.TELEPORTED_TO_LOCATION_SINGLE.getMessage(source, targets.stream().findAny().get().getName(), vector3d.x, vector3d.y, vector3d.z), true);
		} else {
			source.sendFeedback(Message.TELEPORTED_TO_LOCATION_MULTIPLE.getMessage(source, targets.size(), vector3d.x, vector3d.y, vector3d.z), true);
		}

		return targets.size();
	}

	private static void teleport(CommandSource source, Entity entity,
					World world, double x, double y, double z, float yaw,
					float pitch, Facing facing) {
		entity.teleport(new Location(world, x, y, z, yaw, pitch));
		if (facing != null)
			facing.updateLook(source, entity);
	}

	static class Facing {
		private final Vector3d position;
		private final Entity entity;
		private final EntityAnchorArgument.Type anchor;

		public Facing(Entity entityIn, EntityAnchorArgument.Type anchorIn) {
			this.entity = entityIn;
			this.anchor = anchorIn;
			this.position = anchorIn.apply(entityIn);
		}

		public Facing(Vector3d positionIn) {
			this.entity = null;
			this.position = positionIn;
			this.anchor = null;
		}

		public void updateLook(CommandSource source, Entity entityIn) {
			if (this.entity != null) {
				if (entityIn instanceof Player) {
					TeleportCommand.lookAt(entityIn, source.getEntityAnchorType(), this.entity, this.anchor);

				} else {
					TeleportCommand.lookAt(entityIn, source.getEntityAnchorType(), this.position);
				}
			} else {
				TeleportCommand.lookAt(entityIn, source.getEntityAnchorType(), this.position);
			}

		}
	}

	private static void lookAt(Entity entity, EntityAnchorArgument.Type anchor,
					Entity target, EntityAnchorArgument.Type targetAnchor) {
		TeleportCommand.lookAt(entity, anchor, targetAnchor.apply(target));
	}

	private static void lookAt(Entity entity, EntityAnchorArgument.Type anchor,
					Vector3d target) {
		Vector3d vector3d = anchor.apply(entity);
		double d0 = target.x - vector3d.x;
		double d1 = target.y - vector3d.y;
		double d2 = target.z - vector3d.z;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		float rotationPitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3)
						* (180F / (float) Math.PI))));
		float rotationYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0)
						* (180F / (float) Math.PI)) - 90.0F);
		Location loc = entity.getLocation();
		loc.setYaw(rotationYaw);
		loc.setPitch(rotationPitch);
		entity.teleport(loc);
	}
}
