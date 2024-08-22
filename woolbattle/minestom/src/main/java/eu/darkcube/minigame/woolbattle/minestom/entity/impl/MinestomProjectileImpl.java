/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity.impl;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitBlockEvent;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitEntityEvent;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.Aerodynamics;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.thread.Acquirable;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("UnstableApiUsage")
public class MinestomProjectileImpl extends AbstractProjectile implements EntityImpl {

    private final MinestomWoolBattle woolbattle;
    private Projectile handle;
    private boolean inBlock = false;
    private boolean firstTick = true;

    public MinestomProjectileImpl(@NotNull MinestomWoolBattle woolbattle, @NotNull EntityType entityType) {
        super(entityType, null);
        this.woolbattle = woolbattle;
        super.hasPhysics = false;
    }

    public void handle(Projectile handle) {
        this.handle = handle;
    }

    @Override
    public @NotNull Projectile handle() {
        return handle;
    }

    private boolean filter(Entity entity) {
        if (!(entity instanceof MinestomPlayer player)) return false;
        var user = player.user();
        if (user == null) return false;
        var team = user.team();
        if (team == null) return false;
        if (!team.canPlay()) return false;
        var shooter = this.handle().shooter();
        if (shooter == null) return true;
        var shooterTeam = shooter.team();
        return team != shooterTeam;
    }

    @Override
    public void tick(long time) {
        if (removed || inBlock) return;

        var posBefore = getPosition();
        updatePosition(time);
        var posNow = getPosition();

        var diff = Vec.fromPoint(posNow.sub(posBefore));
        var yaw = Vector.getYaw(diff.x(), diff.z());
        var pitch = Vector.getPitch(diff.x(), diff.y(), diff.z());
        this.position = posNow.withView(yaw, pitch);

        if (firstTick) {
            firstTick = false;
            setView(yaw, pitch);
        }
    }

    @Override
    protected void handleBlockCollision(Block hitBlock, Point hitPos, Pos posBefore) {
        velocity = Vec.ZERO;
        setNoGravity(true);

        inBlock = true;
        velocity = Vec.fromPoint(hitPos.sub(posBefore));
        var v = velocity.normalize().mul(0.01F); // Required so the entity is lit (just outside the block)
        velocity = Vec.ZERO;

        // if the value is zero, it will be unlit. If the value is more than 0.01, there will be noticeable pitch change visually
        position = new Pos(hitPos.x() - v.x(), hitPos.y() - v.y(), hitPos.z() - v.z(), posBefore.yaw(), posBefore.pitch());
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            synchronizePosition();
            sendPacketToViewersAndSelf(getVelocityPacket());
        });

        collideWithBlock(hitPos);
        System.out.println(hitPos.blockX() + " " + hitPos.blockY() + " " + hitPos.blockZ());
    }

    @Override
    protected boolean handleEntityCollision(Entity hitEntity, Point hitPos, Pos posBefore) {
        if (filter(hitEntity)) {
            return collideWithEntity(hitPos, hitEntity);
        }
        return false;
    }

    @Override
    protected @NotNull Vec updateVelocity(@NotNull Pos entityPosition, @NotNull Vec currentVelocity, @NotNull Block.Getter blockGetter, @NotNull Aerodynamics aerodynamics, boolean positionChanged, boolean entityFlying, boolean entityOnGround, boolean entityNoGravity) {
        if (!positionChanged) {
            return entityFlying ? Vec.ZERO : new Vec(0.0, entityNoGravity ? 0.0 : -aerodynamics.gravity() * aerodynamics.verticalAirResistance(), 0.0);
        } else {
            var drag = entityOnGround ? blockGetter.getBlock(entityPosition.sub(0.0, 0.5000001, 0.0)).registry().friction() * aerodynamics.horizontalAirResistance() : aerodynamics.horizontalAirResistance();
            var gravity = entityFlying ? 0.0 : aerodynamics.gravity();
            var gravityDrag = entityFlying ? 0.6 : aerodynamics.verticalAirResistance();
            var x = currentVelocity.x() * drag;
            var y = entityNoGravity ? currentVelocity.y() : (currentVelocity.y() - gravity) * gravityDrag;
            var z = currentVelocity.z() * drag;
            return new Vec(Math.abs(x) < 1.0E-6 ? 0.0 : x, Math.abs(y) < 1.0E-6 ? 0.0 : y, Math.abs(z) < 1.0E-6 ? 0.0 : z);
        }
    }

    private boolean collideWithEntity(Point pos, Entity target) {
        if (!(target instanceof EntityImpl impl)) throw new IllegalStateException("Invalid Entity: " + target);
        var event = new ProjectileHitEntityEvent(this.handle, impl.handle(), new Position.Simple(pos.x(), pos.y(), pos.z()));
        woolbattle.api().eventManager().call(event);
        return !event.cancelled();
    }

    private void collideWithBlock(Point pos) {
        var event = new ProjectileHitBlockEvent(this.handle, this.handle.location().world().blockAt(pos.blockX(), pos.blockY(), pos.blockZ()));
        woolbattle.api().eventManager().call(event);
    }

    @ApiStatus.Experimental
    @Override
    public @NotNull Acquirable<? extends MinestomProjectileImpl> acquirable() {
        return (Acquirable<? extends MinestomProjectileImpl>) super.acquirable();
    }
}
