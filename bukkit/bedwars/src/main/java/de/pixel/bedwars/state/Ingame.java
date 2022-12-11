/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.state;

import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.*;

import de.pixel.bedwars.*;
import de.pixel.bedwars.listener.*;
import de.pixel.bedwars.listener.special.*;
import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.spawner.*;
import de.pixel.bedwars.spawner.io.*;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.*;

public class Ingame extends GameState {

	private Map map;

	public java.util.Map<UUID, Team> disconnectedPlayers = new HashMap<>();
	public Set<Pair<Player, Player>> kills = new HashSet<>();
	public List<ItemSpawner> spawners = new ArrayList<>();
	public Set<Team> eliminated = new HashSet<>();
	public boolean isEnabling = false;

	public Ingame() {
		super(new IngameBlock(), new IngameEntityDamage(), new IngamePlayerJoin(), new IngamePlayerQuit(),
				new IngameItemMerge(), new IngameItemPickup(), new IngameFoodLevelChange(), new ListenerGlassBreaker());
	}

	@Override
	protected void onEnable() {
		IngameBlock.reset();
		isEnabling = true;
		kills.clear();
		eliminated.clear();
		disconnectedPlayers.clear();
		IngameEntityDamage.lastHit.clear();
		Lobby lobby = Main.getInstance().getLobby();
		this.map = lobby.map;

		splitPlayersToTeams();

		for (Team team : Team.getTeams()) {
			if (team.getPlayers().size() == 0) {
				team.setBed0(false);
				Location loc = getMap().getBed(team);
				if (loc != null) {
					BedBreakAgent.execute(loc);
				}
			}
		}

		Bukkit.getOnlinePlayers().forEach(this::setupPlayer);

		for (String spawnerString : SpawnerIO.getSpawnersStringList()) {
			ItemSpawner spawner = SpawnerIO.fromString(spawnerString);
			spawners.add(spawner);
			spawner.start();
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				checkTeamWon();
			}
		}.runTaskTimer(Main.getInstance(), 10, 10);
		isEnabling = false;
	}

	@Override
	protected void onDisable() {
		for (ItemSpawner spawner : spawners) {
			spawner.stop();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player o : Bukkit.getOnlinePlayers()) {
				if (!o.canSee(p))
					o.showPlayer(p);
			}
		}
	}

	public Map getMap() {
		return map;
	}

	private void setupSpectator(Player p) {
		if (isActive()) {
			reset(p);
			p.setSaturation(0);
			p.spigot().setCollidesWithEntities(false);
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (all != p && Team.getTeam(all) != Team.SPECTATOR) {
					all.hidePlayer(p);
				} else {
					p.showPlayer(all);
				}
			}
		}
	}

	public void checkTeamWon() {
		if (!this.isEnabling) {
			Set<Team> teams = new HashSet<>();
			for (Team team : Team.getTeams()) {
				Set<UUID> disconnected = this.disconnectedPlayers.entrySet().stream()
						.filter(e -> e.getValue() == team).map(Entry::getKey).collect(Collectors.toSet());
				if (team.getPlayers().size() != 0 || (team.hasBed() && disconnected.size() != 0)) {
					teams.add(team);
				}
			}
			if (teams.size() == 1) {
				Main.getInstance().getEndgame().activate();
			}
		}
	}

	public void checkEliminated(Team team) {
		if (isActive()) {
			if (!eliminated.contains(team)) {
				if (team.isEliminated()) {
					eliminated.add(team);
					if (team != Team.SPECTATOR) {
						Main.sendMessage(Message.TEAM_ELIMINATED, team::toString);
					}
				}
			}
		}
	}

	private void reset(Player p) {
		Bukkit.getOnlinePlayers().stream().filter(s -> Team.getTeam(p) != Team.SPECTATOR && !s.canSee(p))
				.forEach(s -> s.showPlayer(p));
		p.setGameMode(GameMode.SURVIVAL);
		p.closeInventory();
		p.setMaxHealth(20);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		p.setNoDamageTicks(40);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setExhaustion(0);
		p.setSaturation(5);
		p.spigot().setCollidesWithEntities(true);
	}

	public void checkPlayerOffline(Team team, UUID uuid) {
		if (!team.hasBed()) {
			disconnectedPlayers.remove(uuid);
		}
	}

	public void kill(Player p) {
		Player killer = IngameEntityDamage.lastHit.get(p);
		IngameEntityDamage.lastHit.remove(p);
		Team team = Team.getTeam(p);
		reset(p);
		if (killer != null) {
			Main.sendMessage(Message.PLAYER_WAS_KILLED, "§" + team.getNamecolor() + p.getName(),
					"§" + Team.getTeam(killer).getNamecolor() + killer.getName());
			killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 10, 2);
		} else {
			Main.sendMessage(Message.PLAYER_DIED, "§" + team.getNamecolor() + p.getName());
		}
		if (!team.hasBed()) {
			Main.sendMessage(Message.PLAYER_WAS_FINAL_KILLED, "§" + team.getNamecolor() + p.getName());
			Team.SPECTATOR.addPlayer(p);
			setupSpectator(p);
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 20, false, false), true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20, 20, false, false), true);
		p.teleport(map.getSpawnSafe(team.getTranslationName()));
		p.playSound(p.getLocation(), Sound.BAT_DEATH, 10, 0.3F);
	}

	public void setScoreboardValues(Player p) {
		Scoreboard sb = p.getScoreboard();
		for (Team team : Team.getTeams()) {
			org.bukkit.scoreboard.Team t = sb.getTeam(team.getSidebarTag());
			String suffix = team.hasBed() ? "§2\u2714 "
					: team.getPlayers().size() != 0 ? "§c" + Integer.toString(team.getPlayers().size()) + " "
					: "§4\u2717 ";
			t.setPrefix(suffix);
		}
	}

	public void setupScoreboard(Player p) {
		Scoreboard sb = p.getScoreboard();
		Objective obj = sb.registerNewObjective("lobby", "dummy");
		obj.setDisplayName(Message.LOBBY_OBJECTIVE_TITLE.getMessage(p));
		int id = Team.getTeams().size();
		for (Team team : Team.getTeams()) {
			org.bukkit.scoreboard.Team t = sb.registerNewTeam(team.getSidebarTag());
			t.setSuffix(team.toString(p));
			t.addEntry(team.getInvisiblePlayerName());
			obj.getScore(team.getInvisiblePlayerName()).setScore(id);
			id--;
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		setScoreboardValues(p);
	}

	public void setupPlayer(Player p) {
		Team team = Team.getTeam(p);
		if (team == Team.SPECTATOR) {
			setupSpectator(p);
		} else {
			reset(p);
		}
//		new NPCShop(p, new Location(p.getWorld(), 6.5, 101, 5.5));
		p.teleport(map.getSpawnSafe(team.getTranslationName()));
		Main.getInstance().setupScoreboard(p);
	}

	public void splitPlayersToTeams() {
		Collection<Team> teams = Team.getTeams();
		int chosenCount = 0;
		for (Team team : teams) {
			chosenCount += team.getPlayers().size();
		}
		if (chosenCount == Bukkit.getOnlinePlayers().size()) {
			if (chosenCount == 0) {
				throw new Error("Starting game without players");
			}
			Set<Player> players = new HashSet<>();
			for (Team team : teams) {
				if (team.getPlayers().size() == chosenCount) {
					int half = team.getPlayers().size() / 2;
					while (players.size() < half) {
						Optional<Player> o = team.getPlayers().stream().findAny();
						if (o.isPresent()) {
							players.add(o.get());
						} else {
							Main.getInstance().sendConsole("§cUser of team was somehow not found");
						}
					}
					break;
				}
			}
			if (players.size() != 0) {
				for (Team team : teams) {
					if (team.getPlayers().size() == 0) {
						for (Player player : players) {
							team.addPlayer0(player);
						}
						break;
					}
				}
			}
		}
		int max = Team.getMaxPlayers();
		for (int i = 0; i < max; i++) {
			for (Team team : teams) {
				if (team.getPlayers().size() == i) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (Team.getTeam(p) == Team.SPECTATOR) {
							team.addPlayer0(p);
							break;
						}
					}
				}
			}
		}
	}

	public void setScoreboardValues() {
		if (!isEnabling && isActive())
			Bukkit.getOnlinePlayers().forEach(this::setScoreboardValues);
	}
}
