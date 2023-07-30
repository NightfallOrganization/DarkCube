/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.team;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.Objects;

public class TeamType implements Comparable<TeamType> {

    private final WoolBattle woolbattle;
    private final int uniqueId;
    private final String scoreboardTag;
    private final MapSize mapSize;
    private final String invisibleTag;
    private final String displayNameKey;
    private final int weight;
    private ChatColor namecolor;
    private DyeColor woolcolor;
    private boolean enabled;

    public TeamType(WoolBattle woolbattle, int uniqueId, MapSize mapSize, String displayNameKey, int weight, DyeColor woolcolor, ChatColor namecolor, boolean enabled) {
        this.woolbattle = woolbattle;
        this.mapSize = mapSize;
        this.displayNameKey = displayNameKey;
        this.woolcolor = woolcolor;
        this.enabled = enabled;
        this.weight = weight;
        this.namecolor = namecolor;
        this.uniqueId = uniqueId;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.uniqueId; i++) {
            builder.append(ChatColor.DARK_BLUE);
        }
        builder.append(ChatColor.DARK_RED);
        this.scoreboardTag = this.weight + "I" + this.uniqueId;
        this.invisibleTag = builder.toString();
    }

    public void save() {
        woolbattle.teamManager().save(this);
    }

    public void delete() {
        woolbattle.teamManager().delete(this);
    }

    public DyeColor getWoolColor() {
        return woolcolor;
    }

    public void setWoolColor(DyeColor woolcolor) {
        this.woolcolor = woolcolor;
        save();
    }

    public String getDisplayNameKey() {
        return displayNameKey;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public String getInvisibleTag() {
        return invisibleTag;
    }

    public String getIngameScoreboardTag() {
        return "ig" + getWeight();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public MapSize mapSize() {
        return mapSize;
    }

    public int getMaxPlayers() {
        return mapSize.teamSize();
    }

    public ChatColor getNameColor() {
        return namecolor;
    }

    public void setNameColor(ChatColor namecolor) {
        this.namecolor = namecolor;
        save();
    }

    public String getScoreboardTag() {
        return scoreboardTag;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamType teamType = (TeamType) o;
        return uniqueId == teamType.uniqueId && weight == teamType.weight && enabled == teamType.enabled && Objects.equals(woolbattle, teamType.woolbattle) && Objects.equals(scoreboardTag, teamType.scoreboardTag) && Objects.equals(mapSize, teamType.mapSize) && Objects.equals(invisibleTag, teamType.invisibleTag) && Objects.equals(displayNameKey, teamType.displayNameKey) && namecolor == teamType.namecolor && woolcolor == teamType.woolcolor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(woolbattle, uniqueId, scoreboardTag, mapSize, invisibleTag, displayNameKey, weight, namecolor, woolcolor, enabled);
    }

    @Override
    public String toString() {
        return getDisplayNameKey();
    }

    @Override
    public int compareTo(TeamType o) {
        return -Integer.compare(o.getWeight(), weight);
    }
}
