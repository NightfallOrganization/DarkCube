/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

public enum Names {

    SYSTEM("§7[§bWool§3Mania§7]"),
    ZINUS("§bZinus §7(§3SHOP§7)"),
    ZINA("§bZina §7(§3TRADE§7)"),
    TELEPORTER("§3Teleporter"),
    VARKAS("§eVarkas §7(§6SMITH§7)"),
    ASTAROTH("§dAstaroth §7(§5ABILITIES§7)"),

    ;

    private final String name;

    Names(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}