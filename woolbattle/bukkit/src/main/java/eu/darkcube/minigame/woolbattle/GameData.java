/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardHelper;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.function.Consumer;

public class GameData {
    public static final boolean EP_GLITCH_DEFAULT = false;
    private final WoolBattleBukkit woolbattle;
    private MapSize mapSize;
    private Map forceMap;
    private Map votedMap;
    private boolean epGlitch = EP_GLITCH_DEFAULT;
    private int forceLifes = -1;

    public GameData(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    public boolean epGlitch() {
        return epGlitch;
    }

    public void epGlitch(boolean epGlitch) {
        this.epGlitch = epGlitch;
        reloadScoreboardEpGlitch();
    }

    public int forceLifes() {
        return forceLifes;
    }

    public void forceLifes(int forceLifes) {
        this.forceLifes = forceLifes;
    }

    public Map forceMap() {
        return forceMap;
    }

    public void forceMap(@Nullable Map forceMap) {
        updateMap(forceMap, m -> this.forceMap = m, true);
    }

    public MapSize mapSize() {
        return mapSize;
    }

    public void mapSize(MapSize mapSize) {
        this.mapSize = mapSize;
    }

    public Map votedMap() {
        return votedMap;
    }

    public void votedMap(@Nullable Map votedMap) {
        updateMap(votedMap, m -> this.votedMap = m, false);
    }

    public void reloadScoreboardEpGlitch() {
        WBUser.onlineUsers().forEach(u -> ScoreboardHelper.setEpGlitch(woolbattle, u));
    }

    private void updateMap(@Nullable Map newMap, Consumer<@Nullable Map> setter, boolean force) {
        if (woolbattle.lobby().enabled()) {
            setter.accept(newMap);
            WBUser.onlineUsers().forEach(u -> ScoreboardHelper.setMap(woolbattle, u));
        } else if (woolbattle.ingame().enabled()) {
            Map oldMap = map();
            if (oldMap == null) throw new Error("Old Map is null");
            if (newMap == oldMap) return;
            Map map = map(newMap, force);
            if (map == null) {
                System.out.println("Ignoring trying to set the map to null while Ingame");
            } else {
                woolbattle.mapLoader().loadMap(map).thenRun(() -> {
                    setter.accept(map);
                    if (!woolbattle.ingame().enabled()) {
                        woolbattle.mapLoader().unloadMap(map);
                        return;
                    }
                    for (WBUser user : WBUser.onlineUsers()) {
                        user.getBukkitEntity().teleport(user.getTeam().getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                    woolbattle.mapLoader().unloadMap(oldMap);
                });
            }
        } else {
            setter.accept(newMap);
        }
    }

    private Map map(@Nullable Map newMap, boolean force) {
        if (force) return newMap;
        if (forceMap != null) return forceMap;
        return newMap;
    }

    public @Nullable Map map() {
        return forceMap != null ? forceMap : votedMap;
    }
}
