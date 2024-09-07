/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.items;
import static eu.darkcube.system.miners.utils.message.CustomItemNames.ITEM_STARTER_PICKAXE;

import java.util.List;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.Tool;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;

public class StarterPickaxeItem extends CustomItem {

    public StarterPickaxeItem(User user) {
        super(ItemBuilder.item(Material.IRON_PICKAXE).set(ItemComponent.TOOL, new Tool(List.of(
                newToolRule(Material.STONE),
                newToolRule(Material.IRON_ORE),
                newToolRule(Material.EMERALD_ORE),
                newToolRule(Material.DIAMOND_ORE),
                newToolRule(Material.GOLD_ORE),
                newToolRule(Material.REDSTONE_ORE),
                newToolRule(Material.COAL_ORE),
                newToolRule(Material.OAK_PLANKS),
                newToolRule(Material.TNT)
        ), 10, 1)));

        setDisplayName(ITEM_STARTER_PICKAXE.getMessage(user));
        setItemID("starter_pickaxe");
        setAmount(1);
        setUnbreakableHidden();
    }

    private static Tool.Rule newToolRule(Material material) {
        return new Tool.Rule(new BlockTypeFilter.Blocks(eu.darkcube.system.server.item.material.Material.of(material)), 10f, true);
    }

}
