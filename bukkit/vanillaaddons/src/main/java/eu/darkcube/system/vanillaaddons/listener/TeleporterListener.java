/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.inventory.TeleporterInventory;
import eu.darkcube.system.vanillaaddons.inventory.TeleportersInventory;
import eu.darkcube.system.vanillaaddons.util.BlockLocation;
import eu.darkcube.system.vanillaaddons.util.Item;
import eu.darkcube.system.vanillaaddons.util.Item.Data;
import eu.darkcube.system.vanillaaddons.util.Teleporter;
import eu.darkcube.system.vanillaaddons.util.Teleporter.TeleportAccess;
import eu.darkcube.system.vanillaaddons.util.Teleporters;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class TeleporterListener implements Listener {
	private final VanillaAddons addons;

	public TeleporterListener(VanillaAddons addons) {
		this.addons = addons;
	}

	public static boolean remove(VanillaAddons addons, Block block) {
		BlockLocation loc = new BlockLocation(block.getWorld().getKey(), block.getX(), block.getY(),
				block.getZ());
		boolean changed = false;
		for (Teleporter teleporter : new ArrayList<>(addons.teleporters(block.getWorld()))) {
			if (!teleporter.block().equals(loc)) {
				continue;
			}
			addons.teleporters(block.getWorld()).remove(teleporter);
			Location drop = block.getLocation().add(0.5, 0.5, 0.5);
			block.getWorld().dropItemNaturally(drop, Item.TELEPORTER.item());
			block.setType(Material.AIR);
			changed = true;
		}
		return changed;
	}

	public static void loadTeleporters(VanillaAddons addons, World world) {
		PersistentDataContainer container = world.getPersistentDataContainer();
		if (container.has(Teleporter.teleporters)) {
			addons.teleporters()
					.put(world, container.get(Teleporter.teleporters, Teleporter.TELEPORTERS));
			for (Teleporter teleporter : addons.teleporters().get(world).teleporters) {
				teleporter.spawn();
			}
		}
	}

	public static void saveTeleporters(VanillaAddons addons, World world) {
		PersistentDataContainer container = world.getPersistentDataContainer();
		Teleporters teleporters = addons.teleporters().get(world);
		if (teleporters != null) {
			container.set(Teleporter.teleporters, Teleporter.TELEPORTERS, teleporters);
		}
	}

	@EventHandler
	public void handle(PlayerToggleSneakEvent event) {
		try {
			if (event.isSneaking()) {
				for (Teleporter teleporter : addons.teleporters(event.getPlayer().getWorld())) {
					if (this.getPlayers(teleporter.block()).contains(event.getPlayer())) {
						AUser user = AUser.user(UserAPI.getInstance().getUser(event.getPlayer()));
						user.openInventory(TeleportersInventory.TYPE, teleporter);
						break;
					}
				}
			}
		} catch (Throwable e) {
			Logger.getLogger("tset").severe("sads");
		}
	}

	@EventHandler
	public void handle(PlayerInteractEvent event) {
		if (!event.hasBlock())
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			//noinspection DataFlowIssue
			BlockLocation o =
					new BlockLocation(block.getWorld().getKey(), block.getX(), block.getY(),
							block.getZ());
			Teleporter teleporter =
					addons.teleporters(block.getWorld()).stream().filter(t -> t.block().equals(o))
							.findFirst().orElse(null);
			if (teleporter == null)
				return;
			AUser.user(UserAPI.getInstance().getUser(event.getPlayer()))
					.openInventory(TeleporterInventory.TYPE, teleporter);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(InventoryClickEvent event) {
		AUser user = AUser.user(UserAPI.getInstance().getUser(event.getWhoClicked().getUniqueId()));
		if (TeleportersInventory.TYPE.equals(user.openInventory())) {
			if (event.getCurrentItem() == null)
				return;
			if (!event.getCurrentItem().hasItemMeta())
				return;
			if (event.getClickedInventory() == null)
				return;
			ItemBuilder item = ItemBuilder.item(event.getCurrentItem());
			event.setCancelled(true);
			ItemMeta meta = event.getCurrentItem().getItemMeta();
			if (meta.getPersistentDataContainer()
					.has(new NamespacedKey("vanillaaddons", "teleporter"))) {
				Teleporter teleporter = meta.getPersistentDataContainer()
						.get(new NamespacedKey("vanillaaddons", "teleporter"),
								Teleporter.TELEPORTER);
				//noinspection DataFlowIssue
				Bukkit.getPlayer(user.user().getUniqueId()).closeInventory(Reason.TELEPORT);
				//noinspection DataFlowIssue
				Bukkit.getPlayer(user.user().getUniqueId()).teleportAsync(
						teleporter.block().block().getLocation().add(0.5, 1, 0.5)
								.setDirection(user.user().asPlayer().getLocation().getDirection()));
			}
		}
	}

	@EventHandler
	public void handle(BlockDestroyEvent event) {
		if (event.isCancelled())
			return;
		Block block = event.getBlock();
		if (remove(addons, block)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Block block = event.getBlock();
		if (remove(addons, block)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(BlockPlaceEvent event) {
		if (!event.canBuild())
			return;
		if (event.isCancelled())
			return;
		Player p = event.getPlayer();
		for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HAND,
				EquipmentSlot.OFF_HAND}) {
			ItemStack item = p.getInventory().getItem(slot);
			if (!item.hasItemMeta())
				continue;
			ItemBuilder builder = ItemBuilder.item(item);
			if (!builder.persistentDataStorage().has(Data.TYPE_KEY))
				continue;
			if (builder.persistentDataStorage().get(Data.TYPE_KEY, Data.TYPE) != Item.TELEPORTER)
				continue;
			Block block = event.getBlockPlaced();
			Teleporter teleporter = new Teleporter(Material.ENDER_EYE, "Teleporter",
					new BlockLocation(block.getWorld().getKey(), block.getX(), block.getY(),
							block.getZ()), TeleportAccess.PUBLIC, p.getUniqueId());
			addons.teleporters(block.getWorld()).add(teleporter);
			new BukkitRunnable() {
				@Override
				public void run() {
					teleporter.spawn();
				}
			}.runTask(addons);
			event.setCancelled(true);
			p.getInventory().setItem(slot, item.subtract());
			return;
		}
	}

	private Collection<Player> getPlayers(BlockLocation block) {
		return block.block().getLocation().add(0.5, 1.0, 0.5).getNearbyPlayers(1.2, 0.2, (p) -> {
			Material mat = p.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType();
			return mat == Material.RESPAWN_ANCHOR || mat == Material.OBSIDIAN
					|| mat == Material.CRYING_OBSIDIAN;
		});
	}

	@EventHandler
	public void handle(WorldLoadEvent event) {
		loadTeleporters(addons, event.getWorld());
	}

	@EventHandler
	public void handle(WorldUnloadEvent event) {
		saveTeleporters(addons, event.getWorld());
	}

	@EventHandler
	public void handle(WorldSaveEvent event) {
		saveTeleporters(addons, event.getWorld());
	}
}
