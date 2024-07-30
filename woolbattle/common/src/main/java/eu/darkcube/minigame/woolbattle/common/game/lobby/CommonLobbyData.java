/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby;

import eu.darkcube.minigame.woolbattle.api.game.lobby.LobbyData;
import eu.darkcube.minigame.woolbattle.api.world.Position.Directed;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class CommonLobbyData implements LobbyData {
    private final @NotNull CommonWoolBattleApi woolbattle;
    private @NotNull Directed spawn;
    private int deathLine;
    private int minPlayerCount;

    public CommonLobbyData(CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
        spawn = woolbattle.persistentDataStorage().get(lobbySpawn(woolbattle), Directed.TYPE, () -> new Directed.Simple(0.5, 100, 0.5, 0, 0));
        deathLine = woolbattle.persistentDataStorage().get(deathLine(woolbattle), PersistentDataTypes.INTEGER, () -> 50);
        minPlayerCount = woolbattle.persistentDataStorage().get(minPlayerCount(woolbattle), PersistentDataTypes.INTEGER, () -> 2);
    }

    public CommonLobbyData(@NotNull CommonWoolBattleApi woolbattle, @NotNull Directed spawn) {
        this.woolbattle = woolbattle;
        this.spawn = spawn;
    }

    @Override
    public @NotNull Directed spawn() {
        return spawn;
    }

    @Override
    public void spawn(@NotNull Directed spawn) {
        ifOriginal(() -> this.woolbattle.persistentDataStorage().set(lobbySpawn(woolbattle), Directed.TYPE, spawn));
        this.spawn = spawn;
    }

    @Override
    public void minPlayerCount(int minPlayerCount) {
        ifOriginal(() -> this.woolbattle.persistentDataStorage().set(minPlayerCount(woolbattle), PersistentDataTypes.INTEGER, minPlayerCount));
        this.minPlayerCount = minPlayerCount;
    }

    @Override
    public int minPlayerCount() {
        return minPlayerCount;
    }

    @Override
    public void deathLine(int deathLine) {
        ifOriginal(() -> this.woolbattle.persistentDataStorage().set(deathLine(woolbattle), PersistentDataTypes.INTEGER, deathLine));
        this.deathLine = deathLine;
    }

    @Override
    public int deathLine() {
        return deathLine;
    }

    @Override
    public @NotNull CommonLobbyData clone() {
        return new CommonLobbyData(woolbattle, spawn);
    }

    /**
     * Clones will not change the persistent data. Operations on the original data will change the persistent data.
     */
    private void ifOriginal(Runnable runnable) {
        if (this.woolbattle.lobbyData() == this) {
            runnable.run();
        }
    }

    private Key lobbySpawn(CommonWoolBattleApi woolbattle) {
        return Key.key(woolbattle, "lobby_spawn");
    }

    private Key minPlayerCount(CommonWoolBattleApi woolbattle) {
        return Key.key(woolbattle, "min_player_count");
    }

    private Key deathLine(CommonWoolBattleApi woolbattle) {
        return Key.key(woolbattle, "lobby_death_line");
    }
}
