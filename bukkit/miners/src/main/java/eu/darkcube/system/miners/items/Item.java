/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.items;

import static eu.darkcube.system.server.item.ItemBuilder.item;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum Item {

    PICKAXE_STONE(item(Material.STONE_PICKAXE).unbreakable(true)),
    PICKAXE_IRON(item(Material.IRON_PICKAXE).unbreakable(true)),
    PICKAXE_GOLD(item(Material.GOLD_PICKAXE).unbreakable(true)),
    PICKAXE_DIAMOND(item(Material.DIAMOND_PICKAXE).unbreakable(true)),

    INGOT_IRON(item(Material.IRON_INGOT)),
    INGOT_GOLD(item(Material.GOLD_INGOT)),
    DIAMOND(item(Material.DIAMOND)),
    COBBLESTONE(item(Material.COBBLESTONE)),
    TNT(item(Material.TNT)),
    SHEARS(item(Material.SHEARS)),
    CRAFTING_TABLE(item(Material.WORKBENCH)),
    STICK(item(Material.STICK)),
    FLINT(item(Material.FLINT)),
    FLINT_AND_STEEL(item(Material.FLINT_AND_STEEL));

    public static final String ITEM_PREFIX = "ITEM_";
    public static final String NAME_SUFFIX = "_NAME";
    public static final String LORE_SUFFIX = "_LORE";
    private final ItemBuilder itemBuilder;
    private final String keyName;
    private final String keyLore;

    Item(ItemBuilder ib) {
        this.itemBuilder = ib;
        ib.persistentDataStorage().set(ItemKey.ITEM, PersistentDataTypes.STRING, this.name());
        this.keyName = ITEM_PREFIX + this.name() + NAME_SUFFIX;
        this.keyLore = ITEM_PREFIX + this.name() + LORE_SUFFIX;
    }

    public ItemStack getItem(Language lang, int amount) {
        var itemBuilder = this.itemBuilder.clone();
        itemBuilder.displayname(lang.getMessage(keyName));
        itemBuilder.lore(lang.getMessage(keyLore));
        itemBuilder.amount(amount);
        return itemBuilder.build();
    }

    public ItemStack getItem(Player player, int amount) {
        return getItem(Miners.getPlayerManager().getMinersPlayer(player).getLanguage(), amount);
    }

    public ItemStack getItem(Player player) {
        return getItem(player, 1);
    }

    public static class ItemKey { // Quick fix, change later
        public static final Key ITEM = new Key(Miners.getInstance(), "item");
    }

}
