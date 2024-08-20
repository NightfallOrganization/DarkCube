/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;

public class TeamSplitter {
    private final CommonGame game;

    public TeamSplitter(CommonGame game) {
        this.game = game;
    }

    public void splitPlayers() {
        var teams = game.teamManager().playingTeams();
        var chosenPlayerCount = 0;
        for (var team : teams) {
            chosenPlayerCount += team.users().size();
        }
        if (chosenPlayerCount == game.users().size()) {
            // All players have selected a team, but they might all be in the same team
            if (chosenPlayerCount == 0) game.api().woolbattle().logger().warn("Starting game without players");

            for (var team : teams) {
                if (team.users().size() != chosenPlayerCount) {
                    continue;
                }

                splitHugeTeam(team, teams);
                break;
            }
        }
        splitRemainingPlayers(teams);
    }

    private void splitRemainingPlayers(List<CommonTeam> teams) {
        var spectators = List.copyOf(game.teamManager().spectator().users());
        var it = spectators.iterator();
        if (!it.hasNext()) return;
        var max = game.mapSize().teamSize();
        for (var targetPlayerCount = 0; targetPlayerCount < max; targetPlayerCount++) {
            for (var team : teams) {
                if (team.users().size() == targetPlayerCount) {
                    var user = it.next();
                    user.team(team);
                    if (!it.hasNext()) return;
                }
            }
        }
    }

    private void splitHugeTeam(CommonTeam hugeTeam, List<CommonTeam> teams) {
        var targetUsers = new ArrayList<WBUser>();
        var half = hugeTeam.users().size() / 2;
        var random = new Random();
        var hugeTeamUsers = new ArrayList<>(hugeTeam.users());
        while (targetUsers.size() < half) {
            var user = hugeTeamUsers.remove(random.nextInt(hugeTeamUsers.size()));
            targetUsers.add(user);
        }
        if (targetUsers.isEmpty()) throw new InternalError("Splitting failed");
        for (var team : teams) {
            if (!team.users().isEmpty()) continue;
            for (var user : targetUsers) {
                user.team(team);
            }
            break;
        }
    }
}
