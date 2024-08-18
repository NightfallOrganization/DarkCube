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
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum InventoryItems implements ItemFactory {
    //Shop-Inventar
    INVENTORY_SHOP_FOOD(item(COOKED_BEEF)),
    INVENTORY_SHOP_GADGETS(item(FISHING_ROD)),
    INVENTORY_SHOP_SOUNDS(item(MUSIC_DISC_PIGSTEP).hideJukeboxPlayableTooltip()),

    //Shop-Food
    INVENTORY_SHOP_FOOD_CARROT(item(ItemBuilder.item(Material.CARROT)), 1000),
    INVENTORY_SHOP_FOOD_MELON(item(ItemBuilder.item(MELON)), 5000),
    INVENTORY_SHOP_FOOD_STEAK(item(ItemBuilder.item(COOKED_BEEF)), 10000),
    INVENTORY_SHOP_FOOD_DIAMOND(item(ItemBuilder.item(Material.DIAMOND)), 20000),

    //Shop-Gadgets
    INVENTORY_SHOP_GADGETS_GRENADE(item(SNOWBALL), 5000),

    //Shop-Sound
    INVENTORY_SHOP_SOUNDS_STANDARD(item(MUSIC_DISC_WARD).hideJukeboxPlayableTooltip(), 0),
    INVENTORY_SHOP_SOUNDS_WOOLBATTLE(item(MUSIC_DISC_MALL).hideJukeboxPlayableTooltip(),15000),

    //Teleport-Inventar
    INVENTORY_TELEPORT_SPAWN(item(NETHER_STAR)),
    INVENTORY_TELEPORT_HALLS(item(BARREL)),
    INVENTORY_TELEPORT_NONE(item(FIREWORK_STAR)),

    //Teleport-Halls
    INVENTORY_TELEPORT_HALLS_HALL_1(item(WHITE_SHULKER_BOX)),
    INVENTORY_TELEPORT_HALLS_HALL_2(item(ORANGE_SHULKER_BOX)),
    INVENTORY_TELEPORT_HALLS_HALL_3(item(MAGENTA_SHULKER_BOX)),

    ;

    public static final DataKey<String> ITEM_ID = DataKey.of(Key.key(WoolMania.getInstance(), "item_id"), PersistentDataTypes.STRING);
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
        Component name = Message.getMessage(this.itemID(), language, displayNameReplacements);
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
