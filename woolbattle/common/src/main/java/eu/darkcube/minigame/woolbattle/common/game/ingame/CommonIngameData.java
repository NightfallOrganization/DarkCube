/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.minigame.woolbattle.api.game.ingame.IngameData;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonIngameData implements IngameData {
    private final @NotNull Map<WBUser, Integer> killStreaks = new HashMap<>();
    private final @NotNull Map<WBUser, Integer> kills = new HashMap<>();
    private final @NotNull Map<WBUser, Integer> deaths = new HashMap<>();
    private int maxBlockDamage = 3;
    private int killsForOneLife = 3;

    @Override
    public int maxBlockDamage() {
        return maxBlockDamage;
    }

    @Override
    public void maxBlockDamage(int maxBlockDamage) {
        this.maxBlockDamage = maxBlockDamage;
    }

    public int killsForOneLife() {
        return killsForOneLife;
    }

    public void killsForOneLife(int killsForOneLife) {
        this.killsForOneLife = killsForOneLife;
    }

    public @NotNull Map<WBUser, Integer> killStreaks() {
        return killStreaks;
    }

    public @NotNull Map<WBUser, Integer> kills() {
        return kills;
    }

    public @NotNull Map<WBUser, Integer> deaths() {
        return deaths;
    }

    public int killStreak(@NotNull WBUser user) {
        return killStreaks.getOrDefault(user, 0);
    }

    public int deaths(@NotNull WBUser user) {
        return deaths.getOrDefault(user, 0);
    }

    public int kills(@NotNull WBUser user) {
        return kills.getOrDefault(user, 0);
    }

    public void addKill(@NotNull WBUser user) {
        kills.put(user, kills.getOrDefault(user, 0) + 1);
    }

    public void addDeath(@NotNull WBUser user) {
        deaths.put(user, deaths.getOrDefault(user, 0) + 1);
    }

    public void incrementKillStreak(@NotNull WBUser user) {
        killStreaks.put(user, killStreaks.getOrDefault(user, 0) + 1);
    }

    public void resetKillStreak(@NotNull WBUser user) {
        killStreaks.remove(user);
    }
}
