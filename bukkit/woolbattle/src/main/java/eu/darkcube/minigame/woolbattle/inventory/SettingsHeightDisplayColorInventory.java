/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.util.data.Key;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SettingsHeightDisplayColorInventory extends WoolBattlePagedInventory {
	public static final InventoryType TYPE =
			InventoryType.of("woolbattle-settings-height-display-color");
	private static final String COLOR_SELECTION_ID = "COLOR_SELECTION";

	public SettingsHeightDisplayColorInventory(WBUser user) {
		super(TYPE, Message.HEIGHT_DISPLAY_COLOR_SETTINGS_TITLE.getMessage(user), user);
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		List<ChatColor> colors =
				Arrays.stream(ChatColor.values()).filter(ChatColor::isColor).sorted()
						.collect(Collectors.toList());
		ItemStack[] arr = colors.stream().map(c -> {
			ItemBuilder b = ItemBuilder.item(Material.PAPER);
			if (user.heightDisplay().getColor() == c) {
				b.lore(Message.SELECTED.getMessage(user));
				b.glow();
			} else {
				b.lore(Message.CLICK_TO_SELECT.getMessage(user));
			}
			ItemManager.setItemId(b, COLOR_SELECTION_ID);
			ItemManager.setId(b, new Key(WoolBattle.getInstance(), "color"),
					Character.toString(c.getChar()));
			return b.build();
		}).toArray(ItemStack[]::new);
		for (int i = 0; i < arr.length; i++) {
			items.put(i, arr[i]);
		}
	}

	@Override
	protected void insertFallbackItems() {
		super.insertFallbackItems();
		fallbackItems.put(IInventory.slot(1, 5), Item.SETTINGS_HEIGHT_DISPLAY_COLOR.getItem(user));
	}

	@Override
	protected void inventoryClick(IInventoryClickEvent event) {
		event.setCancelled(true);
	}
}
