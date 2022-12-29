/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars;

import de.pixel.bedwars.command.CommandBedwars;
import de.pixel.bedwars.command.CommandTimer;
import de.pixel.bedwars.listener.IngameBlock;
import de.pixel.bedwars.listener.ListenerChat;
import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.shop.ShopListener;
import de.pixel.bedwars.state.Endgame;
import de.pixel.bedwars.state.GameState;
import de.pixel.bedwars.state.Ingame;
import de.pixel.bedwars.state.Lobby;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Arrays;
import de.pixel.bedwars.util.I18n;
import de.pixel.bedwars.util.Message;
import de.pixel.bedwars.util.convertingrule.RuleChatColor;
import de.pixel.bedwars.util.convertingrule.RuleMap;
import de.pixel.bedwars.util.convertingrule.RuleTeam;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.libs.com.github.juliarn.npc.NPCPool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.function.Function;

public class Main extends Plugin {

	private static Main instance;
	public String atall;
	public String atteam;
	private int maxPlayers;
	private Lobby lobby = new Lobby();
	private Ingame ingame = new Ingame();
	private Endgame endgame = new Endgame();
	private NPCPool npcPool;

	public Main() {
		instance = this;
		npcPool = NPCPool.builder(this).spawnDistance(60).actionDistance(30).tabListRemoveTicks(40)
				.build();
		System.setProperty("file.encoding", "UTF-8");
	}

	@SuppressWarnings("unchecked")
	public static void sendMessage(Message msg, Function<Player, String> replacement) {
		sendMessage(msg, new Function[] {replacement});
	}

	@SuppressWarnings("unchecked")
	public static void sendMessage(Message msg, Function<Player, String> replacement,
			Function<Player, String> replacement2) {
		sendMessage(msg, new Function[] {replacement, replacement2});
	}

	public static void sendMessage(Message msg, Function<Player, String>[] replacements) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			String[] repla = new String[replacements.length];
			for (int i = 0; i < replacements.length; i++) {
				repla[i] = replacements[i].apply(p);
			}
			p.sendMessage(msg.getMessage(p, repla));
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void sendMessage(Message msg, String... replacements) {
		Function[] f = new Function[replacements.length];
		for (int i = 0; i < f.length; i++) {
			int j = i;
			f[i] = p -> replacements[j];
		}
		sendMessage(msg, f);
	}

	public static Main getInstance() {
		return instance;
	}

	@Override
	public String getCommandPrefix() {
		return getName();
	}

	public void setupScoreboard(Player p) {
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		Scoreboard sb = p.getScoreboard();
		for (Team team : Team.getAllTeams()) {
			org.bukkit.scoreboard.Team t = sb.registerNewTeam(team.getScoreboardTag());
			t.setPrefix("§" + team.getNamecolor());
		}
		for (Player all : Bukkit.getOnlinePlayers()) {
			Team team = Team.getTeam(all);
			if (team != null) {
				sb.getTeam(team.getScoreboardTag()).addEntry(all.getName());
			}
		}
		if (lobby.isActive()) {
			lobby.setupScoreboard(p);
		} else if (ingame.isActive()) {
			ingame.setupScoreboard(p);
		} else if (endgame.isActive()) {
			endgame.setupScoreboard(p);
		}
	}

	@Override
	public void onDisable() {
		IngameBlock.reset();
		if (GameState.getActiveGamestate() != null) {
			GameState.getActiveGamestate().deactivate();
		}
	}

	@Override
	public void onEnable() {

		Arrays.addConvertingRule(new RuleTeam());
		Arrays.addConvertingRule(new RuleChatColor());
		Arrays.addConvertingRule(new RuleMap());

		saveDefaultConfig("config");
		createConfig("config");
		getConfig("config").options().copyDefaults(true);
		saveConfig(getConfig("config"));
		saveDefaultConfig("spawns");
		createConfig("spawns");
		getConfig("spawns").options().copyDefaults(true);
		saveConfig(getConfig("spawns"));

		atall = getConfig("config").getString("at_all", "@a");
		atteam = getConfig("config").getString("at_team", "@t");

		I18n.init();
		Map.init();
		Team.init();
		maxPlayers = Team.getTeams().size() * Team.getMaxPlayers();

		CommandAPI.enable(this, new CommandBedwars());
		CommandAPI.enable(this, new CommandTimer());

		lobby.activate();

		Bukkit.getPluginManager().registerEvents(new ListenerChat(), this);
		Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
	}

	public Lobby getLobby() {
		return lobby;
	}

	public Ingame getIngame() {
		return ingame;
	}

	public Endgame getEndgame() {
		return endgame;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public NPCPool getNpcPool() {
		return npcPool;
	}
}
