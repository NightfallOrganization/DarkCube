/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import eu.darkcube.minigame.woolbattle.api.entity.EntityImplementations;
import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.MinestomProjectileImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.entity.EntityType;

public class MinestomEntityImplementations implements EntityImplementations {
    private final MinestomWoolBattle woolbattle;

    public MinestomEntityImplementations(MinestomWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public @NotNull Projectile launchSnowball(@NotNull WBUser fromUser) {
        var shooter = (CommonWBUser) fromUser;
        var player = woolbattle.player(shooter);
        var team = shooter.team();
        if (team == null) throw new IllegalStateException("Player can only launch snowball while in a team");
        var game = (CommonGame) team.game();
        var snowball = new MinestomProjectileImpl(woolbattle, game, shooter, player, team, EntityType.SNOWBALL);
        player.acquirable().sync(p -> {
            var pos = p.getPosition().add(0, p.getEyeHeight(), 0);
            snowball.setInstance(p.getInstance(), pos);
            p.setNoGravity(true);
        });
        var projectile = new MinestomProjectile(snowball.acquirable(), woolbattle);
        snowball.handle(projectile);
        return projectile;
    }

    @Override
    public @NotNull Projectile launchEgg(@NotNull WBUser fromUser) {
        return null;
    }

    @Override
    public @NotNull Projectile spawnArrow(@NotNull Location location, @NotNull Vector direction, float speed, float spread) {
        return null;
    }
}
