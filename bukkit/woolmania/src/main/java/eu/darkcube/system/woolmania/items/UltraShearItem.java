/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import static eu.darkcube.system.woolmania.util.message.CustomItemNames.ITEM_SHEARS;

import java.util.List;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.Tool;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.enums.Tiers;
import org.bukkit.Material;

public class UltraShearItem extends CustomItem {

    public static final String ITEM_ID = "ultra_shear";

    public UltraShearItem(User user) {
        super(ItemBuilder.item(Material.SHEARS).set(ItemComponent.TOOL, new Tool(List.of(new Tool.Rule(new BlockTypeFilter.Tag("wool"), 50f, true),
                newToolRule(Material.COPPER_BULB),
                newToolRule(Material.EXPOSED_COPPER_BULB),
                newToolRule(Material.OXIDIZED_COPPER_BULB),
                newToolRule(Material.WAXED_COPPER_BULB),
                newToolRule(Material.WAXED_EXPOSED_COPPER_BULB),
                newToolRule(Material.WAXED_WEATHERED_COPPER_BULB),
                newToolRule(Material.WAXED_OXIDIZED_COPPER_BULB),
                newToolRule(Material.WEATHERED_COPPER_BULB),

                newToolRule(Material.COPPER_GRATE),
                newToolRule(Material.EXPOSED_COPPER_GRATE),
                newToolRule(Material.OXIDIZED_COPPER_GRATE),
                newToolRule(Material.WAXED_COPPER_GRATE),
                newToolRule(Material.WAXED_EXPOSED_COPPER_GRATE),
                newToolRule(Material.WAXED_WEATHERED_COPPER_GRATE),
                newToolRule(Material.WAXED_OXIDIZED_COPPER_GRATE),
                newToolRule(Material.WEATHERED_COPPER_GRATE)
        ), 0, 1)));

        setDisplayName(ITEM_SHEARS.getMessage(user));
        setAmount(1);
        setTier(Tiers.TIER16);
        setItemID(ITEM_ID);
        setLevel(1);
        setWholeDurability(-1);
        setUnbreakableHidden();
        updateItemLore();
    }

    private static Tool.Rule newToolRule(Material material) {
        return new Tool.Rule(new BlockTypeFilter.Blocks(eu.darkcube.system.server.item.material.Material.of(material)), 100f, true);
    }
}
