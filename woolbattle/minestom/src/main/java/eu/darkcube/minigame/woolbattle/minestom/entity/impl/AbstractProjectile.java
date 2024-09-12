/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity.impl;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.ServerFlag;
import net.minestom.server.collision.Aerodynamics;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.EntityCollisionResult;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.collision.ShapeImpl;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.chunk.ChunkCache;
import net.minestom.server.utils.chunk.ChunkUtils;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractProjectile extends MinestomEntityImpl {
    protected final Entity shooter;
    protected PhysicsResult previousPhysicsResult;
    protected boolean isInEntity;
    protected boolean isInBlock;

    public AbstractProjectile(EntityType type, Entity shooter, MinestomWoolBattle woolbattle) {
        super(type, woolbattle);
        // ((SnowballMeta) this.entityMeta).setItem(ItemStack.of(Material.NETHERITE_SWORD));
        this.shooter = shooter;
    }

    protected PhysicsResult computePhysics(@NotNull Pos entityPosition, @NotNull Vec currentVelocity, @NotNull Block.Getter blockGetter, @NotNull Aerodynamics aerodynamics) {
        var newVelocity = updateVelocity(entityPosition, currentVelocity, blockGetter, aerodynamics, true, false, onGround, false);

        var newPhysicsResult = CollisionUtils.handlePhysics(blockGetter, this.boundingBox, entityPosition, newVelocity, previousPhysicsResult, true);

        previousPhysicsResult = newPhysicsResult;
        return newPhysicsResult;
    }

    @Override
    protected void movementTick() {
        this.gravityTickCount = onGround ? 0 : gravityTickCount + 1;
        if (vehicle != null) return;
        if (removed) return;

        final Block.Getter chunkCache = new ChunkCache(instance, currentChunk, Block.STONE);
        var blockResult = computePhysics(position, velocity.div(ServerFlag.SERVER_TICKS_PER_SECOND), chunkCache, getAerodynamics());
        var collidedWithEntity = checkEntityCollision(this.position, blockResult.newPosition());
        if (removed) return;
        if (collidedWithEntity != null) {
            velocity = Vec.ZERO;
            if (isInEntity) {
                sendPacketToViewers(getVelocityPacket());
                synchronizePosition();
                return;
            }
            isInEntity = true;
            sendPacketToViewers(getVelocityPacket());
            refreshPosition(Pos.fromPoint(collidedWithEntity.collisionPoint()), true, true);
            return;
        } else if (isInEntity) {
            isInEntity = false;
        }

        var finalChunk = ChunkUtils.retrieve(instance, currentChunk, blockResult.newPosition());
        if (!ChunkUtils.isLoaded(finalChunk)) return;

        if (blockResult.hasCollision()) {
            Block hitBlock = null;
            Point hitBlockPos = null;
            Point hitPoint = null;
            if (blockResult.collisionShapes()[0] instanceof ShapeImpl block) { // x
                hitBlock = block.block();
                hitBlockPos = blockResult.collisionShapePositions()[0];
                hitPoint = blockResult.collisionPoints()[0];
            }
            if (blockResult.collisionShapes()[1] instanceof ShapeImpl block) { // y
                hitBlock = block.block();
                hitBlockPos = blockResult.collisionShapePositions()[1];
                hitPoint = blockResult.collisionPoints()[1];
            }
            if (blockResult.collisionShapes()[2] instanceof ShapeImpl block) { // z
                hitBlock = block.block();
                hitBlockPos = blockResult.collisionShapePositions()[2];
                hitPoint = blockResult.collisionPoints()[2];
            }

            if (hitBlock == null) return;
            handleBlockCollision(hitBlock, hitBlockPos, hitPoint, position, blockResult);
            if (removed) return;
        } else {
            velocity = blockResult.newVelocity().mul(ServerFlag.SERVER_TICKS_PER_SECOND).mul(0.99);
        }

        onGround = blockResult.isOnGround();

        refreshPosition(blockResult.newPosition(), true, false);
        if (hasVelocity()) sendPacketToViewers(getVelocityPacket());
    }

    protected @Nullable EntityCollisionResult checkEntityCollision(@NotNull Pos previousPos, @NotNull Pos currentPos) {
        var diff = currentPos.sub(previousPos).asVec();

        var results = CollisionUtils.checkEntityCollisions(this, diff, diff.length() + 1.8, entity -> entity != shooter && entity != this, previousPhysicsResult);

        if (results.isEmpty()) {
            return null;
        }

        var sorted = results.stream().sorted().toList();

        for (var result : sorted) {
            if (handleEntityCollision(result.entity(), result.collisionPoint(), previousPos)) {
                return result;
            }
            if (removed) return null;
        }
        return null;
    }

    protected void updatePosition(long time) {
        if (instance == null || isRemoved() || !ChunkUtils.isLoaded(currentChunk)) return;

        movementTick();
        update(time);
        EventDispatcher.call(new EntityTickEvent(this));
    }

    protected abstract void handleBlockCollision(Block hitBlock, Point hitBlockPos, Point hitPos, Pos posBefore, PhysicsResult blockResult);

    protected abstract boolean handleEntityCollision(Entity hitEntity, Point hitPos, Pos posBefore);

    protected abstract @NotNull Vec updateVelocity(@NotNull Pos entityPosition, @NotNull Vec currentVelocity, @NotNull Block.@NotNull Getter blockGetter, @NotNull Aerodynamics aerodynamics, boolean positionChanged, boolean entityFlying, boolean entityOnGround, boolean entityNoGravity);
}
