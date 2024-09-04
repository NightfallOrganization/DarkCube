/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.enums;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static eu.darkcube.system.server.item.component.ItemComponent.ATTRIBUTE_MODIFIERS;
import static eu.darkcube.system.util.Language.lastStyle;
import static org.bukkit.Material.*;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.TextComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.utils.message.Message;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.components.AttributeList;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;

public enum InventoryItems implements ItemFactory {

    // Shop-Sound
    // INVENTORY_SHOP_SOUNDS_STANDARD(item(MUSIC_DISC_WARD).hideJukeboxPlayableTooltip(), 0),
    // INVENTORY_SHOP_SOUNDS_WOOLBATTLE(item(MUSIC_DISC_MALL).hideJukeboxPlayableTooltip(), 1500000),
    // INVENTORY_SHOP_SOUNDS_ARMADILLO(item(MUSIC_DISC_13).hideJukeboxPlayableTooltip(), 15000),
    // INVENTORY_SHOP_SOUNDS_ARMADILLO_HIGH(item(MUSIC_DISC_13).hideJukeboxPlayableTooltip(), 15000),
    // INVENTORY_SHOP_SOUNDS_SCAFFOLDING(item(MUSIC_DISC_CREATOR_MUSIC_BOX).hideJukeboxPlayableTooltip(), 500000),
    // INVENTORY_SHOP_SOUNDS_SCAFFOLDING_HIGH(item(MUSIC_DISC_CREATOR_MUSIC_BOX).hideJukeboxPlayableTooltip(), 50000),

    // Ability
    INVENTORY_ABILITY_ITEM_AUTO_EAT(item(RABBIT_STEW)),
    INVENTORY_ABILITY_ITEM_FLY(item(FEATHER)),
    INVENTORY_ABILITY_ITEM_SPEED(item(DIAMOND_BOOTS).set(ATTRIBUTE_MODIFIERS, AttributeList.EMPTY.withTooltip(false))),

    ;

    public static final DataKey<String> ITEM_ID = DataKey.of(Key.key(Miners.getInstance(), "item_id"), PersistentDataTypes.STRING);
    private final String key;
    private final ItemBuilder builder;
    private Integer cost;

    InventoryItems(ItemBuilder builder, Integer cost) {
        this.builder = builder;
        this.cost = cost;
        key = this.name();
    }

    InventoryItems(ItemBuilder builder) {
        this.builder = builder;
        key = this.name();
    }

    public Integer getCost() {
        return cost;
    }

    public @NotNull String key() {
        return key;
    }

    public String itemID() {
        return Message.PREFIX_INVENTORY_ITEM + key();
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

    public @NotNull ItemBuilder getItem(@NotNull User user, Object... displayNameReplacements) {
        return getItem(user, displayNameReplacements, true);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user, boolean storeItemId) {
        return getItem(user, new Object[0], storeItemId);
    }

    public @NotNull ItemBuilder getItem(@NotNull User user, Object[] displayNameReplacements, boolean storeItemId) {
        ItemBuilder builder = this.builder();
        if (storeItemId) {
            builder.persistentDataStorage().set(ITEM_ID, itemID());
        }
        Language language = user.language();
        Component name;
        try {
            name = Message.getMessage(this.itemID(), language, displayNameReplacements);
        } catch (Throwable t) {
            System.out.println(this.itemID());
            t.printStackTrace();
            throw t;
        }
        builder.displayname(name);

        if (language.containsMessage(Message.KEY_PREFIX + Message.PREFIX_INVENTORY_ITEM + Message.PREFIX_LORE + key())) {
            Component lore = Message.getMessage(Message.PREFIX_INVENTORY_ITEM + Message.PREFIX_LORE + key(), language);

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
