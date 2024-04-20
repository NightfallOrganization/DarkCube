/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import java.util.Collection;
import java.util.Collections;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityAnchorArgument;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.bukkit.commandapi.argument.ILocationArgument;
import eu.darkcube.system.bukkit.commandapi.argument.RotationArgument;
import eu.darkcube.system.bukkit.commandapi.argument.Vec3Argument;
import eu.darkcube.system.commandapi.util.MathHelper;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TeleportCommand extends PServer {

    public static final SimpleCommandExceptionType TOO_MANY_ENTITIES = Messages.TOO_MANY_ENTITIES.newSimpleCommandExceptionType();

    public TeleportCommand() {
        super("teleport", new String[]{"tp"}, b -> b.then(Commands
                .argument("targets", EntityArgument.entities())
                .executes(source -> {
                    // When tp @e[limit=1] for example
                    return TeleportCommand.teleportToSingleEntity(source.getSource(), EntityArgument.getEntities(source, "targets"));
                })
                .then(Commands.argument("destination", EntityArgument.entity()).executes(source -> TeleportCommand.teleportToEntity(source.getSource(), EntityArgument.getEntities(source, "targets"), EntityArgument.getEntity(source, "destination"))))
                .then(Commands
                        .argument("location", Vec3Argument.vec3())
                        .executes(source -> TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, null))
                        .then(Commands.argument("rotation", RotationArgument.rotation()).executes(source -> TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), RotationArgument.getRotation(source, "rotation"), null)))
                        .then(Commands
                                .literal("facing")
                                .then(Commands.literal("entity").then(Commands.argument("facingEntity", EntityArgument.entity()).executes(source -> TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, new Facing(EntityArgument.getEntity(source, "facingEntity"), EntityAnchorArgument.Type.FEET))).then(Commands.argument("facingAnchor", EntityAnchorArgument.entityAnchor()).executes(source -> TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, new Facing(EntityArgument.getEntity(source, "facingEntity"), EntityAnchorArgument.getEntityAnchor(source, "facingAnchor")))))))
                                .then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes(source -> TeleportCommand.teleportToPos(source.getSource(), EntityArgument.getEntities(source, "targets"), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, new Facing(Vec3Argument.getVec3(source, "facingLocation")))))))).then(Commands.argument("location", Vec3Argument.vec3()).executes(source -> TeleportCommand.teleportToPos(source.getSource(), Collections.singleton(source.getSource().assertIsEntity()), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), null, null)).then(Commands.argument("rotation", RotationArgument.rotation()).executes(source -> TeleportCommand.teleportToPos(source.getSource(), Collections.singleton(source.getSource().assertIsEntity()), source.getSource().getWorld(), Vec3Argument.getLocation(source, "location"), RotationArgument.getRotation(source, "rotation"), null)))));
    }

    private static int teleportToSingleEntity(CommandSource source, Collection<? extends Entity> destinations) throws CommandSyntaxException {
        if (destinations.size() > 1) {
            throw TeleportCommand.TOO_MANY_ENTITIES.create();
        }
        var destination = destinations.stream().findAny().get();
        source.assertIsEntity().teleport(destination);
        source.sendMessage(Message.TELEPORTED_TO_ENTITY_SINGLE, source.assertIsEntity().getName(), destination.getName());
        return 0;
    }

    private static int teleportToEntity(CommandSource source, Collection<? extends Entity> targets, Entity destination) {
        var loc = destination.getLocation();
        for (var entity : targets) {
            TeleportCommand.teleport(source, entity, destination.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), null);
        }
        if (targets.size() == 1) {
            source.sendMessage(Message.TELEPORTED_TO_ENTITY_SINGLE, targets.stream().findAny().get().getName(), destination.getName());
        } else {
            source.sendMessage(Message.TELEPORTED_TO_ENTITY_MULTIPLE, targets.size(), destination.getName());
        }
        return targets.size();
    }

    public static int teleportToPos(CommandSource source, Collection<? extends Entity> targets, World world, ILocationArgument position, final ILocationArgument rotation, Facing facing) {
        var vector3d = position.getPosition(source);
        final var vector2f = rotation == null ? null : rotation.getRotation(source);

        for (var entity : targets) {
            if (vector2f == null) {
                TeleportCommand.teleport(source, entity, world, vector3d.x, vector3d.y, vector3d.z, entity.getLocation().getYaw(), entity.getLocation().getPitch(), facing);
            } else {
                TeleportCommand.teleport(source, entity, world, vector3d.x, vector3d.y, vector3d.z, vector2f.x, vector2f.y, facing);
            }
        }

        if (targets.size() == 1) {
            source.sendMessage(Message.TELEPORTED_TO_LOCATION_SINGLE, targets.stream().findAny().get().getName(), vector3d.x, vector3d.y, vector3d.z);
        } else {
            source.sendMessage(Message.TELEPORTED_TO_LOCATION_MULTIPLE, targets.size(), vector3d.x, vector3d.y, vector3d.z);
        }

        return targets.size();
    }

    private static void teleport(CommandSource source, Entity entity, World world, double x, double y, double z, float yaw, float pitch, Facing facing) {
        entity.teleport(new Location(world, x, y, z, yaw, pitch));
        if (facing != null) facing.updateLook(source, entity);
    }

    private static void lookAt(Entity entity, EntityAnchorArgument.Type anchor, Entity target, EntityAnchorArgument.Type targetAnchor) {
        TeleportCommand.lookAt(entity, anchor, targetAnchor.apply(target));
    }

    private static void lookAt(Entity entity, EntityAnchorArgument.Type anchor, Vector3d target) {
        var vector3d = anchor.apply(entity);
        var d0 = target.x - vector3d.x;
        var d1 = target.y - vector3d.y;
        var d2 = target.z - vector3d.z;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        var rotationPitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3) * (180F / (float) Math.PI))));
        var rotationYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0) * (180F / (float) Math.PI)) - 90.0F);
        var loc = entity.getLocation();
        loc.setYaw(rotationYaw);
        loc.setPitch(rotationPitch);
        entity.teleport(loc);
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
}
