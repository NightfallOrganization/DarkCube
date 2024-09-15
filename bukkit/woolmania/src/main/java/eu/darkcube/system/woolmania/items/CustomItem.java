/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.empty;
import static eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Tiers;
import org.bukkit.inventory.ItemStack;

public class CustomItem {
    private static final DataKey<Tiers> TIER = DataKey.ofImmutable(Key.key(WoolMania.getInstance(), "tier"), Tiers.class);
    private static final DataKey<Integer> DURABILITY = DataKey.of(Key.key(WoolMania.getInstance(), "durability"), PersistentDataTypes.INTEGER);
    private static final DataKey<Integer> MAX_DURABILITY = DataKey.of(Key.key(WoolMania.getInstance(), "max_durability"), PersistentDataTypes.INTEGER);
    private static final DataKey<Integer> LEVEL = DataKey.of(Key.key(WoolMania.getInstance(), "level"), PersistentDataTypes.INTEGER);
    private static final DataKey<String> ID = DataKey.of(Key.key(WoolMania.getInstance(), "id"), PersistentDataTypes.STRING);
    private static final DataKey<Boolean> FOOD = DataKey.of(Key.key(WoolMania.getInstance(), "food"), PersistentDataTypes.BOOLEAN);

    private final ItemBuilder item;

    public CustomItem(ItemBuilder item) {
        this.item = item;
    }

    //<editor-fold desc="Getter">
    public ItemStack getItemStack() {
        return item.build();
    }

    public int getDurability() {
        return item.persistentDataStorage().get(DURABILITY, this::getDefaultDurability);
    }

    public int getLevel() {
        return item.persistentDataStorage().get(LEVEL, this::getDefaultLevel);
    }

    // public int getMaxDurability() {
    //     return item.persistentDataStorage().get(MAX_DURABILITY, () -> 1);
    // }

    public int getMaxDurability() {
        return item.persistentDataStorage().get(MAX_DURABILITY, this::getDefaultDurability);
    }

    public Tiers getTier() {
        return item.persistentDataStorage().get(TIER, () -> Tiers.TIER1);
    }

    public int getTierID() {
        return getTier().getId();
    }

    public @Nullable String getItemID() {
        return item.persistentDataStorage().get(ID);
    }

    public int getDefaultDurability() {
        return 1;
    }

    public int getDefaultLevel() {
        return 0;
    }

    public boolean getDefaultFood() {
        return false;
    }

    public int getAmount() {
        return item.amount();
    }

    public Component getDisplayName() {
        return item.displayname();
    }

    public void getNutrition() {

    }

    public boolean isFood() {
        return item.persistentDataStorage().get(FOOD, this::getDefaultFood);
    }

    //</editor-fold>

    //<editor-fold desc="Setter">
    // public void setDisplayName(String displayName) {
    //     item.displayname(LegacyComponentSerializer.legacySection().deserialize(displayName));
    // }

    public void setDisplayName(Component displayName) {
        item.displayname(displayName);
    }

    public void setTier(Tiers tiers) {
        item.persistentDataStorage().set(TIER, tiers);
    }

    public void setDurability(int durability) {
        item.persistentDataStorage().set(DURABILITY, durability);
    }

    public void setMaxDurability(int maxDurability) {
        item.persistentDataStorage().set(MAX_DURABILITY, maxDurability);
    }

    public void setLevel(int level) {
        item.persistentDataStorage().set(LEVEL, level);
    }

    public void setFood(boolean isFood) {
        item.persistentDataStorage().set(FOOD, isFood);
    }

    public void setWholeDurability(int durability) {
        item.persistentDataStorage().set(DURABILITY, durability);
        item.persistentDataStorage().set(MAX_DURABILITY, durability);
    }

    public void setAmount(int amount) {
        item.amount(amount);
    }

    public void setUnbreakable() {
        item.unbreakable(true);
    }

    public void setUnbreakableHidden() {
        item.hiddenUnbreakable();
    }

    public void setItemID(String id) {
        item.persistentDataStorage().set(ID, id);
    }

    //</editor-fold>

    public boolean hasMaxDurability() {
        if (item.persistentDataStorage().has(MAX_DURABILITY)) {
            return item.persistentDataStorage().get(MAX_DURABILITY, this::getDefaultDurability) != 1;
        }
        return false;
    }

    public boolean hasItemID() {
        return item.persistentDataStorage().has(ID);
    }

    public void reduceDurability() {
        int currentDurability = getDurability();
        if (currentDurability > 0) {
            setDurability(currentDurability - 1);
            updateItemLore();
        }
    }

    public void addGlowing() {
        item.glow(true);
    }

    public void updateItemLore() {
        int itemDurability = getDurability();
        int itemMaxDurability = getMaxDurability();
        int tiers = getTierID();
        int level = getLevel();
        // int itemBuffSlot = getBuffSlot();
        setLore(itemDurability, itemMaxDurability, tiers, level);
    }

    public void setLore(int itemdurability, int itemmaxDurability, long tiers, int level) {
        List<Component> lore = new ArrayList<>();
        boolean showStats = tiers > 0 || itemdurability != -2;

        if (showStats) {
            lore.add(empty());
            lore.add(legacySection().deserialize("§7§m      §7« §bStats §7»§m      "));
            lore.add(empty());

            if (tiers > 0) {
                lore.add(legacySection().deserialize("§7Seltenheit: §6" + getTier().getName()));
            }

            if (itemdurability == -1) {
                lore.add(legacySection().deserialize("§7Durability: §a∞"));
            } else if (itemdurability != -2) {
                lore.add(legacySection().deserialize("§7Durability: §a" + itemdurability + "§7/§a" + itemmaxDurability));
            }

            lore.add(empty());
        }

        if (level > 0) {
            lore.add(legacySection().deserialize("§7§m      §7« §dReqir §7»§7§m      "));
            lore.add(empty());
            lore.add(legacySection().deserialize("§7Level: §6" + level));
            lore.add(empty());
        }

        item.setLore(lore);
    }

}
