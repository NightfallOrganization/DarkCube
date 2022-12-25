package eu.darkcube.system.miners.player;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.darkcube.system.miners.Miners;

public class TeamManager {

    private final Map<Player, Integer> TEAMS;

    public TeamManager() {
        TEAMS = new HashMap<>();
    }

    public void updatePlayer(Player p) {
        Miners.getPlayerManager().getMinersPlayer(p).updatePlayer();
    }

    public boolean setPlayerTeam(Player p, int team, boolean allowOverTeamSize) {
        if (!isTeamFull(team) || allowOverTeamSize || team == 0) {
            System.out.println("added player " + p.getName());
            TEAMS.put(p, team);
            updatePlayer(p);
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(Player p) {
        TEAMS.remove(p);
        updatePlayer(p);
    }

    /**
     * adds all players without team to a team
     */
    public void distributeRemainingPlayers() {
        for (int i = 1; i <= Miners.getMinersConfig().TEAM_COUNT; i++) {
            if (getPlayersWithoutTeam().isEmpty())
                return;
            fillTeam(i, getPlayersWithoutTeam());
        }
    }

    /**
     * fills a team with players from list until full
     */
    public void fillTeam(int team, List<Player> players) {
        for (Player p : players) {
            if (!setPlayerTeam(p, team, false))
                return;
        }
    }

    /**
     * If all players are on the same team, change someone's team
     */
    public void fixTeams() {
        Player prev = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (prev != null)
                if (!Objects.equals(TEAMS.get(p), TEAMS.get(prev)))
                    return;
            prev = p;
        }
        if (TEAMS.get(prev) == 1)
            setPlayerTeam(prev, 2, false);
        else
            setPlayerTeam(prev, 1, false);
    }

    public List<Player> getPlayersInTeam(int team) {
        List<Player> players = new ArrayList<>();
        TEAMS.forEach((key, value) -> {
            if (value == team)
                players.add(key);
        });
        return players;
    }

    public boolean isTeamFull(int team) {
        return !(getPlayersInTeam(team).size() < Miners.getMinersConfig().TEAM_SIZE);
    }

    public List<Player> getPlayersWithoutTeam() {
        List<Player> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (getPlayerTeam(p) == 0)
                players.add(p);
        });
        return players;
    }

    public int getPlayerTeam(Player p) {
        if (p == null)
            return 0;
        if (TEAMS.containsKey(p))
            return TEAMS.get(p);
        return 0;
    }

}
