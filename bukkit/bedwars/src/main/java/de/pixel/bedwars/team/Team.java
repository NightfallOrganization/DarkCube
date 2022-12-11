/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.team;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.google.gson.JsonSyntaxException;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.state.Lobby;
import de.pixel.bedwars.util.BedwarsGson;
import de.pixel.bedwars.util.DontSerialize;
import de.pixel.bedwars.util.I18n;

public class Team {

	public static Map<Team, Collection<Player>> players = new HashMap<>();

	private String invisiblePlayerName;
	private String scoreboardTag;
	@DontSerialize
	private String translationName;
	@DontSerialize
	private String sidebarTag;
	private boolean enabled;
	private char namecolor;
	private static int maxPlayers = Main.getInstance().getConfig().getInt("maxTeamPlayers");
	private static List<File> files = new ArrayList<>();
	public static Team SPECTATOR = null;
	@DontSerialize
	private boolean deleted = false;
	@DontSerialize
	private boolean bed = true;

	static {
		File folder = getFolder();
		folder.mkdirs();
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".team")) {
				try {
					BufferedReader r = new BufferedReader(
							new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
					Team team = BedwarsGson.GSON.fromJson(r, Team.class);
					team.translationName = file.getName().substring(0, file.getName().length() - 5);
					if (team.translationName.equals("SPECTATOR")) {
						SPECTATOR = team;
					}
					r.close();
					Main.getInstance().sendConsole("§aLoaded team §" + team.namecolor + team.translationName);
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (JsonSyntaxException ex) {
					System.out.println("Invalid team-file " + file.getName() + ":");
					ex.printStackTrace();
				}
			} else {
				System.out.println("Invalid team-file " + file.getName());
			}
		}
		if (SPECTATOR == null) {
			SPECTATOR = new Team("SPECTATOR", true, '7');
		}
	}

	public static void init() {
		
	}

	private Team() {
		players.put(this, new HashSet<>());
		files.add(getFile());
	}

	private Team(String translationName, boolean enabled, char namecolor) {
		this();
//		this.invisiblePlayerName = invisiblePlayerName;
		this.scoreboardTag = translationName;
		this.translationName = translationName;
		this.enabled = enabled;
		this.namecolor = namecolor;
		invisiblePlayerName = randomInvisName();
		save();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.getScoreboard().registerNewTeam(scoreboardTag);
		}
	}

	private String sidebarTag() {
		StringBuilder b = new StringBuilder();
		Random r = new Random();
		for (int i = 0; i < 7; i++) {
			ChatColor c = ChatColor.values()[r.nextInt(ChatColor.values().length)];
			b.append(c.toString());
		}
		for (Team team : getAllTeams()) {
			if ((team != this && b.toString().equals(team.sidebarTag)) || team.scoreboardTag.equals(b.toString())) {
				return sidebarTag();
			}
		}
		return b.toString();
	}

	private String randomInvisName() {
		StringBuilder b = new StringBuilder();
		Random r = new Random();
		for (int i = 0; i < 7; i++) {
			ChatColor c = ChatColor.values()[r.nextInt(ChatColor.values().length)];
			b.append(c.toString());
		}
		for (Team team : getAllTeams()) {
			if (team != this && team.invisiblePlayerName.equals(b.toString())) {
				return randomInvisName();
			}
		}
		return b.toString();
	}

	public void setBed(boolean bed) {
		setBed0(bed);
		Main.getInstance().getIngame().checkEliminated(this);
	}

	public void setBed0(boolean bed) {
		this.bed = bed;
	}

	public boolean hasBed() {
		return bed;
	}

	public String getSidebarTag() {
		if (sidebarTag == null) {
			sidebarTag = sidebarTag();
		}
		return sidebarTag;
	}

	public static Team createTeam(String translationName) {
		return new Team(translationName, false, '7');
	}

	public static Collection<String> getAllTeamNames() {
		Set<String> names = new HashSet<>();
		for (Team team : Team.getAllTeams()) {
			names.add(team.translationName);
		}
		return names;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public static Collection<Team> getAllTeams() {
		return players.keySet();
	}

	public static Collection<Team> getTeams() {
		Set<Team> teams = new HashSet<>();
		for (Team t : getAllTeams()) {
			if (t.isEnabled() && SPECTATOR != t) {
				teams.add(t);
			}
		}
		return teams;
	}

	public Collection<Player> getPlayers() {
		return Collections.unmodifiableCollection(players.get(this));
	}

	public void setNamecolor(char namecolor) {
		this.namecolor = namecolor;
		save();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		save();
	}

	public static int getMaxPlayers() {
		return maxPlayers;
	}

	public static Team getTeam(Player p) {
		for (Team team : getAllTeams()) {
			if (team.getPlayers().contains(p)) {
				return team;
			}
		}
		return null;
	}

	public boolean isEliminated() {
		Collection<Player> players = getPlayers();
		Set<UUID> disconnected = Main.getInstance().getIngame().disconnectedPlayers.entrySet().stream()
				.filter(e -> e.getValue() == this).map(Entry::getKey).collect(Collectors.toSet());
		return this == SPECTATOR || (players.size() == 0 && !hasBed())
				|| (players.size() == 0 && disconnected.size() == 0);
	}

	public void removePlayer0(Player p) {
		players.get(this).remove(p);
		for (Player all : Bukkit.getOnlinePlayers()) {
			Scoreboard sb = all.getScoreboard();
			sb.getTeam(scoreboardTag).removeEntry(p.getName());
		}
		Lobby lobby = Main.getInstance().getLobby();
		lobby.recalculateItemsTeamInventories();
		Main.getInstance().getIngame().checkEliminated(this);
	}

	public void removePlayer(Player p) {
		removePlayer0(p);
		Main.getInstance().getIngame().setScoreboardValues();
		Main.getInstance().getIngame().checkTeamWon();
	}

	public void addPlayer(Player p) {
		addPlayer0(p);
		Main.getInstance().getIngame().setScoreboardValues();
		Main.getInstance().getIngame().checkTeamWon();
	}

	public void addPlayer0(Player p) {
		Team t = getTeam(p);
		if (t != null)
			t.removePlayer(p);
		players.get(this).add(p);
		for (Player all : Bukkit.getOnlinePlayers()) {
			Scoreboard sb = all.getScoreboard();
			org.bukkit.scoreboard.Team te = sb.getTeam(scoreboardTag);
			if (te != null) {
				te.addEntry(p.getName());
			}
		}
		Lobby lobby = Main.getInstance().getLobby();
		lobby.recalculateItemsTeamInventories();
	}

	public String getInvisiblePlayerName() {
		return invisiblePlayerName + "§" + namecolor;
	}

	public String getScoreboardTag() {
		return scoreboardTag;
	}

	public String getTranslationName() {
		return translationName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public char getNamecolor() {
		return namecolor;
	}

	public void deleteFile() {
		File f = getFile();
		deleted = true;
		if (f.exists()) {
			f.delete();
		}
	}

	private static File getFolder() {
		return new File(Main.getInstance().getDataFolder(), "teams");
	}

	private File getFile() {
		File f = new File(getFolder(), translationName + ".team");
		return f;
	}

	@Override
	public String toString() {
		return "§" + namecolor + translationName;
	}

	public String toString(Player p) {
		return "§" + namecolor + I18n.translate(I18n.getPlayerLanguage(p), translationName);
	}

	public void save() {
		if (deleted)
			return;
		String json = BedwarsGson.GSON.toJson(this);
		File f = getFile();
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		try {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), Charsets.UTF_8));
			w.write(json);
			w.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
