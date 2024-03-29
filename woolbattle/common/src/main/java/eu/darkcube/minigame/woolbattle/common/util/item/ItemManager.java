/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class ItemManager {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private final Key itemId;

    public ItemManager(WoolBattleApi api) {
        itemId = new Key(api, "item_id");
    }

    public static ItemManager instance() {
        return ItemManagerImpl.MANAGER;
    }

    public ItemBuilder getItem(Item item, WBUser user) {
        return getItem(item, user, EMPTY_ARRAY);
    }

    public ItemBuilder getItem(Item item, WBUser user, Object... replacements) {
        return getItem(item, user, replacements, EMPTY_ARRAY);
    }

    public ItemBuilder getItem(Item item, WBUser user, Object[] replacements, Object... loreReplacements) {
        var builder = item.builder();
        setId(builder, itemId, item.itemId());
        var language = user.language();
        var name = Messages.getMessage(item.itemId(), language, replacements);
        builder.displayname(name);
        if (language.containsMessage(Messages.KEY_PREFIX + Messages.ITEM_PREFIX + Messages.LORE_PREFIX + item.key())) {
            builder.lore(Messages.getMessage(Messages.ITEM_PREFIX + Messages.LORE_PREFIX + item.key(), language, loreReplacements));
        }
        return builder;
    }

    public String getItemId(ItemBuilder item) {
        return getId(item, itemId);
    }

    public String getId(ItemBuilder item, Key key) {
        return item.persistentDataStorage().get(key, PersistentDataTypes.STRING);
    }

    public void setId(ItemBuilder item, Key key, String id) {
        item.persistentDataStorage().set(key, PersistentDataTypes.STRING, id);
    }
}
