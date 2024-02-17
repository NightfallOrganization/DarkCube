/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.inventory;

import java.util.Map;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import org.bukkit.inventory.ItemStack;

public class PerksInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle_perks");
    private final Key PERKS_TYPE;
    private final Key PERKS_TYPE_NUMBER;

    public PerksInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.INVENTORY_PERKS.getMessage(user), user);
        PERKS_TYPE = new Key(woolbattle, "perks_type");
        PERKS_TYPE_NUMBER = new Key(woolbattle, "perks_type_number");
        complete();
    }

    @Override protected boolean done() {
        return super.done() && PERKS_TYPE != null;
    }

    @Override protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        String typeId = ItemManager.getId(event.item(), PERKS_TYPE);
        if (typeId == null) return;
        ActivationType type = ActivationType.values()[Integer.parseInt(typeId)];
        int number = Integer.parseInt(ItemManager.getId(event.item(), PERKS_TYPE_NUMBER));
        user.setOpenInventory(new PerksTypeInventory(woolbattle, user, type, number));
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        PerkRegistry registry = woolbattle.perkRegistry();
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
