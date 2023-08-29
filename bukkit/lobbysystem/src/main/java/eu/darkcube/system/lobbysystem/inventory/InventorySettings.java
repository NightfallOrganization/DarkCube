/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullUtils;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventorySettings extends LobbyAsyncPagedInventory {
    public static final Key language = new Key(Lobby.getInstance(), "language");
    private static final InventoryType type_settings = InventoryType.of("settings");

    public InventorySettings(User user) {
        super(InventorySettings.type_settings, Item.INVENTORY_SETTINGS.getDisplayName(user), user);
    }

    @Override protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 5), Item.INVENTORY_SETTINGS.getItem(this.user.user()));
        this.fallbackItems.put(IInventory.slot(4, 3), this.user.isSounds() ? Item.INVENTORY_SETTINGS_SOUNDS_ON.getItem(this.user.user()) : Item.INVENTORY_SETTINGS_SOUNDS_OFF.getItem(this.user.user()));

        String textureId = "";
        String name = "";
        switch (this.user.user().getLanguage()) {
            case ENGLISH:
                textureId = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1" + "cmUvYTE3MDFmMjE4MzVhODk4YjIwNzU5ZmIzMGE1ODNhMzhiOTk0YWJmNjBkMzkxMmFiNGNlOWYyMzExZTc0Zj" + "cyIn19fQ==";
                name = "English";
                break;
            case GERMAN:
                textureId = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1" + "cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3Yj" + "NmIn19fQ==";
                name = "Deutsch";
                break;
        }
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e" + name);
        item.setItemMeta(meta);
        item = ItemBuilder
                .item(item)
                .persistentDataStorage()
                .iset(language, PersistentDataTypes.STRING, this.user.user().getLanguage().name())
                .builder()
                .build();
        SkullUtils.setSkullTextureId(item, textureId);
        this.fallbackItems.put(IInventory.slot(4, 5), item);
        this.fallbackItems.put(IInventory.slot(4, 7), this.user.isAnimations() ? Item.INVENTORY_SETTINGS_ANIMATIONS_ON.getItem(this.user.user()) : Item.INVENTORY_SETTINGS_ANIMATIONS_OFF.getItem(this.user.user()));
        super.insertFallbackItems();
    }
}
