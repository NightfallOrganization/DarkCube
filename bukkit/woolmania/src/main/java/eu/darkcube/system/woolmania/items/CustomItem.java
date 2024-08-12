/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.util.PersistentDataValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CustomItem {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    Plugin woolMania = WoolMania.getInstance();
    PersistentDataValue<Tiers> tier;
    PersistentDataValue<Integer> durability;
    PersistentDataValue<Integer> maxDurability;

    public CustomItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        tier = new PersistentDataValue<>(new NamespacedKey(woolMania, "tier"), Tiers.class, itemMeta.getPersistentDataContainer(), Tiers.TIER1);
        durability = new PersistentDataValue<>(new NamespacedKey(woolMania, "durability"), Integer.class, itemMeta.getPersistentDataContainer(), 1);
        maxDurability = new PersistentDataValue<>(new NamespacedKey(woolMania, "maxDurability"), Integer.class, itemMeta.getPersistentDataContainer(), 1);
    }

    //<editor-fold desc="Getter">
    public ItemStack getItemStack() {
        updateMeta();
        return itemStack;
    }

    public ItemMeta getItemMeta() {
        return itemMeta;
    }

    public Integer getDurability() {
        return durability.getOrDefault();
    }

    public Integer getMaxDurability() {
        return maxDurability.getOrDefault();
    }

    public Tiers getTier() {
        return tier.getOrDefault();
    }

    public Integer getID() {
        return tier.getOrDefault().getId();
    }
    //</editor-fold>

    //<editor-fold desc="Setter">
    public void setDisplayName(String string) {
        Component joinMessage = LegacyComponentSerializer.legacySection().deserialize(string);
        itemMeta.displayName(Component.empty().decoration(TextDecoration.ITALIC, false).append(joinMessage));
    }

    public void setTier(Tiers tiers) {
        tier.set(tiers);
    }

    public void setDurability(Integer integer) {
        durability.set(integer);
    }

    public void setMaxDurability(Integer integer) {
        maxDurability.set(integer);
    }
    //</editor-fold>

    private void updateMeta() {
        itemStack.setItemMeta(itemMeta);
    }

    public void updateItemLore() {
        int itemDurability = getDurability();
        int itemMaxDurability = getMaxDurability();
        long tiers = tier.getOrDefault().getId();
        // int itemBuffSlot = getBuffSlot();
        setLore(itemDurability, itemMaxDurability, tiers);
    }

    public void setLore(int itemdurability, int itemmaxDurability, long tiers) {
        List<String> lore = new ArrayList<>();
        boolean showStats = tiers > 0 || itemdurability != -2;

        if (showStats) {
            lore.add(" ");
            lore.add("§7§m      §7« §bStats §7»§m      ");
            lore.add(" ");

            if (tiers > 0) {
                lore.add("§7Seltenheit: §6" + tier.getOrDefault().getName());
            }

            if (itemdurability == -1) {
                lore.add("§7Durability: §a∞");
            } else if (itemdurability != -2) {
                lore.add("§7Durability: §a" + itemdurability + "§7/§a" + itemmaxDurability);
            }

            lore.add(" ");
        }

        // List<Buff> buffs = getBuffList();
        // if (itemBuffSlot > 0 || !buffs.isEmpty()) {
        //     lore.add(" ");
        //     lore.add("§7§m      §7« §aBuff §7»§m      ");
        //     lore.add(" ");
        // }

        // if (!buffs.isEmpty()) {
        //     for (Buff buff : buffs) {
        //         String buffValue = buff.getType().getPrefix() + " §7+" + Math.round(buff.getValue());
        //         if (buff.isPercentage()) {
        //             buffValue += "%";
        //         }
        //         lore.add(buffValue);
        //     }
        // }

        // for (int i = 0; i < itemBuffSlot; i++) {
        //     lore.add("§7[ §8Empty §7]");
        // }
        //
        // if (itemBuffSlot > 0 || !buffs.isEmpty()) {
        //     lore.add(" ");
        // }

        itemMeta.setLore(lore);
    }

    public void hideInformations() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    }
}
