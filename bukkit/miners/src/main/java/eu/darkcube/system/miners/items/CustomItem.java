/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.items;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.inventory.ItemStack;

public class CustomItem {
    private static final DataKey<String> ID = DataKey.of(Key.key(Miners.getInstance(), "id"), PersistentDataTypes.STRING);
    private static final DataKey<Boolean> FOOD = DataKey.of(Key.key(Miners.getInstance(), "food"), PersistentDataTypes.BOOLEAN);

    private final ItemBuilder item;

    public CustomItem(ItemBuilder item) {
        this.item = item;
    }

    //<editor-fold desc="Getter">
    public ItemStack getItemStack() {
        return item.build();
    }

    public @Nullable String getItemID() {
        return item.persistentDataStorage().get(ID);
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

    public boolean isFood() {
        return item.persistentDataStorage().get(FOOD, this::getDefaultFood);
    }
    //</editor-fold>

    //<editor-fold desc="Setter">

    public void setDisplayName(Component displayName) {
        item.displayname(displayName);
    }

    public void setFood(boolean isFood) {
        item.persistentDataStorage().set(FOOD, isFood);
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

    public Boolean hasItemID() {
        return item.persistentDataStorage().has(ID);
    }

    public void addGlowing() {
        item.glow(true);
    }

}
