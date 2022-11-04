package eu.darkcube.minigame.smash.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import eu.darkcube.minigame.smash.user.Message;
import eu.darkcube.minigame.smash.user.User;

public class ItemManager {

	public static int countItems(ItemStack item, Inventory inv) {
		int i = 1;
		for (; inv.contains(item, i); i++) {
		}
		if (inv instanceof PlayerInventory) {
			PlayerInventory t = (PlayerInventory) inv;
			List<ItemStack> items = new ArrayList<>();
			items.add(t.getHolder().getItemOnCursor());
			items.add(t.getBoots());
			items.add(t.getChestplate());
			items.add(t.getLeggings());
			items.add(t.getHelmet());
			for (ItemStack s : items) {
				if (item.equals(s)) {
					i += s.getAmount();
				}
			}
		}
		return i - 1;
	}

	public static void removeItems(Inventory invToRemoveFrom, ItemStack itemToRemove, int count) {
		for (int i = 0; i < count; i++)
			invToRemoveFrom.removeItem(itemToRemove);
	}

	public static int countItems(Material item, Inventory inv) {
		int i = 1;
		for (; inv.contains(item, i); i++) {
		}
		if (inv instanceof PlayerInventory) {
			PlayerInventory t = (PlayerInventory) inv;
			List<ItemStack> items = new ArrayList<>();
			items.add(t.getHolder().getItemOnCursor());
			items.add(t.getBoots());
			items.add(t.getChestplate());
			items.add(t.getLeggings());
			items.add(t.getHelmet());
			for (ItemStack s : items) {
				if (s != null)
					if (s.getType() == item) {
						i += s.getAmount();
					}
			}
		}
		return i - 1;
	}

	public static ItemStack getItem(Item item, User user, String... replacements) {
		return getItem(item, user, replacements, new String[0]);
	}

	public static ItemStack getItem(Item item, User user, String[] replacements, String... loreReplacements) {
		ItemBuilder builder = item.getBuilder().getUnsafe().setString("itemId", getItemId(item)).builder();
		String name = getDisplayName(item, user, replacements);
		builder.setDisplayName(name);
		if (builder.getLores().size() != 0) {
			builder.getLores().clear();
			String last = null;
			for (String lore : Message.getMessage(Message.ITEM_PREFIX + Message.LORE_PREFIX + item.name(),
					user.getLanguage(), loreReplacements).split("\\%n")) {
				if (last != null) {
					lore = ChatColor.getLastColors(last) + lore;
				}
				last = lore;
				builder.addLore(last);
			}
		}
		return builder.build();
	}

	public static String getItemId(Item item) {
		return Message.ITEM_PREFIX + item.name();
	}

	public static String getMapId(ItemStack item) {
		return getNBTValue(new ItemBuilder(item), "map");
	}

	public static int getLifes(ItemStack item) {
		return Integer.parseInt(getNBTValue(new ItemBuilder(item), "lifes"));
	}

	public static String getItemId(ItemStack item) {
		return getNBTValue(new ItemBuilder(item), "itemId");
	}

	public static String getTeamId(ItemStack item) {
		return getNBTValue(new ItemBuilder(item), "team");
	}

	private static String getNBTValue(ItemBuilder builder, String key) {
		return builder.getUnsafe().getString(key);
	}

	public static String getDisplayName(Item item, User user, String... replacements) {
		return Message.getMessage(getItemId(item), user.getLanguage(), replacements);
	}
}
