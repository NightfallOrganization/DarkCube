/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.impl.inventory;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.impl.adventure.AdventureUtils;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.impl.inventory.InventoryTemplateImpl;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.minestom.server.entity.Player;

public class MinestomInventoryTemplate extends InventoryTemplateImpl<Player> {
    public MinestomInventoryTemplate(@NotNull Key key, @NotNull InventoryType type, int size) {
        super(key, type, size);
    }

    @Override
    protected @NotNull User user(@NotNull Player player) {
        return UserAPI.instance().user(player.getUuid());
    }

    @Nullable
    @Override
    protected Player onlinePlayer(@NotNull Object player) {
        return MinestomInventoryUtils.player(player);
    }

    @NotNull
    @Override
    protected Inventory open(@Nullable Component title, @Nullable Player player) {
        var inventory = new MinestomTemplateInventory(title, (MinestomInventoryType) type, this, player);
        inventory.open(player);
        return inventory;
    }

    @Override
    protected @Nullable Object tryConvertTitle(@NotNull Object title) {
        if (title instanceof net.kyori.adventure.text.Component component) {
            return AdventureUtils.convert(component);
        }
        return null;
    }
}
