/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.equipment.crafting;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Recipe {

    List<CraftingType> getCraftingTypes();
    boolean matchesPattern(ItemStack[][] craftingContent);
    boolean canCraft(Player p);
    List<ItemStack> getResult();

}
