/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import static eu.darkcube.system.server.item.component.ItemComponent.FIREWORK_EXPLOSION;
import static org.bukkit.Material.*;

import java.util.ArrayList;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.components.FireworkExplosion;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Color;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum Item {

    INVENTORY_COMPASS(ItemBuilder.item(Material.COMPASS)),

    INVENTORY_GADGET(ItemBuilder.item(ENDER_CHEST)),

    INVENTORY_LOBBY_SWITCHER(ItemBuilder.item(NETHER_STAR)),
    INVENTORY_LOBBY_SWITCHER_CURRENT(ItemBuilder.item(STORAGE_MINECART)),
    INVENTORY_LOBBY_SWITCHER_OTHER(ItemBuilder.item(MINECART)),

    INVENTORY_SETTINGS(ItemBuilder.item(REDSTONE_COMPARATOR)),
    INVENTORY_SETTINGS_ANIMATIONS_ON(ItemBuilder.item(BLAZE_POWDER)),
    INVENTORY_SETTINGS_ANIMATIONS_OFF(ItemBuilder.item(BLAZE_POWDER)),
    INVENTORY_SETTINGS_SOUNDS_ON(ItemBuilder.item(GOLD_RECORD)),
    INVENTORY_SETTINGS_SOUNDS_OFF(ItemBuilder.item(GOLD_RECORD)),

    INVENTORY_COMPASS_JUMPANDRUN(ItemBuilder.item(DIAMOND_BOOTS)),
    INVENTORY_COMPASS_SPAWN(ItemBuilder.item(NETHER_STAR)),
    INVENTORY_COMPASS_SMASH(ItemBuilder.item(FIREBALL)),
    INVENTORY_COMPASS_WOOLBATTLE(ItemBuilder.item(BOW)),
    INVENTORY_COMPASS_SUMO(ItemBuilder.item(STICK).glow(true)),
    INVENTORY_COMPASS_FISHER(ItemBuilder.item(FISHING_ROD)),
    INVENTORY_COMPASS_MINERS(ItemBuilder.item(DIAMOND_PICKAXE).flag(ItemFlag.HIDE_ATTRIBUTES)),

    GADGET_HOOK_ARROW(ItemBuilder.item(BOW).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE).enchant(Enchantment.ARROW_INFINITE, 1).flag(ItemFlag.HIDE_ENCHANTS)),
    GADGET_HOOK_ARROW_ARROW(ItemBuilder.item(ARROW)),

    GADGET_GRAPPLING_HOOK(ItemBuilder.item(FISHING_ROD).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE)),

    LIGHT_GRAY_GLASS_PANE(ItemBuilder.item(STAINED_GLASS_PANE).damage(7)),
    DARK_GRAY_GLASS_PANE(ItemBuilder.item(STAINED_GLASS_PANE).damage(15)),
    LIME_GLASS_PANE(ItemBuilder.item(STAINED_GLASS_PANE).damage(5)),

    INVENTORY_PSERVER_PUBLIC(ItemBuilder.item(PAPER)),
    INVENTORY_PSERVER_PRIVATE(ItemBuilder.item(COMMAND)),

    INVENTORY_PSERVER_SLOT_EMPTY(ItemBuilder.item(STONE_BUTTON)),
    INVENTORY_PSERVER_SLOT_NOT_BOUGHT(ItemBuilder.item(FIREWORK_CHARGE).flag(ItemFlag.HIDE_POTION_EFFECTS)),

    PSERVER_MAIN_ITEM(ItemBuilder.item(COMMAND)),
    NEXT(ItemBuilder.item(ARROW)),
    PREV(ItemBuilder.item(ARROW)),
    PSERVER_OWN_MENU(ItemBuilder.item(COMMAND)),
    INVENTORY_PSERVER(ItemBuilder.item(COMMAND)),
    GAMESERVER_SELECTION_WOOLBATTLE(ItemBuilder.item(BOW)),
    INVENTORY_NEW_PSERVER(ItemBuilder.item(COMMAND)),
    PSERVER_SLOT(ItemBuilder.item(STAINED_GLASS_PANE).damage(5)),
    WORLD_PSERVER(ItemBuilder.item(GRASS)),
    GAME_PSERVER(ItemBuilder.item(DIAMOND_SWORD)),
    GAMESERVER_WOOLBATTLE(ItemBuilder.item(BOW)),
    PSERVER_DELETE(ItemBuilder.item(TNT).lore(Component.empty())),
    CONFIRM(ItemBuilder.item(INK_SACK).damage(10)),
    CANCEL(ItemBuilder.item(INK_SACK).damage(1)),
    START_PSERVER(ItemBuilder.item(INK_SACK).damage(2)),
    STOP_PSERVER(ItemBuilder.item(INK_SACK).damage(1)),
    PSERVER_PUBLIC(ItemBuilder.item(FIREWORK_CHARGE).set(FIREWORK_EXPLOSION, FireworkExplosion.builder().withColor(new Color(255, 255, 255)).build()).flag(ItemFlag.HIDE_POTION_EFFECTS)),

    PSERVER_PRIVATE(ItemBuilder.item(FIREWORK_CHARGE).set(FIREWORK_EXPLOSION, FireworkExplosion.builder().withColor(new Color(255, 0, 0)).build()).flag(ItemFlag.HIDE_POTION_EFFECTS)),

    ARROW_NEXT(ItemBuilder.item(ARROW)),

    ARROW_PREVIOUS(ItemBuilder.item(ARROW)),

    GAME_NOT_FOUND(ItemBuilder.item(BARRIER)),

    LOADING(ItemBuilder.item(BARRIER)),
    JUMPANDRUN_STOP(ItemBuilder.item(INK_SACK).damage(1)),
    ;

    private static final Key itemId = Key.key(Lobby.getInstance(), "item_id");
    private final ItemBuilder builder;
    private final String key = this.name();

    Item(ItemBuilder builder) {
        this.builder = builder;
    }

    public static ItemStack getItem(Item item, User user, Object... replacements) {
        return Item.getItem(item, user, replacements, new Object[0]);
    }

    public static ItemStack getItem(Item item, User user, Object[] replacements, Object... loreReplacements) {
        ItemBuilder builder = setItemId(item.getBuilder(), item.getItemId());
        Component name = Item.getDisplayName(item, user, replacements);
        builder.displayname(name);
        if (builder.lore().size() != 0) {
            builder.setLore(new ArrayList<>());
            builder.lore(Item.getLore(item, user, loreReplacements));
        }
        return builder.build();
    }

    public static String getItemId(Item item) {
        return Message.PREFIX_ITEM + item.getKey();
    }

    public static String getItemId(ItemStack item) {
        return getItemId(ItemBuilder.item(item));
    }

    public static String getItemId(ItemBuilder item) {
        return item.persistentDataStorage().get(itemId, PersistentDataTypes.STRING);
    }

    public static ItemBuilder setItemId(ItemBuilder b, String itemId) {
        return b.persistentDataStorage().iset(Item.itemId, PersistentDataTypes.STRING, itemId).builder();
    }

    public static Component getDisplayName(Item item, User user, Object... replacements) {
        return user.language().getMessage(Message.KEY_PREFIX + item.getItemId(), replacements);
    }

    public static Component getLore(Item item, User user, Object... loreReplacements) {
        return user.language().getMessage(Message.KEY_PREFIX + Message.PREFIX_ITEM + Message.PREFIX_LORE + item.getKey(), loreReplacements);
    }

    public static Item byGadget(Gadget gadget) {
        for (Item item : Item.values()) {
            if (item.getKey().startsWith("GADGET_") && item.getKey().substring(7).equals(gadget.name())) {
                return item;
            }
        }
        throw new IllegalArgumentException();
    }

    public ItemBuilder getBuilder() {
        return builder.clone();
    }

    public Component getDisplayName(User user) {
        return this.getDisplayName(user, new Object[0]);
    }

    public Component getDisplayName(User user, Object... replacements) {
        return Item.getDisplayName(this, user, replacements);
    }

    public ItemStack getItem(User user) {
        return Item.getItem(this, user);
    }

    public String getKey() {
        return this.key;
    }

    public ItemStack getItem(User user, Object... replacements) {
        return Item.getItem(this, user, replacements);
    }

    public ItemStack getItem(User user, String[] replacements, Object... loreReplacements) {
        return Item.getItem(this, user, replacements, loreReplacements);
    }

    public String getItemId() {
        return Item.getItemId(this);
    }
}
