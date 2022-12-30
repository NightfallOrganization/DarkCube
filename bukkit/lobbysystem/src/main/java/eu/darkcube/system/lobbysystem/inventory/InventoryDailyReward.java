/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.AsyncPagedInventory;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InventoryDailyReward extends LobbyAsyncPagedInventory {
	public static final Key reward = new Key(Lobby.getInstance(), "reward");
	public static final InventoryType type_daily_reward = InventoryType.of("daily_reward");

	private boolean displayedRewards = false;

	public InventoryDailyReward(User user) {
		super(InventoryDailyReward.type_daily_reward,
				Message.INVENTORY_NAME_DAILY_REWARD.getMessage(user), 5 * 9,
				AsyncPagedInventory.box(1, 1, 5, 9), user);

		this.SORT[IInventory.slot(5, 2)] = this.SORT[IInventory.slot(5, 1)] + 1;
		this.SORT[IInventory.slot(5, 3)] = this.SORT[IInventory.slot(5, 2)] + 1;
		this.SORT[IInventory.slot(5, 4)] = this.SORT[IInventory.slot(5, 3)] + 1;
		this.SORT[IInventory.slot(5, 5)] = this.SORT[IInventory.slot(5, 4)] + 1;
		this.SORT[IInventory.slot(5, 6)] = this.SORT[IInventory.slot(5, 5)] - 1;
		this.SORT[IInventory.slot(5, 7)] = this.SORT[IInventory.slot(5, 6)] - 1;
		this.SORT[IInventory.slot(5, 8)] = this.SORT[IInventory.slot(5, 7)] - 1;

		this.SORT[IInventory.slot(3, 4)] = this.SORT[IInventory.slot(5, 5)] + 2;
		this.SORT[IInventory.slot(3, 5)] = this.SORT[IInventory.slot(5, 5)] + 2;
		this.SORT[IInventory.slot(3, 6)] = this.SORT[IInventory.slot(5, 5)] + 2;
		updateSorts();
		// super(Bukkit.createInventory(null, 5 * 9,
		// Message.INVENTORY_NAME_DAILY_REWARD.getMessage(user)),
		// InventoryType.DAILY_REWARD);
	}

	@Override
	protected void postTick(boolean changedInformations) {
		if (!displayedRewards) {
			displayedRewards = currentSort.get() >= this.SORT[IInventory.slot(3, 5)];
		}
		if (changedInformations) {
			if (this.displayedRewards) {
				this.playSound();
			}
		}
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(this.user.getLastDailyReward());
		Calendar c2 = Calendar.getInstance();
		if (c.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)
				|| c.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
			this.user.setRewardSlotsUsed(new HashSet<>());
		}

		ItemStack used = ItemBuilder.item(Material.SULPHUR)
				.displayname(Message.REWARD_ALREADY_USED.getMessage(this.user.getUser())).build();
		ItemStack unused = ItemBuilder.item(Material.GLOWSTONE_DUST).displayname("§e???").build();
		Set<Integer> usedSlots = this.user.getRewardSlotsUsed();
		if (usedSlots.contains(1)) {
			items.put(21, reward(used, 1));
		} else {
			items.put(21, reward(unused, 1));
		}
		if (usedSlots.contains(2)) {
			items.put(22, reward(used, 2));
		} else {
			items.put(22, reward(unused, 2));
		}
		if (usedSlots.contains(3)) {
			items.put(23, reward(used, 3));
		} else {
			items.put(23, reward(unused, 3));
		}
	}

	@Override
	protected void insertFallbackItems() {
		ItemStack l =
				ItemBuilder.item(Material.STAINED_GLASS_PANE).displayname("§6").damage(7).build();
		this.fallbackItems.put(IInventory.slot(1, 1), l);
		this.fallbackItems.put(IInventory.slot(1, 2), l);
		this.fallbackItems.put(IInventory.slot(1, 3), l);
		this.fallbackItems.put(IInventory.slot(1, 4), l);
		this.fallbackItems.put(IInventory.slot(1, 5), l);
		this.fallbackItems.put(IInventory.slot(1, 6), l);
		this.fallbackItems.put(IInventory.slot(1, 7), l);
		this.fallbackItems.put(IInventory.slot(1, 8), l);
		this.fallbackItems.put(IInventory.slot(1, 9), l);

		this.fallbackItems.put(IInventory.slot(2, 1), l);
		this.fallbackItems.put(IInventory.slot(2, 9), l);
		this.fallbackItems.put(IInventory.slot(3, 1), l);
		this.fallbackItems.put(IInventory.slot(3, 9), l);
		this.fallbackItems.put(IInventory.slot(4, 1), l);
		this.fallbackItems.put(IInventory.slot(4, 9), l);

		this.fallbackItems.put(IInventory.slot(5, 1), l);
		this.fallbackItems.put(IInventory.slot(5, 2), l);
		this.fallbackItems.put(IInventory.slot(5, 3), l);
		this.fallbackItems.put(IInventory.slot(5, 4), l);
		this.fallbackItems.put(IInventory.slot(5, 5), l);
		this.fallbackItems.put(IInventory.slot(5, 6), l);
		this.fallbackItems.put(IInventory.slot(5, 7), l);
		this.fallbackItems.put(IInventory.slot(5, 8), l);
		this.fallbackItems.put(IInventory.slot(5, 9), l);

	}

	@Override
	protected void playSound0() {
		this.opened.stream().filter(p -> p instanceof Player).map(p -> (Player) p).forEach(p -> {
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
		});
	}

	private ItemStack reward(ItemStack old, int reward) {
		return ItemBuilder.item(old).persistentDataStorage()
				.iset(InventoryDailyReward.reward, PersistentDataTypes.INTEGER, reward).builder()
				.build();
	}

	// private void animate(User user, boolean instant) {
	// Calendar c = Calendar.getInstance();
	// c.setTimeInMillis(user.getLastDailyReward());
	// Calendar c2 = Calendar.getInstance();
	//// c2.setTimeInMillis(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
	// if (c.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
	// user.getRewardSlotsUsed().clear();
	// }
	//
	// new BukkitRunnable() {
	//
	// private ItemStack i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);
	//
	// private ItemStack i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);
	//
	// private int count = 0;
	//
	// @Override
	// public void run() {
	// if (this.count >= 20) {
	// this.cancel();
	// return;
	// }
	// switch (this.count) {
	// case 0:
	// InventoryDailyReward.this.handle.setItem(4, this.i1);
	// InventoryDailyReward.this.handle.setItem(3, this.i2);
	// InventoryDailyReward.this.handle.setItem(5, this.i2);
	// break;
	// case 1:
	// InventoryDailyReward.this.handle.setItem(2, this.i1);
	// InventoryDailyReward.this.handle.setItem(6, this.i1);
	// break;
	// case 2:
	// InventoryDailyReward.this.handle.setItem(1, this.i2);
	// InventoryDailyReward.this.handle.setItem(7, this.i2);
	// break;
	// case 3:
	// InventoryDailyReward.this.handle.setItem(0, this.i1);
	// InventoryDailyReward.this.handle.setItem(8, this.i1);
	// break;
	// case 4:
	// InventoryDailyReward.this.handle.setItem(9, this.i2);
	// InventoryDailyReward.this.handle.setItem(17, this.i2);
	// break;
	// case 5:
	// InventoryDailyReward.this.handle.setItem(18, this.i1);
	// InventoryDailyReward.this.handle.setItem(26, this.i1);
	// break;
	// case 6:
	// InventoryDailyReward.this.handle.setItem(27, this.i2);
	// InventoryDailyReward.this.handle.setItem(35, this.i2);
	// break;
	// case 7:
	// InventoryDailyReward.this.handle.setItem(36, this.i1);
	// InventoryDailyReward.this.handle.setItem(44, this.i1);
	// break;
	// case 8:
	// InventoryDailyReward.this.handle.setItem(37, this.i2);
	// InventoryDailyReward.this.handle.setItem(43, this.i2);
	// break;
	// case 9:
	// InventoryDailyReward.this.handle.setItem(38, this.i1);
	// InventoryDailyReward.this.handle.setItem(42, this.i1);
	// break;
	// case 10:
	// InventoryDailyReward.this.handle.setItem(39, this.i2);
	// InventoryDailyReward.this.handle.setItem(41, this.i2);
	// break;
	// case 11:
	// InventoryDailyReward.this.handle.setItem(40, this.i1);
	// break;
	// case 15:
	// user.playSound(Sound.LEVEL_UP, 1, 1);
	// InventoryDailyReward.this.setItems(user);
	// break;
	// default:
	// break;
	// }
	//
	// this.count++;
	// if (instant)
	// this.run();
	// }
	//
	// }.runTaskTimer(Lobby.getInstance(), 1, 1);
	// }

}
