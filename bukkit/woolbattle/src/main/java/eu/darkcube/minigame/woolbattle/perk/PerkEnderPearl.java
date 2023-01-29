/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;

public class PerkEnderPearl implements Perk {

	public static int COOLDOWN = 5;

	public static int COST = 8;

	private final User owner;

	private final Player player;

	private final ObservableInteger slot;

	private final ObservableInteger cooldown;

	private final PerkNumber perkNumber;

	public PerkEnderPearl(User owner) {
		this.perkNumber = PerkNumber.ENDER_PEARL;
		this.owner = owner;
		this.player = Bukkit.getPlayer(owner.getUniqueId());
		this.cooldown = new SimpleObservableInteger(0) {

			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				PerkEnderPearl.this.setItem();
			}

			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {

			}

		};
		this.slot = new SimpleObservableInteger() {

			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				if (oldValue != null) {
					if (oldValue == 100)
						PerkEnderPearl.this.player.getOpenInventory().setCursor(null);
					else
						PerkEnderPearl.this.player.getOpenInventory().setItem(oldValue, null);
				}
				PerkEnderPearl.this.perkNumber.setRawSlot(owner, newValue);
				PerkEnderPearl.this.setItem();
			}

			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				PerkEnderPearl.this.perkNumber.setRawSlot(owner, newValue);
			}

		};
		this.setSlot(this.perkNumber.getRawSlot(owner));
	}

	@Override
	public int getMaxCooldown() {
		return PerkEnderPearl.COOLDOWN;
	}

	@Override
	public int getCost() {
		return PerkEnderPearl.COST;
	}

	@Override
	public void setCost(int cost) {
		PerkEnderPearl.COST = cost;
	}

	@Override
	public boolean hasCooldown() {
		return true;
	}

	@Override
	public Item getCooldownItem() {
		return Item.DEFAULT_PEARL_COOLDOWN;
	}

	@Override
	public Item getItem() {
		return Item.DEFAULT_PEARL;
	}

	@Override
	public ItemStack calculateItem() {
		if (this.getCooldown() == 0) {
			return this.getItem().getItem(this.owner);
		}
		ItemStack item = this.getCooldownItem().getItem(this.owner);
		item.setAmount(this.getCooldown());
		return item;
	}

	@Override
	public PerkName getPerkName() {
		return new PerkName("ENDER_PEARL");
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

	@Override
	public boolean isHardCooldown() {
		return true;
	}

	@Override
	public void setHardCooldown(boolean hardCooldown) {
	}

}
