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
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.util.data.Key;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class PerksTypeInventory extends WoolBattlePagedInventory {
	public static final InventoryType TYPE = InventoryType.of("woolbattle-perks-type");
	private static final Key PERK = new Key(WoolBattle.getInstance(), "perk");
	private final ActivationType type;
	private final int number;
	private boolean done;

	public PerksTypeInventory(WBUser user, ActivationType type, int number) {
		super(TYPE, Message.INVENTORY_PERKS.getMessage(user), user);
		this.type = type;
		this.number = number;
		this.done = true;
		complete();
	}

	@Override
	protected void inventoryClick(IInventoryClickEvent event) {
		event.setCancelled(true);
		if (event.item() == null)
			return;
		String perkName = ItemManager.getId(event.item(), PERK);
		if (perkName == null)
			return;
		PerkName perk = new PerkName(perkName);
		PlayerPerks perks = user.perksStorage();
		List<PerkName> userPerks = Arrays.asList(perks.perks(type));
		if (userPerks.contains(perk)) {
			return;
		}
		perks.perk(type, number, perk);
		user.perksStorage(perks);
		user.perks().reloadFromStorage();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		Perk[] perks = WoolBattle.getInstance().perkRegistry().perks(type);
		List<PerkName> userPerks = Arrays.asList(user.perksStorage().perks(type));
		for (int i = 0; i < perks.length; i++) {
			ItemBuilder b = ItemBuilder.item(perks[i].defaultItem().getItem(user));
			ItemManager.setId(b, PERK, perks[i].perkName().getName());
			if (userPerks.contains(perks[i].perkName())) {
				b.lore(Message.SELECTED.getMessage(user));
			}
			if (userPerks.get(number).equals(perks[i].perkName())) {
				b.glow();
			}
			items.put(i, b.build());
		}
	}

	@Override
	public boolean done() {
		return super.done() && done;
	}
}
