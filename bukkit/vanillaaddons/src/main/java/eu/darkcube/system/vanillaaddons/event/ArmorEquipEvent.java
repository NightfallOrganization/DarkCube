/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.event;

import eu.darkcube.system.vanillaaddons.listener.ArmorListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private final EquipMethod equipType;
	private final ArmorType type;
	private ItemStack oldArmorPiece;
	private ItemStack newArmorPiece;

	public ArmorEquipEvent(Player player, EquipMethod equipType, ArmorType type,
			ItemStack oldArmorPiece, ItemStack newArmorPiece) {
		super(player);// 30
		this.equipType = equipType;// 31
		this.type = type;// 32
		this.oldArmorPiece = oldArmorPiece;// 33
		this.newArmorPiece = newArmorPiece;// 34
	}// 35

	public static HandlerList getHandlerList() {
		return handlers;// 43
	}

	public HandlerList getHandlers() {
		return handlers;// 53
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;// 63
	}// 64

	public boolean isCancelled() {
		return this.cancel;// 73
	}

	public ArmorType getType() {
		return this.type;// 77
	}

	public ItemStack getOldArmorPiece() {
		return this.oldArmorPiece;// 84
	}

	public void setOldArmorPiece(ItemStack oldArmorPiece) {
		this.oldArmorPiece = oldArmorPiece;// 88
	}// 89

	public ItemStack getNewArmorPiece() {
		return this.newArmorPiece;// 95
	}

	public void setNewArmorPiece(ItemStack newArmorPiece) {
		this.newArmorPiece = newArmorPiece;// 99
	}// 100

	public EquipMethod getMethod() {
		return this.equipType;// 106
	}

	public enum EquipMethod {
		SHIFT_CLICK, DRAG, PICK_DROP, HOTBAR, HOTBAR_SWAP, DISPENSER, BROKE, DEATH;

		EquipMethod() {
		}// 109
	}

	public enum ArmorType {
		HELMET(5), CHESTPLATE(6), LEGGINGS(7), BOOTS(8);

		private final int slot;

		ArmorType(int slot) {
			this.slot = slot;// 17
		}// 18

		public static ArmorType matchType(ItemStack itemStack) {
			if (ArmorListener.isAirOrNull(itemStack)) {// 27
				return null;
			} else {
				String type = itemStack.getType().name();// 28
				if (!type.endsWith("_HELMET") && !type.endsWith("_SKULL") && !type.endsWith(
						"_HEAD")) {// 29
					if (!type.endsWith("_CHESTPLATE") && !type.equals("ELYTRA")) {
						if (type.endsWith("_LEGGINGS")) {// 31
							return LEGGINGS;
						} else {
							return type.endsWith("_BOOTS") ? BOOTS : null;// 32 33
						}
					} else {
						return CHESTPLATE;// 30
					}
				} else {
					return HELMET;
				}
			}
		}

		public int getSlot() {
			return this.slot;// 37
		}
	}
}
