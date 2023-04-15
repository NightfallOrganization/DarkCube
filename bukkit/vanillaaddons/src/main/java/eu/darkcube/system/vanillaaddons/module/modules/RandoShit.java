/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules;

import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Iterator;
import java.util.List;

public class RandoShit implements Module, Listener {
	private final VanillaAddons addons;

	public RandoShit(VanillaAddons addons) {
		this.addons = addons;
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, addons);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onItemBreak(PlayerItemDamageEvent event) {
		ItemStack item = event.getItem();// 131
		Player player = event.getPlayer();// 132
		if (this.isValidMaterial(item.getType())) {// 133
			ItemMeta var5 = item.getItemMeta();// 135
			if (var5 instanceof Damageable) {
				Damageable damageable = (Damageable) var5;
				if (damageable.getDamage() + event.getDamage() >= item.getType()
						.getMaxDurability()) {// 136
					event.setCancelled(true);// 137
					damageable.setDamage(item.getType().getMaxDurability() - 1);// 138
					item.setItemMeta(damageable);// 139
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F,
							1.0F);// 140
				}
			}

		}
	}// 143

	@EventHandler
	private void onRightClickStairs(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {// 147
			Player player = event.getPlayer();// 148
			Block clickedBlock = event.getClickedBlock();// 149
			if (clickedBlock != null) {// 150
				if (clickedBlock.getRelative(BlockFace.UP).isPassable()) {// 151
					BlockData var5 = clickedBlock.getBlockData();// 153
					if (var5 instanceof Stairs) {
						Stairs stairs = (Stairs) var5;
						Location location = new Location(clickedBlock.getWorld(),
								clickedBlock.getLocation().getX() + 0.5,
								clickedBlock.getLocation().getY() - 1.2,
								clickedBlock.getLocation().getZ() + 0.5,
								this.getYaw(stairs.getFacing().getOppositeFace()), 0.0F);// 154
						ArmorStand armorStand = (ArmorStand) player.getWorld()
								.spawn(location, ArmorStand.class);// 156
						armorStand.setCustomName(
								"SIT_" + String.valueOf(player.getUniqueId()));// 157
						armorStand.setCustomNameVisible(false);// 158
						armorStand.setGravity(false);// 159
						armorStand.setVisible(false);// 160
						armorStand.addPassenger(player);// 161
					}

				}
			}
		}
	}// 163

	@EventHandler
	private void onDismount(EntityDismountEvent event) {
		Entity var4 = event.getEntity();// 167
		if (var4 instanceof Player player) {
			var4 = event.getDismounted();
			if (var4 instanceof ArmorStand stand) {
				if (stand.getCustomName() == null) {// 168
					return;
				}

				if (stand.getCustomName()
						.equals("SIT_" + String.valueOf(player.getUniqueId()))) {// 169
					stand.remove();
				}

				player.teleport(player.getLocation().add(0.0, 1.0, 0.0));// 170
			}
		}

	}// 172

	private float getYaw(BlockFace face) {
		switch (face) {// 175
			case NORTH:
				return 180.0F;// 177
			case WEST:
				return 90.0F;// 178
			case EAST:
				return -90.0F;// 179
			default:
				return 0.0F;// 176
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onDamage(EntityDamageEvent event) {
		Entity var3 = event.getEntity();// 185
		if (var3 instanceof Player player) {
			if (this.hasBrokenPiece(player)) {// 186
				player.setHealth(Math.max(0.0, player.getHealth() - event.getDamage()));// 187
				event.setDamage(0.0);// 188
			}
		}

	}// 191

	private boolean hasBrokenPiece(Player player) {
		ItemStack[] var2 = player.getInventory().getArmorContents();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {// 194
			ItemStack stack = var2[var4];
			if (stack != null && this.isValidMaterial(stack.getType())) {// 195 196
				ItemMeta var7 = stack.getItemMeta();
				if (var7 instanceof Damageable) {// 197
					Damageable damageable = (Damageable) var7;
					if (damageable.getDamage() + 1 > stack.getType().getMaxDurability()) {
						return true;// 198
					}
				}
			}
		}

		return false;// 202
	}

	private boolean isValidMaterial(Material material) {
		List<String> mats = List.of("DIAMOND_", "NETHERITE_");// 206
		Iterator var3 = mats.iterator();

		String s;
		do {
			if (!var3.hasNext()) {
				return false;// 212
			}

			s = (String) var3.next();// 208
		} while (!material.name().startsWith(s));// 209

		return true;
	}
}
