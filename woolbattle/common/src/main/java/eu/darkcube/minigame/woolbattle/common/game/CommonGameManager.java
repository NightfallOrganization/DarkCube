/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.minigame.woolbattle.api.game.GameManager;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

public class CommonGameManager implements GameManager {
    private final @NotNull CommonWoolBattleApi woolbattle;
    private final @NotNull Map<@NotNull UUID, @NotNull CommonGame> games = new ConcurrentHashMap<>();

    public CommonGameManager(@NotNull CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public @UnmodifiableView @NotNull Collection<CommonGame> games() {
        return Collections.unmodifiableCollection(games.values());
    }

    @Override
    public @NotNull CommonGame createGame(MapSize mapSize) {
        UUID id;
        for (id = UUID.randomUUID(); games.containsKey(id); id = UUID.randomUUID()) ;
        var game = new CommonGame(woolbattle, id, mapSize);
        games.put(id, game);
        woolbattle.eventManager().addChild(game.eventManager());
        return game;
    }

    @Override
    public @Nullable CommonGame game(@NotNull UUID id) {
        return games.get(id);
    }

    public void unload(@NotNull CommonGame game) {
        var removed = games.remove(game.id());
        if (removed != game) {
            if (removed == null) {
                woolbattle.woolbattle().logger().severe("Game was trying to unload but not registered: " + game);
            } else {
                woolbattle.woolbattle().logger().severe("Multiple games with same id: " + game + " and " + removed);
            }
            return;
        }
        woolbattle.eventManager().removeChild(game.eventManager());
        game.unload0();
    }
}
