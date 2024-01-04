/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.equipment.crafting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BasicRecipe implements Recipe{
    List<CraftingType> craftingTypes;
    List<ItemStack> result;
    ItemStack[][] pattern;

    @Override
    public List<CraftingType> getCraftingTypes() {
        return craftingTypes;
    }

    @Override public boolean matchesPattern(ItemStack[][] craftingContent) {
        //todo
        for (int i = 0; i < craftingContent.length; i++) {
            for (int i1 = 0; i1 < craftingContent[i].length; i1++) {
                if (pattern.length > i){
                    if (pattern[i].length > i1){
                        if (!pattern[i][i1].getType().equals(craftingContent[i][i1].getType()) || pattern[i][i1].getAmount() > (craftingContent[i][i1].getAmount())){
                            return false;
                        }
                    }else {
                        if (!craftingContent[i][i1].getType().equals(Material.AIR)){
                            return false;
                        }
                    }
                }else {
                    if (!craftingContent[i][i1].getType().equals(Material.AIR)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override public boolean canCraft(Player p) {
        //todo crafting restrictions
        return true;
    }

    @Override public List<ItemStack> getResult() {
        return result;
    }
}
