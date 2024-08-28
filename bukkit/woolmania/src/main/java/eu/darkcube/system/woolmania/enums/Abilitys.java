/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;

import java.util.ArrayList;
import java.util.List;

public enum Abilitys {
    FLY("fly",false, false, INVENTORY_ABILITY_ITEM_FLY, 2500000),
    SPEED("speed",false, false, INVENTORY_ABILITY_ITEM_SPEED, 15000),
    AUTO_EAT("auto_eat",false, false, INVENTORY_ABILITY_ITEM_AUTO_EAT, 1500000),

    ;

    private final boolean buyed;
    private final boolean active;
    private final InventoryItems inventoryItems;
    private final int cost;
    private final String id;

    Abilitys(String id,boolean buyed, boolean active, InventoryItems inventoryItems, int cost) {
        this.id = id;
        this.buyed = buyed;
        this.active = active;
        this.inventoryItems = inventoryItems;
        this.cost = cost;
    }

    public static List<Abilitys> boughtAbilities() {
        List<Abilitys> boughtAbilities = new ArrayList<>();
        for (Abilitys ability : Abilitys.values()) {
            if (ability.isDefaultBuyed()) {
                boughtAbilities.add(ability);
            }
        }
        return List.copyOf(boughtAbilities);
    }

    public static List<Abilitys> activeAbilities() {
        List<Abilitys> activeAbilities = new ArrayList<>();
        for (Abilitys ability : Abilitys.values()) {
            if (ability.isDefaultActive()) {
                activeAbilities.add(ability);
            }
        }
        return List.copyOf(activeAbilities);
    }

    public boolean isDefaultActive() {
        return active;
    }

    public boolean isDefaultBuyed() {
        return buyed;
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
}
