/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import java.util.function.Consumer;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.entity.SimpleEntityType;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.entity.CommonEntityImplementations;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.EntityTypeMappings;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.MinestomProjectileImpl;
import eu.darkcube.minigame.woolbattle.minestom.util.MinestomUtil;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.entity.EntityType;

public class MinestomEntityImplementations extends CommonEntityImplementations {
    private final MinestomWoolBattle woolbattle;
    private final EntityTypeMappings mappings;

    public MinestomEntityImplementations(MinestomWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
        this.mappings = new EntityTypeMappings();
    }

    @Override
    protected <T extends Projectile> @NotNull T spawnProjectile0(@NotNull eu.darkcube.minigame.woolbattle.api.entity.EntityType<T> type, @Nullable CommonWBUser shooter, @NotNull Location location, @NotNull Vector velocity, float speed, float spread, @Nullable Consumer<T> preSpawnCallback) {
        var mapping = findMapping(type);
        var entity = new MinestomProjectileImpl(woolbattle, mapping);
        var projectile = new MinestomProjectile(entity.acquirable(), woolbattle, shooter);
        var wrapped = createWrapped(type, projectile);
        if (preSpawnCallback != null) {
            preSpawnCallback.accept(wrapped);
        }
        entity.handle(wrapped);
        var instance = ((MinestomWorld) location.world()).instance();
        entity.acquirable().sync(e -> {
            e.setInstance(instance, MinestomUtil.toPos(location));
            e.setVelocity(MinestomUtil.toVec(velocity));
        });
        return wrapped;
    }

    //
    // @Override
    // public @NotNull Projectile launchSnowball(@NotNull WBUser fromUser) {
    //     var shooter = (CommonWBUser) fromUser;
    //     var player = woolbattle.player(shooter);
    //     var team = shooter.team();
    //     if (team == null) throw new IllegalStateException("Player can only launch snowball while in a team");
    //     var game = (CommonGame) team.game();
    //     var snowball = new MinestomProjectileImpl(woolbattle, game, shooter, player, team, EntityType.SNOWBALL);
    //     player.acquirable().sync(p -> {
    //         var pos = p.getPosition().add(0, p.getEyeHeight(), 0);
    //         snowball.setInstance(p.getInstance(), pos);
    //         p.setNoGravity(true);
    //     });
    //     var projectile = new MinestomProjectile(snowball.acquirable(), woolbattle);
    //     snowball.handle(projectile);
    //     return projectile;
    // }

    @NotNull
    private EntityType findMapping(eu.darkcube.minigame.woolbattle.api.entity.EntityType<?> type) {
        var original = type;
        do {
            if (mappings.has(type)) {
                return mappings.get(type);
            }
            var simpleType = (SimpleEntityType<?>) type;
            type = simpleType.wrapping();
        } while (type != null);
        throw new IllegalArgumentException("Can't find mapping for " + original.key().asString());
    }
}
