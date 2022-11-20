package eu.darkcube.minigame.woolbattle.user;

import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemBuilder;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;

public class UserSettings {

	public static final String COLOR_SELECTION_ID = "COLOR_SELECTION";

	private static final int[] slots = new int[] {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23,
			24, 25, 28, 29, 30, 31, 32, 33, 34};

	public static Inventory openSettings(User user) {
		Inventory inv =
				openInventory(user, InventoryId.SETTINGS, Message.SETTINGS_TITLE, Item.SETTINGS);
		inv.setItem(21, Item.SETTINGS_WOOL_DIRECTION.getItem(user));
		inv.setItem(23, Item.SETTINGS_HEIGHT_DISPLAY.getItem(user));
		// user.getBukkitEntity().updateInventory();
		return inv;
	}

	public static Inventory openHeightDisplay(User user) {
		Inventory inv = openInventory(user, InventoryId.HEIGHT_DISPLAY,
				Message.HEIGHT_DISPLAY_SETTINGS_TITLE, Item.SETTINGS_HEIGHT_DISPLAY);
		inv.setItem(inv.getSize() / 2 - 1, Item.SETTINGS_HEIGHT_DISPLAY_COLOR.getItem(user));
		setInventoryHeightDisplayToggled(user, inv);
		return inv;
	}

	public static Inventory openWoolDirection(User user) {
		Inventory inv = openInventory(user, InventoryId.WOOL_DIRECTION,
				Message.WOOL_DIRECTION_SETTINGS_TITLE, Item.SETTINGS_WOOL_DIRECTION);
		ItemBuilder ltr = new ItemBuilder(Item.SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT.getItem(user));
		ItemBuilder rtl = new ItemBuilder(Item.SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT.getItem(user));
		WoolSubtractDirection dir = user.getData().getWoolSubtractDirection();
		if (dir == WoolSubtractDirection.LEFT_TO_RIGHT) {
			ltr.glow();
		} else if (dir == WoolSubtractDirection.RIGHT_TO_LEFT) {
			rtl.glow();
		}
		inv.setItem(21, ltr.build());
		inv.setItem(23, rtl.build());
		return inv;
	}

	public static void setInventoryHeightDisplayToggled(User user, Inventory inv) {
		Item i;
		if (user.getData().getHeightDisplay().isEnabled()) {
			i = Item.HEIGHT_DISPLAY_ON;
		} else {
			i = Item.HEIGHT_DISPLAY_OFF;
		}
		inv.setItem(inv.getSize() / 2 + 1, i.getItem(user));
	}

	public static Inventory openHeightDisplayColor(User user) {
		Inventory inv = openInventory(user, InventoryId.HEIGHT_DISPLAY_COLOR,
				Message.HEIGHT_DISPLAY_COLOR_SETTINGS_TITLE, Item.SETTINGS_HEIGHT_DISPLAY_COLOR);

		Set<ChatColor> colors = Arrays.asList(ChatColor.values()).stream()
				.filter(ChatColor::isColor).sorted().collect(Collectors.toSet());
		int count = colors.size();
		ChatColor[] arr2 = colors.toArray(new ChatColor[count]);
		ItemStack[] arr = colors.stream().map(m -> {
			ItemBuilder b = new ItemBuilder(Material.PAPER)
					.addLore(Message.CLICK_TO_SELECT.getMessage(user))
					.addFlag(ItemFlag.HIDE_ENCHANTS);
			ItemManager.setItemId(b, COLOR_SELECTION_ID);
			ItemManager.setId(b, "color", Character.toString(m.getChar()));
			return b.build();
		}).collect(Collectors.toList()).toArray(new ItemStack[count]);

		new BukkitRunnable() {
			private int i = 100;

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (user.getOpenInventory() != InventoryId.HEIGHT_DISPLAY_COLOR) {
					cancel();
					return;
				}
				i--;
				for (int i = 0; i < arr.length; i++) {
					ItemMeta m = arr[i].getItemMeta();
					m.setDisplayName(arr2[i].toString() + this.i);
					if (arr2[i] == user.getData().getHeightDisplay().getColor()) {
						m.addEnchant(Enchantment.ARROW_INFINITE, 100, true);
					} else if (m.hasEnchant(Enchantment.ARROW_INFINITE)) {
						m.removeEnchant(Enchantment.ARROW_INFINITE);
					}
					arr[i].setItemMeta(m);
					inv.setItem(slots[i], arr[i]);
				}
				user.getBukkitEntity().updateInventory();
				if (i <= 0) {
					i = 100;
				}
			}
		}.runTaskTimer(WoolBattle.getInstance(), 1, 0);
		return inv;
	}

	public static Inventory openInventory(User user, InventoryId id, Message title,
			Item settingsItem) {
		Inventory inv = Bukkit.createInventory(null, 5 * 9, title.getMessage(user));
		ItemStack gp = ItemBuilder.item(Material.STAINED_GLASS_PANE).setDisplayName("§5")
				.setDurability(7).build();
		for (int i = 0; i < inv.getSize(); i++) {
			if (i == 4) {
				inv.setItem(i, settingsItem.getItem(user));
			} else if (i <= 8 || i >= inv.getSize() - 9 || (i + 1) % 9 == 1 || (i + 1) % 9 == 0) {
				ItemStack s = gp.clone();
				ItemBuilder b = new ItemBuilder(s);
				b.getUnsafe().setInt("id", i);
				s = b.build();
				inv.setItem(i, s);
			}
		}
		user.getBukkitEntity().openInventory(inv);
		user.setOpenInventory(id);
		return inv;
	}
}
