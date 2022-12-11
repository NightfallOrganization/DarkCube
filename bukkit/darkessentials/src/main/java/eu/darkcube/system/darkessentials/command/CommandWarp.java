/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;
import eu.darkcube.system.darkessentials.util.NumbzUtils;
import eu.darkcube.system.darkessentials.util.WarpPoint;
import net.md_5.bungee.api.ChatColor;

public class CommandWarp extends Command implements Listener {

	Set<Player> teleportingPlayers = new HashSet<>();
	Set<WarpPoint> enabledWarps = new HashSet<>();
	private static final WarpPoint SPAWN = new WarpPoint("Spawn", Bukkit.getWorlds().get(0).getSpawnLocation(),
			"NETHER_STAR:0", false);

	public CommandWarp() {
		super(Main.getInstance(), "warp", new Command[0], "Teleportpunkte",
				new Argument("name", "Der Warp-Punkt, zu dem du dich teleportieren willst.", false));
		setAliases("warps", "d_warp");
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			Main.getInstance().sendMessage(Main.colorFail + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
			return true;
		}

		if (teleportingPlayers.contains(sender)) {
			Main.getInstance().sendMessage(Main.colorFail + "Es läuft bereits ein Teleportvorgang!", sender);
			return true;
		}

		enabledWarps = new HashSet<>();
		for (WarpPoint w : Main.updateWarps())
			if (w.getEnabled())
				enabledWarps.add(w);
		if (enabledWarps.isEmpty()) {
			Main.getInstance().sendMessage(Main.colorFail + "Es sind noch keine Warppunkte vorhanden.", sender);
			return true;
		}

		if (args.length > 1)
			return false;
		if (args.length == 1) {
			if (getWarp(args[0]) != null && getWarp(args[0]).isValid() && getWarp(args[0]).getEnabled()) {
				startTeleport((Player) sender, getWarp(args[0]));
			} else {
				Main.getInstance()
						.sendMessage(new StringBuilder(Main.colorFail).append("Der Warp-Punkt ").append(Main.colorValue)
								.append(args[0]).append(Main.colorFail).append(" wurde nicht gefunden!").toString(),
								sender);
			}
		} else {
			Inventory inv = Bukkit.createInventory(null, (int) (Math.ceil(enabledWarps.size() / 9.0) * 9), "Warps");
			inv.clear();
			for (WarpPoint warp : enabledWarps) {
				Location loc = warp.getLocation();
				inv.addItem(
						NumbzUtils.setNBT(NumbzUtils.getNamedItemStack(
								new ItemStack(Material.valueOf(warp.getIcon().split(":")[0]), 1,
										Short.parseShort(warp.getIcon().split(":")[1])),
								ChatColor.RESET + warp.getName(),
								new StringBuilder(ChatColor.DARK_PURPLE.toString()).append(loc.getBlockX()).append(", ")
										.append(loc.getBlockY()).append(", ").append(loc.getBlockZ()).toString(),
								ChatColor.LIGHT_PURPLE + loc.getWorld().getName()), "warp", "true"));
			}
			((Player) sender).openInventory(inv);
		}
		return true;
	}

	private void startTeleport(Player sender, WarpPoint target) {
		if (target == null || sender == null)
			return;

		if (Main.config.getInt("command.warp.teleportDelay") == 0) {
			sender.teleport(target.getLocation());
			Main.getInstance().sendMessage(
					new StringBuilder(Main.colorConfirm).append("Du wurdest zum Warp-Punkt ").append(Main.colorValue)
							.append(target.getName()).append(Main.colorConfirm).append(" teleportiert.").toString(),
					sender);
		} else {
			teleportingPlayers.add(sender);
			Location startPosition = sender.getLocation();
			long startTime = System.currentTimeMillis();
			boolean disallowMove = Main.config.getBoolean("command.warp.disallowMove");
			int teleportDelay = Main.config.getInt("command.warp.teleportDelay");

			Main.getInstance().sendMessage(new StringBuilder(Main.colorConfirm).append("Der Teleport zum Warp-Punkt ")
					.append(Main.colorValue).append(target.getName()).append(Main.colorConfirm).append(" startet in ")
					.append(Main.colorValue).append(teleportDelay).append(Main.colorConfirm)
					.append(" Sekunden. Bewege dich nicht!").toString(), sender);

			BukkitRunnable teleporter = new BukkitRunnable() {
				@Override
				public void run() {
					if (disallowMove) {
						Location currentPosition = sender.getLocation();
						if (!(startPosition.getWorld().equals(currentPosition.getWorld())
								&& startPosition.getBlock().equals(currentPosition.getBlock()))) {
							Main.getInstance().sendMessage(
									Main.colorFail + "Teleportvorgang abgebrochen! Du hast dich bewegt.", sender);
							teleportingPlayers.remove(sender);
							this.cancel();
						}
					}
					if (System.currentTimeMillis() >= startTime + teleportDelay * 1000) {
						sender.teleport(target.getLocation());
						Main.getInstance()
								.sendMessage(new StringBuilder(Main.colorConfirm).append("Du wurdest zum Warp-Punkt ")
										.append(Main.colorValue).append(target.getName()).append(Main.colorConfirm)
										.append(" teleportiert.").toString(), sender);
						teleportingPlayers.remove(sender);
						this.cancel();
					}
				}
			};
			teleporter.runTaskTimer(Main.getInstance(), 0, 20);
		}
	}

	private WarpPoint getWarp(String name) {
		for (WarpPoint warp : enabledWarps) {
			if (warp.getName().equalsIgnoreCase(name))
				return warp;
		}
		return SPAWN;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void handle(InventoryClickEvent e) {
		if ((e instanceof InventoryCreativeEvent) || (e instanceof CraftItemEvent))
			return;
		if (e.getClickedInventory() == null || !e.getClickedInventory().getName().equals("Warps"))
			return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;

		if (NumbzUtils.getTagValue(e.getCurrentItem(), "warp").equals("true")) {
			e.setCancelled(true);
			startTeleport((Player) e.getWhoClicked(),
					getWarp(e.getCurrentItem().getItemMeta().getDisplayName().substring(2)));
			e.getWhoClicked().closeInventory();
		}
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			Set<String> result = new HashSet<>();
			for (WarpPoint w : Main.updateWarps())
				if (w.getEnabled())
					result.add(w.getName());
			return EssentialCollections.toSortedStringList(enabledWarps, args[0]);
		}
		return Collections.emptyList();
	}
}
