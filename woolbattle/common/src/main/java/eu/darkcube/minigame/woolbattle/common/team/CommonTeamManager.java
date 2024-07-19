/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.team;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.TeamManager;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public class CommonTeamManager implements TeamManager {
    private final @NotNull Game game;
    private final @NotNull Map<UUID, CommonTeam> teams;

    public CommonTeamManager(@NotNull CommonGame game, @NotNull MapSize mapSize) {
        this.game = game;
        var teams = new HashMap<UUID, CommonTeam>();
        for (var configuration : game.woolbattle().teamRegistry().teamConfigurations(mapSize)) {
            UUID id;
            do {
                id = UUID.randomUUID();
            } while (teams.containsKey(id));
            var nameStyle = configuration.nameStyle();
            var woolColor = configuration.woolColor();
            var teamType = configuration.type();
            var key = configuration.key();
            var team = new CommonTeam(game, id, key, teamType, nameStyle, woolColor);
            teams.put(id, team);
        }
        this.teams = Map.copyOf(teams);
    }

    @Override
    public @NotNull Game game() {
        return game;
    }

    @Override
    public @Nullable CommonTeam team(UUID uniqueId) {
        return teams.get(uniqueId);
    }

    @Override
    public @NotNull @Unmodifiable Collection<CommonTeam> teams() {
        return teams.values();
    }
}
