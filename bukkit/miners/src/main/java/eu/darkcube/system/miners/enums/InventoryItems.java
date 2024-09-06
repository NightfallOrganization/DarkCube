/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.enums;

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
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.utils.message.Message;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;

public enum InventoryItems implements ItemFactory {

    // Lobby-Hotbar
    HOTBAR_ITEM_ABILITIES(item(OMINOUS_BOTTLE).hideAdditionalTooltip(), "hotbar_item_abilities"),
    HOTBAR_ITEM_TEAMS(item(BOOK), "hotbar_item_teams"),
    HOTBAR_ITEM_SETTINGS(item(COMPARATOR), "hotbar_item_settings"),
    HOTBAR_ITEM_SHOP(item(ENDER_CHEST), "hotbar_item_shop"),
    HOTBAR_ITEM_VOTING(item(PAPER), "hotbar_item_voting"),

    //Teams
    INVENTORY_TEAM_RED(item(RED_WOOL), "inventory_team_red"),
    INVENTORY_TEAM_BLUE(item(BLUE_WOOL), "inventory_team_blue"),
    INVENTORY_TEAM_GREEN(item(GREEN_WOOL), "inventory_team_green"),
    INVENTORY_TEAM_YELLOW(item(YELLOW_WOOL), "inventory_team_yellow"),
    INVENTORY_TEAM_WHITE(item(WHITE_WOOL), "inventory_team_white"),
    INVENTORY_TEAM_BLACK(item(BLACK_WOOL), "inventory_team_black"),
    INVENTORY_TEAM_PURPLE(item(PURPLE_WOOL), "inventory_team_purple"),
    INVENTORY_TEAM_ORANGE(item(ORANGE_WOOL), "inventory_team_orange"),

    // Ability
    INVENTORY_ABILITY_ITEM_DIGGER(item(IRON_PICKAXE), "inventory_ability_item_digger"),
    INVENTORY_ABILITY_ITEM_SPEEDSTER(item(LEATHER_BOOTS), "inventory_ability_item_speedster"),
    // INVENTORY_ABILITY_ITEM_FLY(item(FEATHER)),
    // INVENTORY_ABILITY_ITEM_SPEED(item(DIAMOND_BOOTS).set(ATTRIBUTE_MODIFIERS, AttributeList.EMPTY.withTooltip(false))),

    ;

    public static final DataKey<String> ITEM_ID = DataKey.of(Key.key(Miners.getInstance(), "item_id"), PersistentDataTypes.STRING);
    private final String key;
    private final ItemBuilder builder;
    private Integer cost;
    private String id;

    InventoryItems(ItemBuilder builder, Integer cost) {
        this.builder = builder;
        this.cost = cost;
        key = this.name();
    }

    InventoryItems(ItemBuilder builder, String id) {
        this.builder = builder;
        key = this.name();
        this.id = id;
    }

    InventoryItems(ItemBuilder builder) {
        this.builder = builder;
        key = this.name();
    }

    public String getID() {
        return id;
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
