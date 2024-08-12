/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static eu.darkcube.system.util.Language.lastStyle;
import static org.bukkit.Material.*;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.TextComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.entity.Player;

public enum Items implements ItemFactory {
    INVENTORY_SHOP_STEAK(item(COOKED_BEEF)),
    INVENTORY_SHOP_SHEARS(item(SHEARS)),
    INVENTORY_SHOP_SOUNDS(item(MUSIC_DISC_PIGSTEP)),

    ;

    public static final DataKey<String> ITEM_ID = DataKey.of(Key.key(WoolMania.getInstance(), "item_id"), PersistentDataTypes.STRING);
    private final String key;
    private final ItemBuilder builder;

    Items(ItemBuilder builder) {
        this.builder = builder;
        key = this.name();
    }

    public @NotNull String key() {
        return key;
    }

    public String itemID() {
        return Message.PREFIX_ITEM + key();
    }

    public @NotNull ItemBuilder builder() {
        return builder.clone();
    }

    public ItemBuilder createItem(Player player) {
        return createItem(UserAPI.instance().user(player.getUniqueId()));
    }

    @Override
    public @NotNull ItemBuilder createItem(@NotNull User user) {
        return getItem(user);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user) {
        return getItem(user, true);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user, boolean storeItemId) {
        ItemBuilder builder = this.builder();
        if (storeItemId) {
            builder.persistentDataStorage().set(ITEM_ID, itemID());
        }
        Language language = user.language();
        Component name = Message.getMessage(this.itemID(), language);
        builder.displayname(name);

        if (language.containsMessage(Message.KEY_PREFIX + Message.PREFIX_ITEM + Message.PREFIX_LORE + key())) {
            Component lore = Message.getMessage(Message.PREFIX_ITEM + Message.PREFIX_LORE + key(), language);

            String serialized = LegacyComponentSerializer.legacySection().serialize(lore);
            String[] loreStringLines = serialized.split("\n");
            Style lastStyle = null;
            for (String loreStringLine : loreStringLines) {
                TextComponent loreLine = LegacyComponentSerializer.legacySection().deserialize(loreStringLine);
                if (lastStyle != null) {
                    Style newStyle = loreLine.style().merge(lastStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
                    loreLine = loreLine.style(newStyle);
                }

                lastStyle = lastStyle(loreLine);
                builder.lore(loreLine);
            }
        }
        return builder;
    }

    @Nullable
    public static String getItemID(ItemBuilder item) {
        return item.persistentDataStorage().get(ITEM_ID);
    }
}
