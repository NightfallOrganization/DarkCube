/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.BOOK;

import java.util.ArrayList;
import java.util.Collection;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.pserver.plugin.user.User;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.inventory.ItemStack;

public enum Item implements BaseMessage {

    ARROW_NEXT(item(ARROW)), ARROW_PREVIOUS(item(ARROW)), USER_MANAGMENT_PERMISSIONS(item(BOOK)),

    ;

    public static final String PREFIX = "ITEM_";
    public static final String PREFIX_LORE = "LORE_";
    public static final Key ITEMID_KEY = new Key(PServerPlugin.instance(), "itemid");

    private final String key;
    private final boolean hasLore;
    private final ItemBuilder builder;

    Item(ItemBuilder builder) {
        this(builder, false);
    }

    Item(ItemBuilder builder, boolean hasLore) {
        this.builder = builder;
        this.hasLore = hasLore;
        this.key = name();
    }

    public static boolean hasItemId(ItemStack item) {
        return ItemBuilder.item(item).persistentDataStorage().has(ITEMID_KEY);
    }

    public static String getItemId(ItemStack item) {
        return ItemBuilder.item(item).persistentDataStorage().get(ITEMID_KEY, PersistentDataTypes.STRING);
    }

    public Collection<String> getMessageKeys() {
        Collection<String> keys = new ArrayList<>();
        keys.add(PREFIX + key);
        if (hasLore()) keys.add(PREFIX + PREFIX_LORE + keys);
        return keys;
    }

    public boolean hasLore() {
        return hasLore;
    }

    @Override public String key() {
        return key;
    }

    public ItemStack getItem(User user) {
        return this.getItem(user, new Object[0]);
    }

    public ItemStack getItem(User user, Object... displayNameArgs) {
        return this.getItem(user, displayNameArgs, new Object[0]);
    }

    public ItemStack getItem(User user, Object[] displayNameArgs, Object[] loreArgs) {
        ItemBuilder b = builder.clone();
        b.displayname(getDisplayName(user, displayNameArgs));
        if (hasLore()) {
            b.lore(getLore(user, loreArgs));
        }
        b.persistentDataStorage().set(ITEMID_KEY, PersistentDataTypes.STRING, key());
        return b.build();
    }

    public boolean equals(ItemStack item) {
        return hasItemId(item) && key().equals(getItemId(item));
    }

    public Component getLore(User user, Object... args) {
        return getMessage(user.getCommandExecutor(), new String[]{PREFIX, PREFIX_LORE}, args);
    }

    public Component getDisplayName(User user, Object... args) {
        return getMessage(user.getCommandExecutor(), new String[]{PREFIX}, args);
    }
}
