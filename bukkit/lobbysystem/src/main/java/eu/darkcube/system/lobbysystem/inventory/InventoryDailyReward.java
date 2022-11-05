package eu.darkcube.system.lobbysystem.inventory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;

public class InventoryDailyReward extends Inventory {

	public InventoryDailyReward(User user) {
		super(Bukkit.createInventory(null, 5 * 9, Message.INVENTORY_NAME_DAILY_REWARD.getMessage(user)),
				InventoryType.DAILY_REWARD);
	}

	@Override
	public void playAnimation(User user) {
		this.animate(user, false);
	}

	@Override
	public void skipAnimation(User user) {
		this.animate(user, true);
	}

	public void setItems(User user) {
		ItemStack used = new ItemBuilder(Material.SULPHUR).displayname(Message.REWARD_ALREADY_USED.getMessage(user))
				.build();
		ItemStack unused = new ItemBuilder(Material.GLOWSTONE_DUST).displayname("§e???").build();
		Set<Integer> usedSlots = user.getRewardSlotsUsed();
		Map<Integer, ItemStack> items = new HashMap<>();
		if (usedSlots.contains(1)) {
			used = new ItemBuilder(used).getUnsafe().setInt("reward", 1).builder().build();
			items.put(21, used);
		} else {
			unused = new ItemBuilder(unused).getUnsafe().setInt("reward", 1).builder().build();
			items.put(21, unused);
		}
		if (usedSlots.contains(2)) {
			used = new ItemBuilder(used).getUnsafe().setInt("reward", 2).builder().build();
			items.put(22, used);
		} else {
			unused = new ItemBuilder(unused).getUnsafe().setInt("reward", 2).builder().build();
			items.put(22, unused);
		}
		if (usedSlots.contains(3)) {
			used = new ItemBuilder(used).getUnsafe().setInt("reward", 3).builder().build();
			items.put(23, used);
		} else {
			unused = new ItemBuilder(unused).getUnsafe().setInt("reward", 3).builder().build();
			items.put(23, unused);
		}
		for (int slot : items.keySet()) {
			this.handle.setItem(slot, items.get(slot));
		}
	}

	private void animate(User user, boolean instant) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(user.getLastDailyReward());
		Calendar c2 = Calendar.getInstance();
//		c2.setTimeInMillis(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
		if (c.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
			user.getRewardSlotsUsed().clear();
		}

		new BukkitRunnable() {

			private ItemStack i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);

			private ItemStack i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);

			private int count = 0;

			@Override
			public void run() {
				if (this.count >= 20) {
					this.cancel();
					return;
				}
				switch (this.count) {
				case 0:
					InventoryDailyReward.this.handle.setItem(4, this.i1);
					InventoryDailyReward.this.handle.setItem(3, this.i2);
					InventoryDailyReward.this.handle.setItem(5, this.i2);
					break;
				case 1:
					InventoryDailyReward.this.handle.setItem(2, this.i1);
					InventoryDailyReward.this.handle.setItem(6, this.i1);
					break;
				case 2:
					InventoryDailyReward.this.handle.setItem(1, this.i2);
					InventoryDailyReward.this.handle.setItem(7, this.i2);
					break;
				case 3:
					InventoryDailyReward.this.handle.setItem(0, this.i1);
					InventoryDailyReward.this.handle.setItem(8, this.i1);
					break;
				case 4:
					InventoryDailyReward.this.handle.setItem(9, this.i2);
					InventoryDailyReward.this.handle.setItem(17, this.i2);
					break;
				case 5:
					InventoryDailyReward.this.handle.setItem(18, this.i1);
					InventoryDailyReward.this.handle.setItem(26, this.i1);
					break;
				case 6:
					InventoryDailyReward.this.handle.setItem(27, this.i2);
					InventoryDailyReward.this.handle.setItem(35, this.i2);
					break;
				case 7:
					InventoryDailyReward.this.handle.setItem(36, this.i1);
					InventoryDailyReward.this.handle.setItem(44, this.i1);
					break;
				case 8:
					InventoryDailyReward.this.handle.setItem(37, this.i2);
					InventoryDailyReward.this.handle.setItem(43, this.i2);
					break;
				case 9:
					InventoryDailyReward.this.handle.setItem(38, this.i1);
					InventoryDailyReward.this.handle.setItem(42, this.i1);
					break;
				case 10:
					InventoryDailyReward.this.handle.setItem(39, this.i2);
					InventoryDailyReward.this.handle.setItem(41, this.i2);
					break;
				case 11:
					InventoryDailyReward.this.handle.setItem(40, this.i1);
					break;
				case 15:
					user.playSound(Sound.LEVEL_UP, 1, 1);
					InventoryDailyReward.this.setItems(user);
					break;
				default:
					break;
				}

				this.count++;
				if (instant)
					this.run();
			}

		}.runTaskTimer(Lobby.getInstance(), 1, 1);
	}

}
