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
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

public class CommonGameManager implements GameManager {
    private final CommonWoolBattleApi woolbattle;
    private final Map<UUID, CommonGame> games = new ConcurrentHashMap<>();

    public CommonGameManager(CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public @UnmodifiableView @NotNull Collection<CommonGame> games() {
        return Collections.unmodifiableCollection(games.values());
    }

    @Override
    public @NotNull CommonGame createGame() {
        UUID id;
        for (id = UUID.randomUUID(); games.containsKey(id); id = UUID.randomUUID()) ;
        var game = new CommonGame(woolbattle, id);
        games.put(id, game);
        return game;
    }

    @Override
    public @Nullable CommonGame game(@NotNull UUID id) {
        return games.get(id);
    }
}
