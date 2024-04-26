/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface CommonItem extends Item {
    @Override
    default ItemBuilder getItem(WBUser user) {
        return ItemManager.instance().getItem(this, user);
    }

    @Override
    default ItemBuilder getItem(WBUser user, Object... replacements) {
        return ItemManager.instance().getItem(this, user, replacements);
    }

    @Override
    default ItemBuilder getItem(WBUser user, Object[] replacements, Object... loreReplacements) {
        return ItemManager.instance().getItem(this, user, replacements, loreReplacements);
    }

    @Override
    default String itemId() {
        return Messages.ITEM_PREFIX + key();
    }

    @Override
    default @NotNull ItemBuilder createItem(@NotNull User user) {
        return ItemManager.instance().getItem(this, user);
    }
}
