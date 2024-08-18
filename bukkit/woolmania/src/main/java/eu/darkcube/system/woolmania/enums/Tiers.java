/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

public enum Tiers {
    // DEFAULT_TIER(1, "Default Tier"),
    TIER1(1, "Tier I", 150),
    TIER2(2, "Tier II", 500),
    TIER3(3, "Tier III", 850),
    TIER4(4, "Tier IV", 1200),
    TIER5(5, "Tier V", 1550),
    TIER6(6, "Tier VI", 1900),
    TIER7(7, "Tier VII", 2250),
    TIER8(8, "Tier VIII", 2600),
    TIER9(9, "Tier IX", 2950),
    TIER10(10, "Tier X", 3300),
    TIER11(11, "Tier XI", 3650),
    TIER12(12, "Tier XII", 4000),
    TIER13(13, "Tier XIII", 4350),
    TIER14(14, "Tier XIV", 4700),
    TIER15(15, "Tier XV", 5050),
    TIER16(15, "Tier XVI", 5400),

    ;

    private final int id;
    private final String name;
    private final int durability;

    Tiers(int id, String name, int durability) {
        this.id = id;
        this.name = name;
        this.durability = durability;
    }

    Tiers(int id, String name){
        this.id = id;
        this.name = name;
        this.durability = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // NICHT TUN
    // public void setName(ItemBuilder builder) {
    //     builder.displayname(Component.text(name));
    // }

    public int getDurability() {
        return durability;
    }
}
