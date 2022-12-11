/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.ListenerGrabber;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;

public class PerkTypePerk implements Perk {

	private final PerkType type;

	private final User owner;

	private final Player player;

	private final ObservableInteger slot;

	private final ObservableInteger cooldown;

	private final PerkNumber perkNumber;

	private boolean hardCooldown = true;

	public PerkTypePerk(PerkType type, User owner, PerkNumber perkNumber) {
		this.perkNumber = perkNumber;
		this.owner = owner;
		this.type = type;
		this.player = Bukkit.getPlayer(owner.getUniqueId());
		this.cooldown = new SimpleObservableInteger(0) {

			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue,
					Integer newValue) {
				if (newValue == 0)
					PerkTypePerk.this.setHardCooldown(false);
				if (!PerkTypePerk.this.isHardCooldown()
						&& newValue == PerkTypePerk.this.getMaxCooldown())
					PerkTypePerk.this.setHardCooldown(true);
				PerkTypePerk.this.setItem();
			}

			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue,
					Integer newValue) {}

		};
		this.slot = new SimpleObservableInteger() {

			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue,
					Integer newValue) {
				if (oldValue != null) {
					if (oldValue == 100)
						PerkTypePerk.this.player.getOpenInventory().setCursor(null);
					else
						PerkTypePerk.this.player.getOpenInventory().setItem(oldValue, null);
				}
				perkNumber.setRawSlot(owner, newValue);
				PerkTypePerk.this.setItem();
			}

			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue,
					Integer newValue) {
				perkNumber.setRawSlot(owner, newValue);
			}

		};
		if (perkNumber != PerkNumber.DISPLAY)
			this.setSlot(perkNumber.getRawSlot(owner));
		if (type == PerkType.LINE_BUILDER)
			this.hardCooldown = false;
	}

	@Override
	public int getCost() {
		return this.type.getCost();
	}

	@Override
	public void setCost(int cost) {
		this.type.setCost(cost);
	}

	@Override
	public boolean isHardCooldown() {
		return this.hardCooldown;
	}

	@Override
	public void setHardCooldown(boolean hardCooldown) {
		this.hardCooldown = hardCooldown;
	}

	@Override
	public int getMaxCooldown() {
		return this.type.getCooldown();
	}

	@Override
	public boolean hasCooldown() {
		return this.type.hasCooldown();
	}

	@Override
	public Item getCooldownItem() {
		return this.type.getCooldownItem();
	}

	@Override
	public Item getItem() {
		return this.type.getItem();
	}

	@Override
	public ItemStack calculateItem() {
		if (this.getCooldown() == 0 || ((this.type == PerkType.LINE_BUILDER)
				&& this.getCooldown() != this.getMaxCooldown() && !this.isHardCooldown())) {
			ItemStack item = this.getItem().getItem(this.owner);
			if (this.type == PerkType.GRABBER && ListenerGrabber.hasTarget(owner)) {
				item = Item.PERK_GRABBER_GRABBED.getItem(this.owner);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ListenerGrabber.getTarget(owner).getTeamPlayerName());
				item.setItemMeta(meta);
			} else if (this.type == PerkType.ARROW_RAIN) {
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.ARROW_INFINITE, 100, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			} else if (this.type == PerkType.TNT_ARROW) {
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.ARROW_INFINITE, 100, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			return item;
		}
		ItemStack item = this.getCooldownItem().getItem(this.owner);
		item.setAmount(this.getCooldown());
		return item;
	}

	@Override
	public PerkName getPerkName() {
		return this.type.getPerkName();
	}

	@Override
	public String getDisplayName() {
		return this.getItem().getDisplayName(this.owner);
	}

	@Override
	public ObservableInteger getSlotLink() {
		return this.slot;
	}

	@Override
	public void setSlot(int slot) {
		this.slot.setObject(slot);
	}

	@Override
	public int getSlot() {
		return this.slot.getObject();
	}

	@Override
	public User getOwner() {
		return this.owner;
	}

	@Override
	public ObservableInteger getCooldownLink() {
		return this.cooldown;
	}

	@Override
	public void setCooldown(int cooldown) {
		this.cooldown.setObject(cooldown);
	}

	@Override
	public int getCooldown() {
		return this.cooldown.getObject();
	}

	@Override
	public PerkNumber getPerkNumber() {
		return this.perkNumber;
	}

	@Override
	public void setSlotSilent(int slot) {
		this.slot.setSilent(slot);
	}

}
