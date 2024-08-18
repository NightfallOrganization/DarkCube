/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.event.entity.EntityDamageByEntityEvent;
import eu.darkcube.minigame.woolbattle.api.event.entity.EntityDamageEvent;
import eu.darkcube.minigame.woolbattle.api.event.entity.ProjectileHitBlockEvent;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.MinestomEntity;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.projectile.ProjectileUncollideEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.thread.Acquirable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class MinestomProjectileImpl extends Entity implements EntityImpl {

    private final MinestomWoolBattle woolbattle;
    private final CommonWBUser shooter;
    private final MinestomPlayer player;
    // We store the team separately because the user's team might change while the projectile is flying
    private final Team team;
    private Projectile handle;
    private boolean wasStuck;

    public MinestomProjectileImpl(@NotNull MinestomWoolBattle woolbattle, @NotNull CommonGame game, @NotNull CommonWBUser shooter, @NotNull MinestomPlayer player, @NotNull Team team, @NotNull EntityType entityType) {
        super(entityType);
        this.woolbattle = woolbattle;
        this.shooter = shooter;
        this.player = player;
        this.team = team;
        setup();
    }

    public void handle(Projectile handle) {
        this.handle = handle;
    }

    @Override
    public Projectile handle() {
        return handle;
    }

    private void setup() {
        super.hasPhysics = false;
    }

    public @NotNull CommonWBUser shooter() {
        return shooter;
    }

    public Player player() {
        return player;
    }

    @Override
    public void tick(long time) {
        final var posBefore = getPosition();
        super.tick(time);
        if (super.isRemoved()) return;

        final var posNow = getPosition();
        if (isStuck(posBefore, posNow)) {
            if (super.onGround) {
                return;
            }
            super.onGround = true;
            this.velocity = Vec.ZERO;
            sendPacketToViewersAndSelf(getVelocityPacket());
            setNoGravity(true);
            wasStuck = true;
        } else {
            if (!wasStuck) return;
            wasStuck = false;
            setNoGravity(super.onGround);
            super.onGround = false;
            EventDispatcher.call(new ProjectileUncollideEvent(this));
        }
    }

    private boolean collideWithEntity(Pos pos, LivingEntity target) {
        var event = new EntityDamageByEntityEvent(new MinestomEntity(target.acquirable(), woolbattle), this.handle, EntityDamageEvent.DamageCause.PROJECTILE);
        woolbattle.api().eventManager().call(event);
        return event.cancelled();
    }

    private boolean collideWithBlock(Pos pos, Block block) {
        var event = new ProjectileHitBlockEvent(this.handle, this.handle.location().world().blockAt(pos.blockX(), pos.blockY(), pos.blockZ()));
        woolbattle.api().eventManager().call(event);
        // if(!event.isCancelled()) {
        teleport(pos);
        return true;
        // }
        // return false;
    }

    private boolean filter(LivingEntity entity) {
        if (!(entity instanceof MinestomPlayer player)) return false;
        var user = player.user();
        if (user == null) return false;
        var team = user.team();
        if (team == null) return false;
        if (!team.canPlay()) return false;
        return team != this.team;
    }

    /**
     * Checks whether an arrow is stuck in block / hit an entity.
     *
     * @param pos    position right before current tick.
     * @param posNow position after current tick.
     * @return if an arrow is stuck in block / hit an entity.
     */
    @SuppressWarnings("ConstantConditions")
    private boolean isStuck(Pos pos, Pos posNow) {
        final var instance = getInstance();
        if (pos.samePoint(posNow)) {
            return instance.getBlock(pos).isSolid();
        }

        Chunk chunk = null;
        Collection<LivingEntity> entities = null;
        final var bb = getBoundingBox();

        /*
          What we're about to do is to discretely jump from a previous position to the new one.
          For each point we will be checking blocks and entities we're in.
         */
        final var part = bb.width() / 2;
        final var dir = posNow.sub(pos).asVec();
        final var parts = (int) Math.ceil(dir.length() / part);
        final var direction = dir.normalize().mul(part).asPosition();
        final var aliveTicks = getAliveTicks();
        Block block = null;
        Point blockPos = null;
        for (var i = 0; i < parts; ++i) {
            // If we're at last part, we can't just add another direction-vector, because we can exceed the end point.
            pos = (i == parts - 1) ? posNow : pos.add(direction);
            if (block == null || !pos.sameBlock(blockPos)) {
                block = instance.getBlock(pos);
                blockPos = pos;
            }
            if (block.isSolid()) {
                var collision = collideWithBlock(pos, block);
                if (collision) {
                    return true;
                }
            }
            if (currentChunk != chunk) {
                chunk = currentChunk;
                entities = instance.getChunkEntities(chunk).stream().filter(entity -> entity instanceof LivingEntity).map(entity -> (LivingEntity) entity).collect(Collectors.toSet());
            }
            final Point currentPos = pos;
            var victimsStream = entities.stream().filter(entity -> bb.intersectEntity(currentPos, entity)).filter(this::filter);
            final var victimOptional = victimsStream.findAny();
            if (victimOptional.isPresent()) {
                final var target = victimOptional.get();
                var cancelled = collideWithEntity(pos, target);
                if (!cancelled) {
                    return super.onGround;
                }
            }
        }
        return false;
    }

    @ApiStatus.Experimental
    @Override
    public @NotNull Acquirable<? extends MinestomProjectileImpl> acquirable() {
        return (Acquirable<? extends MinestomProjectileImpl>) super.acquirable();
    }
}
