/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.minigame.woolbattle.api.game.GameManager;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

public class CommonGameManager implements GameManager {
    private final @NotNull CommonWoolBattleApi woolbattle;
    private final @NotNull java.util.Map<@NotNull UUID, @NotNull CommonGame> games = new ConcurrentHashMap<>();

    public CommonGameManager(@NotNull CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public @UnmodifiableView @NotNull Collection<CommonGame> games() {
        return Collections.unmodifiableCollection(games.values());
    }

    @Override
    public @NotNull CommonGame createGame(@NotNull Map map) {
        while (true) {
            var id = UUID.randomUUID();
            var game = new CommonGame(woolbattle, id, map);
            var old = games.putIfAbsent(id, game);
            if (old != null) continue;
            game.init();
            woolbattle.eventManager().addChild(game.eventManager());
            woolbattle.woolbattle().logger().info("Created game {}", game.id());
            woolbattle.lobbySystemLink().update();
            return game;
        }
    }

    @Override
    public @Nullable CommonGame game(@NotNull UUID id) {
        return games.get(id);
    }

    public void unload(@NotNull CommonGame game) {
        var removed = games.remove(game.id());
        if (removed != game) {
            if (removed == null) {
                woolbattle.woolbattle().logger().error("Game was trying to unload but not registered: {}", game);
            } else {
                woolbattle.woolbattle().logger().error("Multiple games with same id: {} and {}", game, removed);
            }
            return;
        }
        woolbattle.eventManager().removeChild(game.eventManager());
        game.unload0();
        woolbattle.woolbattle().logger().info("Unloaded game {}", game.id());
        woolbattle.lobbySystemLink().update();
    }
}
