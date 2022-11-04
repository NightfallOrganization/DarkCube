package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.InventoryBuilder;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;
import eu.darkcube.minigame.woolbattle.util.ItemManager;

public class ListenerItemTeams extends Listener<EventInteract> {

	@Override
	@EventHandler
	public void handle(EventInteract e) {
		try {
			ItemStack item = e.getItem();
			if (item != null) {
				Player p = e.getPlayer();
				User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
				if (item.hasItemMeta()) {
					if (ItemManager.getItemId(item).equals(ItemManager.getItemId(Item.LOBBY_TEAMS))) {
						e.setCancelled(true);
						if (user.getOpenInventory() != InventoryId.TEAMS) {
							openInventory(p);
						}
					} else if (ItemManager.getTeamId(item) != null) {
						if (user.getOpenInventory() == InventoryId.TEAMS) {
							e.setCancelled(true);
							Team uteam = user.getTeam();
							Team team = Main.getInstance().getTeamManager().getTeam(ItemManager.getTeamId(e.getItem()));
							if (team != null) {
								if (uteam.equals(team)) {
									p.sendMessage(Message.ALREADY_IN_TEAM.getMessage(user));
									return;
								}
								if (team.getUsers().size() >= team.getType().getMaxPlayers()) {
									p.sendMessage(Message.TEAM_IS_FULL.getMessage(user, team.getName(user)));
									return;
								}
								Main.getInstance().getTeamManager().setTeam(user, team);
								p.sendMessage(Message.CHANGED_TEAM.getMessage(user, team.getName(user)));
							}
						}
					}
				}
			}
		} catch (NullPointerException ex) {
			Main.getInstance().sendConsole(
					"§cThe Item " + e.getItem().getItemMeta().getDisplayName() + " is not correctly set up");
		}
	}

	public void reloadInventories() {
		Main.getInstance().getUserWrapper().getUsers().stream()
				.filter(user -> user.getOpenInventory() == InventoryId.TEAMS).forEach(user -> {
					openInventory(Bukkit.getPlayer(user.getUniqueId()));
				});
	}

	public void openInventory(Player p) {
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		InventoryBuilder builder = new InventoryBuilder(Message.INVENTORY_TEAMS.getMessage(user));
		List<Team> teams = new ArrayList<>(Main.getInstance().getTeamManager().getTeams());
		builder.size(teams.size());
		teams.sort(new Comparator<Team>() {
			@Override
			public int compare(Team o1, Team o2) {
				return o1.compareTo(o2);
			}
		});
		for (int i = 0; i < teams.size(); i++) {
			Team team = teams.get(i);
			ItemBuilder b = new ItemBuilder(Material.WOOL);
			b.setDisplayName(ChatColor.BOLD + team.getName(user));
			b.setDurability(team.getType().getWoolColor());
			if (team.getUsers().contains(user)) {
				b.glow();
			}
			team.getUsers().forEach(u -> b.addLore("§r§" + team.getType().getNameColor() + u.getPlayerName()));
			b.getUnsafe().setString("team", team.getType().getDisplayNameKey()).setString("itemId", "custom");
			builder.setItem(i, b.build());
		}
		p.openInventory(builder.build());
		user.setOpenInventory(InventoryId.TEAMS);
	}
}