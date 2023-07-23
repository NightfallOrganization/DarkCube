/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.util.data.Key;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PerksInventory extends WoolBattlePagedInventory {
	public static final InventoryType TYPE = InventoryType.of("woolbattle_perks");
	private static final Key PERKS_TYPE = new Key(WoolBattle.instance(), "perks_type");
	private static final Key PERKS_TYPE_NUMBER =
			new Key(WoolBattle.instance(), "perks_type_number");

	public PerksInventory(WBUser user) {
		super(TYPE, Message.INVENTORY_PERKS.getMessage(user), user);
	}

	@Override
	protected void inventoryClick(IInventoryClickEvent event) {
		event.setCancelled(true);
		if (event.item() == null)
			return;
		String typeId = ItemManager.getId(event.item(), PERKS_TYPE);
		if (typeId == null)
			return;
		ActivationType type = ActivationType.values()[Integer.parseInt(typeId)];
		int number = Integer.parseInt(ItemManager.getId(event.item(), PERKS_TYPE_NUMBER));
		user.setOpenInventory(new PerksTypeInventory(user, type, number));
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		PerkRegistry registry = WoolBattle.instance().perkRegistry();
		int i = 0;
		for (Perk.ActivationType type : Perk.ActivationType.values()) {
			Perk[] perks = registry.perks(type);
			for (int j = 0; j < type.maxCount(); j++) {
				if (perks.length > type.maxCount()) {
					ItemBuilder b = ItemBuilder.item(type.displayItem().getItem(user, j));
					ItemManager.setId(b, PERKS_TYPE, String.valueOf(type.ordinal()));
					ItemManager.setId(b, PERKS_TYPE_NUMBER, String.valueOf(j));
					items.put(i++, b.build());
				}
			}
		}
	}
}
