/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

public enum Tiers {
    TIER1(1, "Tier I"),
    TIER2(2, "Tier II"),
    TIER3(3, "Tier III"),
    TIER4(4, "Tier IV"),
    TIER5(5, "Tier V"),
    TIER6(6, "Tier VI"),
    TIER7(7, "Tier VII"),
    TIER8(8, "Tier VIII"),
    TIER9(9, "Tier IX"),
    TIER10(10, "Tier X"),
    TIER11(11, "Tier XI"),
    TIER12(12, "Tier XII"),
    TIER13(13, "Tier XIII"),
    TIER14(14, "Tier XIV"),
    TIER15(15, "Tier XV"),

    ;

    long id;
    String name;

    private Tiers(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
