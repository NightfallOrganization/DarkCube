/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.team;

import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.api.team.TeamType;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonTeamConfiguration implements TeamConfiguration {
    private final @NotNull String key;
    private final @NotNull MapSize mapSize;
    private final @NotNull TeamType teamType;
    private @NotNull Style nameStyle;
    private @NotNull ColoredWool woolColor;

    public CommonTeamConfiguration(@NotNull String key, @NotNull MapSize mapSize, @NotNull TeamType teamType, @NotNull Style nameStyle, @NotNull ColoredWool woolColor) {
        this.key = key;
        this.mapSize = mapSize;
        this.teamType = teamType;
        this.nameStyle = nameStyle;
        this.woolColor = woolColor;
    }

    @Override
    public @NotNull String key() {
        return key;
    }

    @Override
    public @NotNull MapSize mapSize() {
        return mapSize;
    }

    @Override
    public @NotNull Style nameStyle() {
        return nameStyle;
    }

    @Override
    public void nameStyle(@NotNull Style style) {
        nameStyle = style;
    }

    @Override
    public @NotNull ColoredWool woolColor() {
        return woolColor;
    }

    @Override
    public void woolColor(@NotNull ColoredWool wool) {
        woolColor = wool;
    }

    @Override
    public @NotNull TeamType type() {
        return teamType;
    }

    @Override
    public @NotNull CommonTeamConfiguration clone() {
        return new CommonTeamConfiguration(key, mapSize, teamType, nameStyle, woolColor);
    }
}
