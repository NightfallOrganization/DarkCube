/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemManager {

    public static final Key ITEM_ID = Key.key(WoolBattleBukkit.instance(), "item_id");

    public static void removeItems(WBUser user, Inventory invToRemoveFrom, ItemStack itemToRemove, int count) {
        if (WoolBattleBukkit.instance().ingame().enabled() && itemToRemove.getType() == Material.WOOL && user.woolSubtractDirection() == WoolSubtractDirection.RIGHT_TO_LEFT) {
            Map<Integer, ItemStack> leftOver = new HashMap<>();
            itemToRemove = new ItemStack(itemToRemove);
            itemToRemove.setAmount(1);
            var toDelete = count;
            var did = 0;
            for (var i = count - 1; i >= 0; i--) {
                do {
                    int last;
                    if ((last = ItemManager.last(invToRemoveFrom, itemToRemove, false)) == -1) {
                        itemToRemove.setAmount(toDelete);
                        leftOver.put(i, itemToRemove);
                        break;
                    }
                    var item = invToRemoveFrom.getItem(last);
                    var amount = item.getAmount();
                    if (amount <= toDelete) {
                        toDelete -= amount;
                        invToRemoveFrom.clear(last);
                        continue;
                    }
                    item.setAmount(amount - toDelete);
                    invToRemoveFrom.setItem(last, item);
                    toDelete = 0;
                } while (toDelete > 0);
                did++;
                if (did > 50) {
                    break;
                }
            }
        } else {
            for (var i = 0; i < count; i++) {
                invToRemoveFrom.removeItem(itemToRemove);
            }
        }
    }

    public static int last(Inventory inv, ItemStack item, boolean withAmount) {
        if (item == null) {
            return -1;
        }
        var invContents = inv.getContents();
        for (var i = invContents.length - 1; i >= 0; i--) {
            if (invContents[i] != null && (withAmount ? item.equals(invContents[i]) : item.isSimilar(invContents[i]))) {
                return i;
            }
        }
        return -1;
    }

    public static ItemStack getItem(Item item, WBUser user, Object... replacements) {
        return ItemManager.getItem(item, user, replacements, new Object[0]);
    }

    public static ItemStack getItem(Item item, WBUser user, Object[] replacements, Object... loreReplacements) {
        var builder = item.getBuilder().persistentDataStorage().iset(ITEM_ID, PersistentDataTypes.STRING, item.getItemId()).builder();
        var language = user.getLanguage();
        var name = ItemManager.getDisplayName(item, language, replacements);
        builder.displayname(name);
        if (language.containsMessage(Message.KEY_PREFIX + Message.ITEM_PREFIX + Message.LORE_PREFIX + item.name())) {
            builder.lore(Message.getMessage(Message.ITEM_PREFIX + Message.LORE_PREFIX + item.name(), language, loreReplacements));
        }
        return builder.build();
    }

    public static void setId(ItemBuilder b, Key key, String id) {
        b.persistentDataStorage().set(key, PersistentDataTypes.STRING, id);
    }

    public static String getId(ItemStack s, Key key) {
        return getId(ItemBuilder.item(s), key);
    }

    public static String getId(ItemBuilder item, Key key) {
        return getNBTValue(item, key);
    }

    public static String getItemId(Item item) {
        return Message.ITEM_PREFIX + item.getKey();
    }

    public static String getItemId(ItemStack item) {
        return ItemManager.getId(item, ITEM_ID);
    }

    public static String getItemId(ItemBuilder item) {
        return getId(item, ITEM_ID);
    }

    private static String getNBTValue(ItemBuilder builder, Key key) {
        return builder.persistentDataStorage().get(key, PersistentDataTypes.STRING);
    }

    public static Component getDisplayName(Item item, Language language, Object... replacements) {
        return Message.getMessage(ItemManager.getItemId(item), language, replacements);
    }
}
