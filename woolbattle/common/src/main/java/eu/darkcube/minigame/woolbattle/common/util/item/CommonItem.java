/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import static eu.darkcube.system.util.Language.lastStyle;

import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface CommonItem extends Item {
    @Override
    default @NotNull ItemBuilder getItem(@NotNull WBUser user) {
        return getItem(user.user());
    }

    @Override
    default @NotNull ItemBuilder getItem(@NotNull WBUser user, Object @NotNull ... replacements) {
        return getItem(user.user(), replacements);
    }

    @Override
    default @NotNull ItemBuilder getItem(@NotNull WBUser user, Object @NotNull [] replacements, Object @NotNull ... loreReplacements) {
        return getItem(user.user(), replacements, loreReplacements);
    }

    @Override
    @NotNull
    default ItemBuilder getItem(@NotNull User user, @NotNull Object @NotNull [] replacements, @NotNull Object @NotNull ... loreReplacements) {
        return getItem(user, replacements, loreReplacements, this.storeIdOnItem(), this::mapItem);
    }

    default ItemBuilder getItem(@NotNull User user, @NotNull Object @NotNull [] replacements, @NotNull Object @NotNull [] loreReplacements, boolean storeIdOnItem, BiFunction<User, ItemBuilder, ItemBuilder> mapper) {
        var builder = this.builder();
        if (storeIdOnItem) {
            ItemManager.instance().setItemId(builder, this.itemId());
        }
        var language = user.language();
        Component name;
        try {
            name = Messages.getMessage(this.itemId(), language, replacements);
        } catch (MissingFormatArgumentException ex) {
            throw new RuntimeException("Error querying item name message " + this.itemId() + " with " + Arrays.toString(replacements) + " and " + Arrays.toString(loreReplacements), ex);
        }
        builder.displayname(name);
        if (language.containsMessage(Messages.KEY_PREFIX + Messages.ITEM_PREFIX + Messages.LORE_PREFIX + this.key())) {
            Component lore;
            try {
                lore = Messages.getMessage(Messages.ITEM_PREFIX + Messages.LORE_PREFIX + this.key(), language, loreReplacements);
            } catch (MissingFormatArgumentException ex) {
                throw new RuntimeException("Error querying item lore message " + this.itemId() + " with " + Arrays.toString(replacements) + " and " + Arrays.toString(loreReplacements), ex);
            }
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
        return mapper.apply(user, builder);
    }

    @Override
    @NotNull
    default ItemBuilder getItem(@NotNull User user, @NotNull Object @NotNull ... replacements) {
        return getItem(user, replacements, defaultLoreReplacements(user));
    }

    @Override
    @NotNull
    default ItemBuilder getItem(@NotNull User user) {
        return getItem(user, defaultReplacements(user));
    }

    @Override
    default @NotNull ItemBuilder createItem(@NotNull User user) {
        return getItem(user);
    }

    @Override
    default @NotNull String itemId() {
        return Messages.ITEM_PREFIX + key();
    }

    default boolean storeIdOnItem() {
        return true;
    }

    default ItemBuilder mapItem(User user, ItemBuilder item) {
        return item;
    }

    default Object[] defaultReplacements(User user) {
        return CommonItemImpl.EMPTY_ARRAY;
    }

    default Object[] defaultLoreReplacements(User user) {
        return CommonItemImpl.EMPTY_ARRAY;
    }

    default CommonItem defaultLoreReplacements(@NotNull Object @NotNull ... loreReplacements) {
        return new WrapperItem(this) {
            @Override
            public Object[] defaultLoreReplacements(User user) {
                return loreReplacements;
            }
        };
    }

    default CommonItem defaultReplacements(@NotNull Object @NotNull ... replacements) {
        return new WrapperItem(this) {
            @Override
            public Object[] defaultReplacements(User user) {
                return replacements;
            }
        };
    }

    default CommonItem modify(BiConsumer<User, ItemBuilder> modifier) {
        return map((user, itemBuilder) -> {
            modifier.accept(user, itemBuilder);
            return itemBuilder;
        });
    }

    default CommonItem map(BiFunction<User, ItemBuilder, ItemBuilder> mapper) {
        return new WrapperItem(this) {
            @Override
            public ItemBuilder mapItem(User user, ItemBuilder item) {
                return mapper.apply(user, item);
            }
        };
    }

    default CommonItem withoutId() {
        return new WrapperItem(this) {
            @Override
            public boolean storeIdOnItem() {
                return false;
            }
        };
    }
}

class CommonItemImpl {
    static final Object[] EMPTY_ARRAY = new Object[0];
}