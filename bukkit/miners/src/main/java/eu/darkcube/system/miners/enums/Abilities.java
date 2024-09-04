/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.enums;

import static eu.darkcube.system.miners.enums.InventoryItems.INVENTORY_ABILITY_ITEM_FLY;
import static eu.darkcube.system.miners.utils.message.Message.ABILITY_DIGGER;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.miners.utils.message.Message;

public enum Abilities {
    DIGGER("digger",false, false, INVENTORY_ABILITY_ITEM_FLY, ABILITY_DIGGER,2500000),

    ;

    private final boolean bought;
    private final boolean active;
    private final InventoryItems inventoryItems;
    private final Message message;
    private final int cost;
    private final String id;

    Abilities(String id, boolean bought, boolean active, InventoryItems inventoryItems, Message message, int cost) {
        this.id = id;
        this.bought = bought;
        this.active = active;
        this.inventoryItems = inventoryItems;
        this.message = message;
        this.cost = cost;
    }

    public static List<Abilities> boughtAbilities() {
        List<Abilities> boughtAbilities = new ArrayList<>();
        for (Abilities ability : Abilities.values()) {
            if (ability.isDefaultBought()) {
                boughtAbilities.add(ability);
            }
        }
        return List.copyOf(boughtAbilities);
    }

    public boolean isDefaultActive() {
        return active;
    }

    public boolean isDefaultBought() {
        return bought;
    }

    public InventoryItems getInventoryItems() {
        return inventoryItems;
    }

    public int getCost() {
        return cost;
    }

    public String getId() {
        return id;
    }

    public Message getName() {
        return message;
    }
}
