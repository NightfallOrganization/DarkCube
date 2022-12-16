/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.state.lobby;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.listener.LobbyBlock;
import eu.darkcube.minigame.smash.listener.LobbyEntityDamage;
import eu.darkcube.minigame.smash.listener.LobbyInteract;
import eu.darkcube.minigame.smash.listener.LobbyInventoryClick;
import eu.darkcube.minigame.smash.listener.LobbyPlayerJoin;
import eu.darkcube.minigame.smash.state.GameState;
import eu.darkcube.minigame.smash.user.User;
import eu.darkcube.minigame.smash.util.Item;
import eu.darkcube.minigame.smash.util.Locations;

public class Lobby extends GameState {

	private Location spawn;

	public final Map<User, Integer> VOTE_LIFES = new HashMap<>();
	public final Map<User, eu.darkcube.minigame.smash.map.Map> VOTE_MAPS = new HashMap<>();

	public Lobby() {
		super(new LobbyPlayerJoin(), new LobbyBlock(), new LobbyInteract(), new LobbyInventoryClick(), new LobbyEntityDamage());
		setSpawn(Locations.deserialize(Main.getInstance().getConfig("spawns").getString("lobby"),
				Locations.DEFAULT_LOCATION));
	}

	public void setItems(User user) {
		if (user.isOnline()) {
			PlayerInventory inv = user.getPlayer().getInventory();
			inv.clear();
			inv.setArmorContents(new ItemStack[4]);
			inv.setItem(4, Item.VOTING_LIFES.getItem(user));
			inv.setItem(8, Item.VOTING_MAPS.getItem(user));
		}
	}

	public void loadScoreboard(User user) {
		if (user.isOnline()) {
			Player p = user.getPlayer();
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			Scoreboard sb = p.getScoreboard();
			if (sb.getTeam(p.getName()) == null) {
				Team team = sb.registerNewTeam(p.getName());
				team.setPrefix(user.getPrefix());
				team.setSuffix(user.getSuffix());
				team.addEntry(p.getName());
			}
		}
	}

	public void teleport(User user) {
		if (user.isOnline())
			user.getPlayer().teleport(spawn);
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		YamlConfiguration cfg = Main.getInstance().getConfig("spawns");
		cfg.set("lobby", Locations.serialize(spawn));
		Main.getInstance().saveConfig(cfg);
	}

	@Override
	protected void onEnable() {
		Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(t -> t.unregister());
		Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().forEach(o -> o.unregister());
		Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
		Team player = sb.registerNewTeam("player");
		player.setPrefix("§7");
		for (Player p : Bukkit.getOnlinePlayers()) {
			player.addEntry(p.getName());
		}

	}

	@Override
	protected void onDisable() {
	}
}
