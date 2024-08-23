/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_ABILITY_ITEM_FLY;
import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_ABILITY_ITEM_SPEED;

import java.util.ArrayList;
import java.util.List;

public enum Abilitys {
    FLY(false, false, INVENTORY_ABILITY_ITEM_FLY, 15000),
    SPEED(false, false, INVENTORY_ABILITY_ITEM_SPEED, 15000),

    ;

    private final boolean buyed;
    private final boolean active;
    private final InventoryItems inventoryItems;
    private final int cost;

    Abilitys(boolean buyed, boolean active, InventoryItems inventoryItems, int cost) {
        this.buyed = buyed;
        this.active = active;
        this.inventoryItems = inventoryItems;
        this.cost = cost;
    }

    public static List<Abilitys> boughtAbilities() {
        List<Abilitys> boughtAbilities = new ArrayList<>();
        for (Abilitys ability : Abilitys.values()) {
            if (ability.isBuyed()) {
                boughtAbilities.add(ability);
            }
        }
        return List.copyOf(boughtAbilities);
    }

    public static List<Abilitys> activeAbilities() {
        List<Abilitys> activeAbilities = new ArrayList<>();
        for (Abilitys ability : Abilitys.values()) {
            if (ability.isActive()) {
                activeAbilities.add(ability);
            }
        }
        return List.copyOf(activeAbilities);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isBuyed() {
        return buyed;
    }

    public InventoryItems getInventoryItems() {
        return inventoryItems;
    }

    public int getCost() {
        return cost;
    }
}
