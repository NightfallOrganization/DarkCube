/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardHelper;

public class GameData {
    public static final boolean EP_GLITCH_DEFAULT = false;
    private final WoolBattle woolBattle;
    private Map forceMap;
    private Map votedMap;
    private boolean epGlitch = EP_GLITCH_DEFAULT;
    private int forceLifes = -1;

    public GameData(WoolBattle woolBattle) {
        this.woolBattle = woolBattle;
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

    public void forceMap(Map forceMap) {
        this.forceMap = forceMap;
        reloadScoreboardMap();
    }

    public Map votedMap() {
        return votedMap;
    }

    public void votedMap(Map votedMap) {
        this.votedMap = votedMap;
        reloadScoreboardMap();
    }

    public void reloadScoreboardEpGlitch() {
        WBUser.onlineUsers().forEach(u -> ScoreboardHelper.setEpGlitch(woolBattle, u));
    }

    public void reloadScoreboardMap() {
        WBUser.onlineUsers().forEach(u -> ScoreboardHelper.setMap(woolBattle, u));
    }

    public Map map() {
        return forceMap != null ? forceMap : votedMap;
    }
}
