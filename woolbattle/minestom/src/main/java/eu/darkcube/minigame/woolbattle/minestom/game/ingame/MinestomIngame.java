/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.ingame;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.game.ingame.listeners.MinestomIngameJoinGameListener;
import eu.darkcube.minigame.woolbattle.minestom.game.ingame.listeners.MinestomIngameQuitGameListener;
import eu.darkcube.minigame.woolbattle.minestom.game.ingame.listeners.MinestomIngameUserChangeTeamListener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.attribute.Attribute;

public class MinestomIngame extends CommonIngame {
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull MinestomIngameScoreboard scoreboard;

    public MinestomIngame(@NotNull CommonGame game, @NotNull MinestomWoolBattle woolbattle) {
        super(game);
        this.woolbattle = woolbattle;
        this.scoreboard = new MinestomIngameScoreboard(woolbattle, game);

        this.listeners.addListener(new MinestomIngameJoinGameListener(woolbattle).create());
        this.listeners.addListener(new MinestomIngameQuitGameListener(woolbattle).create());
        this.listeners.addListener(new MinestomIngameUserChangeTeamListener(woolbattle, this).create());
    }

    @Override
    public void init(@Nullable CommonPhase oldPhase) {
        for (var user : game.users()) {
            var player = woolbattle.player(user);
            var lock = player.acquirable().lock();
            player.setGameMode(GameMode.SPECTATOR);
            player.setFlyingSpeed(0);
            lock.unlock();
        }
        super.init(oldPhase);
    }

    @Override
    public void enable(@Nullable CommonPhase oldPhase) {
        super.enable(oldPhase);
    }

    @Override
    public void join(@NotNull CommonWBUser user) {
        super.join(user);
        var player = woolbattle.player(user);
        var lock = player.acquirable().lock();
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlying(true);
        player.setFlyingSpeed(0.05F);
        var attackSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        attackSpeed.setBaseValue(1024.0);
        lock.unlock();
        scoreboard.setupPlayer(player, user);
    }

    @Override
    public void quit(@NotNull CommonWBUser user) {
        var player = woolbattle.player(user);
        var oldTeam = user.team();
        if (oldTeam != null) {
            scoreboard.cleanupPlayer(player, oldTeam);
        }
        super.quit(user);
    }

    public @NotNull MinestomIngameScoreboard scoreboard() {
        return scoreboard;
    }
}
