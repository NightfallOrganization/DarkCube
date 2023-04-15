/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.listener;

import eu.darkcube.system.vanillaaddons.event.ArmorEquipEvent;
import eu.darkcube.system.vanillaaddons.event.ArmorEquipEvent.ArmorType;
import eu.darkcube.system.vanillaaddons.event.ArmorEquipEvent.EquipMethod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class ArmorListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public final void inventoryClick(InventoryClickEvent e) {
		boolean shift = false;// 45
		boolean numberkey = false;
		// 46
		if (e.getAction() != InventoryAction.NOTHING) {// 48
			if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick()
					.equals(ClickType.SHIFT_RIGHT)) {// 50
				shift = true;// 51
			}

			if (e.getClick().equals(ClickType.NUMBER_KEY)) {// 53
				numberkey = true;// 54
			}

			if (e.getSlotType() == SlotType.ARMOR || e.getSlotType() == SlotType.QUICKBAR
					|| e.getSlotType() == SlotType.CONTAINER) {// 56 57
				if (e.getClickedInventory() == null || e.getClickedInventory().getType()
						.equals(InventoryType.PLAYER)) {// 59
					if (e.getInventory().getType().equals(InventoryType.CRAFTING)
							|| e.getInventory().getType().equals(InventoryType.PLAYER)) {// 61 62
						if (e.getWhoClicked() instanceof Player) {// 64
							ArmorType newArmorType = ArmorType.matchType(
									shift ? e.getCurrentItem() : e.getCursor());// 66
							if (shift || newArmorType == null
									|| e.getRawSlot() == newArmorType.getSlot()) {// 67
								if (shift) {// 72
									newArmorType = ArmorType.matchType(e.getCurrentItem());
									// 73
									if (newArmorType != null) {// 74
										boolean equipping =
												e.getRawSlot() != newArmorType.getSlot();// 75

										label165:
										{
											if (newArmorType.equals(ArmorType.HELMET)) {// 79
												if (equipping) {// 80
													if (isAirOrNull(e.getWhoClicked().getInventory()
															.getHelmet())) {
														break label165;
													}
												} else if (!isAirOrNull(
														e.getWhoClicked().getInventory()
																.getHelmet())) {// 81
													break label165;
												}
											}

											if (newArmorType.equals(ArmorType.CHESTPLATE)) {// 82
												if (equipping) {// 83
													if (isAirOrNull(e.getWhoClicked().getInventory()
															.getChestplate())) {
														break label165;
													}
												} else if (!isAirOrNull(
														e.getWhoClicked().getInventory()
																.getChestplate())) {// 84
													break label165;
												}
											}

											if (newArmorType.equals(ArmorType.LEGGINGS)) {// 85
												if (equipping) {// 86
													if (isAirOrNull(e.getWhoClicked().getInventory()
															.getLeggings())) {
														break label165;
													}
												} else if (!isAirOrNull(
														e.getWhoClicked().getInventory()
																.getLeggings())) {// 87
													break label165;
												}
											}

											if (!newArmorType.equals(ArmorType.BOOTS)) {// 88
												return;// 144
											}

											if (equipping) {// 89
												if (!isAirOrNull(e.getWhoClicked().getInventory()
														.getBoots())) {
													return;
												}
											} else if (isAirOrNull(e.getWhoClicked().getInventory()
													.getBoots())) {// 90
												return;
											}
										}

										ArmorEquipEvent armorEquipEvent =
												new ArmorEquipEvent((Player) e.getWhoClicked(),
														EquipMethod.SHIFT_CLICK, newArmorType,
														equipping ? null : e.getCurrentItem(),
														equipping
																? e.getCurrentItem()
																: null);// 91 92 93
										Bukkit.getServer().getPluginManager()
												.callEvent(armorEquipEvent);// 94
										if (armorEquipEvent.isCancelled()) {// 95
											e.setCancelled(true);// 96
										}
									}
								} else {
									ItemStack newArmorPiece = e.getCursor();// 101
									ItemStack oldArmorPiece = e.getCurrentItem();// 102
									if (numberkey) {// 103
										if (e.getClickedInventory().getType()
												.equals(InventoryType.PLAYER)) {// 104
											ItemStack hotbarItem = e.getClickedInventory()
													.getItem(e.getHotbarButton());// 112
											if (!isAirOrNull(hotbarItem)) {// 113
												newArmorType =
														ArmorType.matchType(hotbarItem);// 114
												newArmorPiece = hotbarItem;// 115
												oldArmorPiece = e.getClickedInventory()
														.getItem(e.getSlot());// 116
											} else {
												newArmorType = ArmorType.matchType(
														!isAirOrNull(e.getCurrentItem())
																? e.getCurrentItem()
																: e.getCursor());// 118 119
											}
										}
									} else if (isAirOrNull(e.getCursor()) && !isAirOrNull(
											e.getCurrentItem())) {// 123
										newArmorType =
												ArmorType.matchType(e.getCurrentItem());// 125
									}

									if (newArmorType != null
											&& e.getRawSlot() == newArmorType.getSlot()) {//
										// 132
										EquipMethod method = EquipMethod.PICK_DROP;// 133
										if (e.getAction().equals(InventoryAction.HOTBAR_SWAP)
												|| numberkey) {// 134
											method = EquipMethod.HOTBAR_SWAP;// 135
										}

										ArmorEquipEvent armorEquipEvent =
												new ArmorEquipEvent((Player) e.getWhoClicked(),
														method, newArmorType, oldArmorPiece,
														newArmorPiece);// 136
										Bukkit.getServer().getPluginManager()
												.callEvent(armorEquipEvent);// 138
										if (armorEquipEvent.isCancelled()) {// 139
											e.setCancelled(true);// 140
										}
									}
								}

							}
						}
					}
				}
			}
		}
	}// 47 49 58 60 63 65 70

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent e) {
		if (!e.useItemInHand().equals(Result.DENY)) {// 148
			if (e.getAction() != Action.PHYSICAL) {// 151
				if (e.getAction() == Action.RIGHT_CLICK_AIR
						|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {// 153
					Player player = e.getPlayer();// 154

					ArmorType newArmorType = ArmorType.matchType(e.getItem());// 178
					if (newArmorType != null && (
							newArmorType.equals(ArmorType.HELMET) && isAirOrNull(
									e.getPlayer().getInventory().getHelmet())
									|| newArmorType.equals(ArmorType.CHESTPLATE) && isAirOrNull(
									e.getPlayer().getInventory().getChestplate())
									|| newArmorType.equals(ArmorType.LEGGINGS) && isAirOrNull(
									e.getPlayer().getInventory().getLeggings())
									|| newArmorType.equals(ArmorType.BOOTS) && isAirOrNull(
									e.getPlayer().getInventory()
											.getBoots()))) {// 179 180 181 182 183 184 185 186
						ArmorEquipEvent armorEquipEvent =
								new ArmorEquipEvent(e.getPlayer(), EquipMethod.HOTBAR,
										ArmorType.matchType(e.getItem()), (ItemStack) null,
										e.getItem());// 187 188
						Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);// 189
						if (armorEquipEvent.isCancelled()) {// 190
							e.setCancelled(true);// 191
							player.updateInventory();// 192
						}
					}
				}

			}
		}
	}// 149 152 197

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventoryDrag(InventoryDragEvent event) {
		ArmorType type = ArmorType.matchType(event.getOldCursor());// 205
		if (!event.getRawSlots().isEmpty()) {// 206
			if (type != null && type.getSlot() == event.getRawSlots().stream().findFirst()
					.orElse(0)) {// 208
				ArmorEquipEvent armorEquipEvent =
						new ArmorEquipEvent((Player) event.getWhoClicked(), EquipMethod.DRAG, type,
								null, event.getOldCursor());// 209 210
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);// 211
				if (armorEquipEvent.isCancelled()) {// 212
					event.setResult(Result.DENY);// 213
					event.setCancelled(true);// 214
				}
			}

		}
	}// 207 226

	@EventHandler
	public void itemBreakEvent(PlayerItemBreakEvent e) {
		ArmorType type = ArmorType.matchType(e.getBrokenItem());// 230
		if (type != null) {// 231
			Player p = e.getPlayer();// 232
			ArmorEquipEvent armorEquipEvent =
					new ArmorEquipEvent(p, EquipMethod.BROKE, type, e.getBrokenItem(),
							(ItemStack) null);// 233
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);// 234
			if (armorEquipEvent.isCancelled()) {// 235
				ItemStack i = e.getBrokenItem().clone();// 236
				i.setAmount(1);// 237
				Damageable dam = (Damageable) i.getItemMeta();// 238
				dam.setDamage(dam.getDamage() + 1);// 240
				i.setItemMeta(dam);// 241
				if (type.equals(ArmorType.HELMET)) {// 242
					p.getInventory().setHelmet(i);// 243
				} else if (type.equals(ArmorType.CHESTPLATE)) {// 244
					p.getInventory().setChestplate(i);// 245
				} else if (type.equals(ArmorType.LEGGINGS)) {// 246
					p.getInventory().setLeggings(i);// 247
				} else if (type.equals(ArmorType.BOOTS)) {// 248
					p.getInventory().setBoots(i);// 249
				}
			}
		}

	}// 253

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent e) {
		Player p = e.getEntity();// 257
		if (!e.getKeepInventory()) {// 258
			ItemStack[] var6;
			int var5 = (var6 = p.getInventory().getArmorContents()).length;

			for (int var4 = 0; var4 < var5; ++var4) {// 260
				ItemStack i = var6[var4];
				if (!isAirOrNull(i)) {// 261
					Bukkit.getServer().getPluginManager().callEvent(
							new ArmorEquipEvent(p, EquipMethod.DEATH, ArmorType.matchType(i), i,
									(ItemStack) null));// 262 263 264
				}
			}

		}
	}// 259 268

	public static boolean isAirOrNull(ItemStack item) {
		return item == null || item.getType().equals(Material.AIR);// 274
	}
}
