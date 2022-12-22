/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.state;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.listener.*;
import de.pixel.bedwars.util.*;
import eu.darkcube.system.inventoryapi.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Lobby extends GameState {

	public static LobbyCountdown lobbyCountdown = new LobbyCountdown();

	public static final ScoreboardTeam[] LOBBY_SCOREBOARD_TEAMS = new ScoreboardTeam[] {
			ScoreboardTeam.LOBBY_MAPS,
			ScoreboardTeam.LOBBY_TIMER, ScoreboardTeam.LOBBY_GOLD, ScoreboardTeam.LOBBY_IRON
	};

	public Lobby() {
		super(new LobbyBlock(), new LobbyPlayerJoin(), new LobbyInventoryClick(), new LobbyFoodLevelChange(),
				new LobbyEntityDamage(), new LobbyPlayerInteract(), new LobbyItemDropPickup(), new LobbyPlayerQuit(),
				lobbyCountdown, new LobbyInventoryClose(), new LobbyPlayerLogin());
	}

	public boolean gold = true;
	public boolean iron = true;
	public de.pixel.bedwars.map.Map map = null;

	public Map<Player, Boolean> VOTE_GOLD = new HashMap<>();
	public Map<Player, Boolean> VOTE_IRON = new HashMap<>();
	public Map<Player, de.pixel.bedwars.map.Map> VOTE_MAP = new HashMap<>();

	public Map<Player, Inventory> INVENTORY_OPEN_TEAMS = new HashMap<>();
	public Map<Player, Inventory> INVENTORY_OPEN_MAPS = new HashMap<>();
	public Map<Player, Inventory> INVENTORY_OPEN_GOLD = new HashMap<>();
	public Map<Player, Inventory> INVENTORY_OPEN_IRON = new HashMap<>();

	private Location spawn;

	@Override
	protected void onEnable() {
		VOTE_GOLD.clear();
		VOTE_IRON.clear();
		VOTE_MAP.clear();

		de.pixel.bedwars.team.Team.getAllTeams()
				.forEach(t -> de.pixel.bedwars.team.Team.players.get(t).clear());
		for (de.pixel.bedwars.team.Team team : de.pixel.bedwars.team.Team.getTeams()) {
			team.setBed0(true);
		}

		reloadSpawn();
		Bukkit.getOnlinePlayers().forEach(this::setupPlayer);
		Bukkit.getOnlinePlayers().stream().map(p -> new PlayerJoinEvent(p, "joined bla bla bla")).forEach(e -> {
			lobbyCountdown.handle(e);
		});

		this.map = new ArrayList<>(de.pixel.bedwars.map.Map.getMaps())
				.get(new Random().nextInt(de.pixel.bedwars.map.Map.getMaps().size()));
		recalculateGold();
		recalculateIron();
		recalculateMap();
	}

	@Override
	protected void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			if (obj != null) {
				obj.setDisplaySlot(null);
			}
		}
		lobbyCountdown.cancel();
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		YamlConfiguration cfg = Main.getInstance().getConfig("spawns");
		cfg.set("lobby", Locations.serialize(spawn));
		Main.getInstance().saveConfig(cfg);
	}

	public Location getSpawn() {
		return spawn;
	}

	public void reloadSpawn() {
		spawn = Locations.deserialize(Main.getInstance().getConfig("spawns").getString("lobby"),
				Locations.DEFAULT_LOCATION);
	}

	private <T> T recalculate(Collection<T> votes, T orDefault) {
		Map<T, Integer> count = new HashMap<>();
		for (T vote : votes) {
			count.put(vote, count.getOrDefault(vote, 0) + 1);
		}
		int highestCount = -1;
		T highestVote = null;
		boolean split = false;
		for (T t : count.keySet()) {
			int voteCount = count.get(t);
			if (voteCount > highestCount) {
				highestCount = voteCount;
				highestVote = t;
				split = false;
			} else if (voteCount == highestCount) {
				split = true;
			}
		}
		return highestVote == null || split ? orDefault : highestVote;
	}

	public void recalculateIron() {
		iron = recalculate(VOTE_IRON.values(), true);
		Bukkit.getOnlinePlayers().forEach(this::setScoreboardValues);
	}

	public void recalculateGold() {
		gold = recalculate(VOTE_GOLD.values(), false);
		Bukkit.getOnlinePlayers().forEach(this::setScoreboardValues);
	}

	public void recalculateMap() {
		map = recalculate(VOTE_MAP.values(), map);
		Bukkit.getOnlinePlayers().forEach(this::setScoreboardValues);
	}

	private <T> int getVoteCount(Map<?, T> voteMap, T voteValue) {
		int count = 0;
		for (T b : voteMap.values()) {
			if (b == voteValue) {
				count++;
			}
		}
		return count;
	}

	public void recalculateItemsVotingGoldInventories() {
		for (Player pl : INVENTORY_OPEN_GOLD.keySet()) {
			setItemsVotingGoldInventory(INVENTORY_OPEN_GOLD.get(pl), pl);
		}
	}

	public void openVotingGoldInventory(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9,
				Message.INVENTORY_TITLE_LOBBY_VOTING_GOLD.getMessage(I18n.getPlayerLanguage(p)));
		INVENTORY_OPEN_GOLD.put(p, inv);
		setItemsVotingGoldInventory(inv, p);
		p.openInventory(inv);
	}

	public void setItemsVotingGoldInventory(Inventory inv, Player p) {
		ItemBuilder b = new ItemBuilder(
				Item.LOBBY_VOTING_GOLD_YES.getItem(p, new String[0], Integer.toString(getVoteCount(VOTE_GOLD, true))));
		if (VOTE_GOLD.getOrDefault(p, false)) {
			b.glow();
		}
		inv.setItem(3, b.build());

		b = new ItemBuilder(
				Item.LOBBY_VOTING_GOLD_NO.getItem(p, new String[0], Integer.toString(getVoteCount(VOTE_GOLD, false))));
		if (!VOTE_GOLD.getOrDefault(p, true)) {
			b.glow();
		}
		inv.setItem(5, b.build());

	}

	public void recalculateItemsVotingIronInventories() {
		for (Player pl : INVENTORY_OPEN_IRON.keySet()) {
			setItemsVotingIronInventory(INVENTORY_OPEN_IRON.get(pl), pl);
		}
	}

	public void openVotingIronInventory(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9,
				Message.INVENTORY_TITLE_LOBBY_VOTING_IRON.getMessage(I18n.getPlayerLanguage(p)));
		INVENTORY_OPEN_IRON.put(p, inv);
		setItemsVotingIronInventory(inv, p);
		p.openInventory(inv);
	}

	public void setItemsVotingIronInventory(Inventory inv, Player p) {
		ItemBuilder b = new ItemBuilder(
				Item.LOBBY_VOTING_IRON_YES.getItem(p, new String[0], Integer.toString(getVoteCount(VOTE_IRON, true))));
		if (VOTE_IRON.getOrDefault(p, false)) {
			b.glow();
		}
		inv.setItem(3, b.build());

		b = new ItemBuilder(
				Item.LOBBY_VOTING_IRON_NO.getItem(p, new String[0], Integer.toString(getVoteCount(VOTE_IRON, false))));
		if (!VOTE_IRON.getOrDefault(p, true)) {
			b.glow();
		}
		inv.setItem(5, b.build());
	}

	public void recalculateItemsTeamInventories() {
		for (Player pl : INVENTORY_OPEN_TEAMS.keySet()) {
			setItemsTeamsInventory(INVENTORY_OPEN_TEAMS.get(pl), pl);
		}
	}

	public void openTeamsInventory(Player p) {
		int size = de.pixel.bedwars.team.Team.getTeams().size();
		if (size % 9 != 0) {
			size /= 9;
			size++;
			size *= 9;
		}
		if (size == 0)
			size = 9;
		Inventory inv = Bukkit.createInventory(null, size, Item.LOBBY_TEAMS.getItem(p).getItemMeta().getDisplayName());
		INVENTORY_OPEN_TEAMS.put(p, inv);
		setItemsTeamsInventory(inv, p);
		p.openInventory(inv);
	}

	public void setItemsTeamsInventory(Inventory inv, Player p) {
		inv.clear();
		int slot = 0;
		de.pixel.bedwars.team.Team t = de.pixel.bedwars.team.Team.getTeam(p);
		for (de.pixel.bedwars.team.Team team : de.pixel.bedwars.team.Team.getTeams()) {
			ItemBuilder b = new ItemBuilder(Material.BOOK);
			b.displayname(
					"§" + team.getNamecolor() + I18n.translate(I18n.getPlayerLanguage(p), team.getTranslationName()));
			for (Player pl : team.getPlayers()) {
				b.lore("§" + team.getNamecolor() + pl.getName());
			}
			b.getUnsafe().setString("itemId", "team").setString("team", team.getTranslationName());
			if (t == team) {
				b.glow();
			}
			inv.setItem(slot++, b.build());
		}
	}

	public void recalculateItemsVotingMapsInventories() {
		for (Player pl : INVENTORY_OPEN_MAPS.keySet()) {
			setItemsVotingMapsInventory(INVENTORY_OPEN_MAPS.get(pl), pl);
		}
	}

	public void openVotingMapsInventory(Player p) {
		int size = de.pixel.bedwars.map.Map.getMaps().size();
		if (size % 9 != 0) {
			size /= 9;
			size++;
			size *= 9;
		}
		if (size == 0)
			size = 9;
		Inventory inv = Bukkit.createInventory(null, size, Item.LOBBY_MAPS.getItem(p).getItemMeta().getDisplayName());
		INVENTORY_OPEN_MAPS.put(p, inv);
		setItemsVotingMapsInventory(inv, p);
		p.openInventory(inv);
	}

	public void setItemsVotingMapsInventory(Inventory inv, Player p) {
		inv.clear();
		int slot = 0;
		de.pixel.bedwars.map.Map m = VOTE_MAP.get(p);
		for (de.pixel.bedwars.map.Map map : de.pixel.bedwars.map.Map.getMaps()) {
			ItemBuilder b = new ItemBuilder(map.getIcon().getMaterial()).durability(map.getIcon().getId());
			b.displayname("§6" + map.getName());
			b.lore("§a" + getVoteCount(VOTE_MAP, map));
			b.getUnsafe().setString("itemId", "map").setString("map", map.getName());
			if (map == m) {
				b.glow();
			}

			inv.setItem(slot++, b.build());
		}
	}

	public void setupPlayer(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		PlayerInventory inv = p.getInventory();
		inv.setItem(0, Item.LOBBY_MAPS.getItem(p));
		inv.setItem(1, Item.LOBBY_TEAMS.getItem(p));
		inv.setItem(7, Item.LOBBY_VOTING_IRON.getItem(p));
		inv.setItem(8, Item.LOBBY_VOTING_GOLD.getItem(p));
		Main.getInstance().setupScoreboard(p);
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		de.pixel.bedwars.team.Team.SPECTATOR.addPlayer0(p);
		p.setMaxHealth(20);
		p.teleport(getSpawn());
		p.setExp(0);
		p.setLevel(0);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setSaturation(0);
		Bukkit.getOnlinePlayers().stream().filter(s -> !s.canSee(p)).forEach(s -> s.showPlayer(p));
	}

	public void setScoreboardValues(Player p) {
		Scoreboard sb = p.getScoreboard();
		Team team = sb.getTeam(ScoreboardTeam.LOBBY_GOLD.getTag());
		team.setSuffix(gold ? "§2\u2714" : "§4\u2717");

		team = sb.getTeam(ScoreboardTeam.LOBBY_IRON.getTag());
		team.setSuffix(iron ? "§2\u2714" : "§4\u2717");

		team = sb.getTeam(ScoreboardTeam.LOBBY_MAPS.getTag());
		team.setSuffix(map == null ? "No Maps" : map.getName());

		lobbyCountdown.setScoreboardValue(p);
	}

	public void setupScoreboard(Player p) {
		Scoreboard sb = p.getScoreboard();
		Objective obj = sb.registerNewObjective("lobby", "dummy");
		obj.setDisplayName(Message.LOBBY_OBJECTIVE_TITLE.getMessage(p));
		int id = Lobby.LOBBY_SCOREBOARD_TEAMS.length;
		for (ScoreboardTeam team : Lobby.LOBBY_SCOREBOARD_TEAMS) {
			org.bukkit.scoreboard.Team t = sb.registerNewTeam(team.getTag());
			t.setPrefix(I18n.translate(I18n.getPlayerLanguage(p), team.getPrefixMessage()));
			t.addEntry(team.getEntry());
			obj.getScore(team.getEntry()).setScore(id);
			id--;
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		setScoreboardValues(p);
	}

}
