/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import static eu.darkcube.system.util.Language.lastStyle;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.ChatColorUtil;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class ItemManager {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private final Key itemId;
    private final CommonWoolBattleApi api;

    public ItemManager(CommonWoolBattleApi api) {
        this.api = api;
        itemId = Key.key(api, "item_id");
    }

    public static ItemManager instance() {
        return ItemManagerImpl.MANAGER;
    }

    public ItemBuilder getItem(Item item, WBUser user) {
        return getItem(item, user, EMPTY_ARRAY);
    }

    public ItemBuilder getItem(Item item, User user) {
        var wbUser = api.user(user);
        if (wbUser == null) {
            throw new IllegalStateException("Tried to query User " + user.name() + "[" + user.uniqueId() + "], but couldn't find WBUser counterpart");
        }
        return getItem(item, wbUser);
    }

    public ItemBuilder getItem(Item item, User user, Object... replacements) {
        var wbUser = api.user(user);
        if (wbUser == null) {
            throw new IllegalStateException("Tried to query User " + user.name() + "[" + user.uniqueId() + "], but couldn't find WBUser counterpart");
        }
        return getItem(item, wbUser, replacements);
    }

    public ItemBuilder getItem(Item item, User user, Object[] replacements, Object... loreReplacements) {
        var wbUser = api.user(user);
        if (wbUser == null) {
            throw new IllegalStateException("Tried to query User " + user.name() + "[" + user.uniqueId() + "], but couldn't find WBUser counterpart");
        }
        return getItem(item, wbUser, replacements, loreReplacements);
    }

    public ItemBuilder getItem(Item item, WBUser user, Object... replacements) {
        return getItem(item, user, replacements, EMPTY_ARRAY);
    }

    public ItemBuilder getItem(Item item, WBUser user, Object[] replacements, Object... loreReplacements) {
        var builder = item.builder();
        if (!(item instanceof CommonItem commonItem) || commonItem.storeIdOnItem()) {
            setId(builder, itemId, item.itemId());
        }
        var language = user.language();
        var name = Messages.getMessage(item.itemId(), language, replacements);
        builder.displayname(name);
        if (language.containsMessage(Messages.KEY_PREFIX + Messages.ITEM_PREFIX + Messages.LORE_PREFIX + item.key())) {
            var lore = Messages.getMessage(Messages.ITEM_PREFIX + Messages.LORE_PREFIX + item.key(), language, loreReplacements);
            var serialized = LegacyComponentSerializer.legacySection().serialize(lore);
            var loreStringLines = serialized.split("\n");
            Style lastStyle = null;
            for (var loreStringLine : loreStringLines) {
                var loreLine = LegacyComponentSerializer.legacySection().deserialize(loreStringLine);
                if (lastStyle != null) {
                    var newStyle = loreLine.style().merge(lastStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
                    loreLine = loreLine.style(newStyle);
                }

                lastStyle = lastStyle(loreLine);
                builder.lore(loreLine);
            }
        }
        return builder;
    }

    public @Nullable String getItemId(@NotNull ItemBuilder item) {
        return getId(item, itemId);
    }

    public @Nullable String getId(@NotNull ItemBuilder item, @NotNull Key key) {
        return item.persistentDataStorage().get(key, PersistentDataTypes.STRING);
    }

    public void setId(@NotNull ItemBuilder item, @NotNull Key key, @NotNull String id) {
        item.persistentDataStorage().set(key, PersistentDataTypes.STRING, id);
    }
}
