/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.game.ingame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.WBUser;

public class TeamSplitter {

    private final WoolBattleBukkit woolbattle;

    public TeamSplitter(Ingame ingame) {
        this.woolbattle = ingame.woolbattle();
    }

    public void splitPlayers() {
        Collection<? extends Team> teams = woolbattle.teamManager().getTeams();
        if (teams.isEmpty()) throw new InternalError("No Teams!");
        int chosenCount = 0;
        for (Team team : teams) {
            chosenCount += team.getUsers().size();
        }
        if (chosenCount == WBUser.onlineUsers().size()) { // There might be one huge team
            if (chosenCount == 0) throw new InternalError("Starting game without players!");

            for (Team team : teams) {
                if (team.getUsers().size() != chosenCount) continue;
                splitHugeTeam(team, teams);
                break;
            }
        }

        splitRemainingPlayers(teams);
    }

    private void splitRemainingPlayers(Collection<? extends Team> teams) {
        List<WBUser> spectators = WBUser.onlineUsers().stream().filter(u -> u.getTeam().isSpectator()).collect(Collectors.toCollection(ArrayList::new));
        Iterator<WBUser> it = spectators.iterator();

        if (!it.hasNext()) return;

        int max = teams.stream().findAny().orElseThrow(InternalError::new).getType().getMaxPlayers();
        for (int i = 0; i < max; i++) {
            for (Team team : teams) {
                if (team.getUsers().size() == i) {
                    WBUser user = it.next();
                    user.setTeam(team);
                    if (!it.hasNext()) return;
                }
            }
        }
    }

    /**
     * Splits the users from a single huge team into two smaller teams with the same amount of players.
     * Happens when all players chose the same team
     */
    private void splitHugeTeam(Team hugeTeam, Collection<? extends Team> teams) {

        Set<WBUser> targetUsers = new HashSet<>();
        int half = hugeTeam.getUsers().size() / 2;
        while (targetUsers.size() < half) {
            WBUser user = hugeTeam.getUsers().stream().findAny().orElseThrow(InternalError::new);
            targetUsers.add(user);
        }
        if (targetUsers.isEmpty()) throw new InternalError("Splitting failed");
        for (Team team : teams) {
            if (!team.getUsers().isEmpty()) continue;

            for (WBUser user : targetUsers) {
                user.setTeam(team);
            }
            break;
        }
    }
}
