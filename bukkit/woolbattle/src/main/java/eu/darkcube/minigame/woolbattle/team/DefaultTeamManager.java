package eu.darkcube.minigame.woolbattle.team;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.system.language.core.Language;

public class DefaultTeamManager implements TeamManager {

	private final Collection<Team> TEAMS;
	private final Map<User, Team> TEAM_BY_USER;
	private Team SPECTATOR;

	public DefaultTeamManager() {
		TEAMS = new HashSet<>();
		TEAM_BY_USER = new HashMap<>();
	}

	@Override
	public void setTeam(User user, Team team) {
		Team t = getTeam(user);
		for (Player p : Bukkit.getOnlinePlayers()) {
			User u = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			Scoreboard s = new Scoreboard(u);
			if (t != null)
				s.getTeam(t.getType().getScoreboardTag()).removePlayer(user.getPlayerName());
			s.getTeam(team.getType().getScoreboardTag()).addPlayer(user.getPlayerName());
		}
		if (!team.isSpectator()) {
			for (Player o : Bukkit.getOnlinePlayers()) {
				o.showPlayer(user.getBukkitEntity());
			}
		}
		TEAM_BY_USER.put(user, team);
		if (user.getActivePerk1() == null || user.getActivePerk2() == null
						|| user.getPassivePerk() == null
						|| user.getEnderPearl() == null) {
			user.loadPerks();
		}
		Main.getInstance().getLobby().listenerItemTeams.reloadInventories();
		if (Main.getInstance().getIngame().isEnabled()) {
			Main.getInstance().getIngame().setPlayerItems(user);
			Main.getInstance().getIngame().checkGameEnd();
		}
	}

	@SuppressWarnings("deprecation")
	public final DefaultTeamManager loadSpectator(TeamType spectator) {
		if (TeamType.SPECTATOR != null) {
			spectator = null;
		}
		if (spectator == null) {
			TeamType.SPECTATOR = new TeamType("SPECTATOR", 99,
							DyeColor.GRAY.getWoolData(), ChatColor.GRAY, false,
							-1);
			SPECTATOR = getOrCreateTeam(TeamType.SPECTATOR);
			TEAMS.remove(SPECTATOR);
		} else {
			TeamType.SPECTATOR = spectator;
			SPECTATOR = getOrCreateTeam(TeamType.SPECTATOR);
			TEAMS.remove(SPECTATOR);
		}
		return this;
	}

	@Override
	public Team getTeam(UUID id) {
		for (Team team : TEAMS) {
			if (team.getUniqueId().equals(id)) {
				return team;
			}
		}
		return null;
	}

	@Override
	public Team getTeam(User user) {
		return TEAM_BY_USER.get(user);
	}

	@Override
	public Team getTeam(TeamType type) {
		for (Team team : TEAMS) {
			if (team.getType().equals(type)) {
				return team;
			}
		}
		return SPECTATOR;
	}

	@Override
	public Team getTeam(String displayName, Language inLanguage) {
		for (Team team : TEAMS) {
			if (Message.getMessage(team.getType().getDisplayNameKey(), inLanguage).equals(displayName)) {
				return team;
			}
		}
		return null;
	}

	@Override
	public Team getOrCreateTeam(TeamType type) {
		Team team = getTeam(type);
		if (team != SPECTATOR && team != null)
			return team;
		team = new DefaultTeam(type);
		TEAMS.add(team);
		return team;
	}

	@Override
	public Team getSpectator() {
		return SPECTATOR;
	}

	@Override
	public Collection<? extends Team> getTeams() {
		return Collections.unmodifiableCollection(TEAMS);
	}

	@Override
	public Team getTeam(String displayNameKey) {
		Set<Team> teams = getTeams().stream().filter(t -> t.getType().getDisplayNameKey().equals(displayNameKey)).collect(Collectors.toSet());
		for (Team team : teams)
			return team;
		return null;
	}
}
