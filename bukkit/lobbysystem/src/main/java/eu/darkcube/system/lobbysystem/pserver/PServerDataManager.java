/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.pserver;

import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.lobbysystem.util.gameregistry.RegistryEntry;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;

public class PServerDataManager {

    public static ItemBuilder getDisplayItem(User user, UniqueId pserverId) {
        if (pserverId != null) {
            var ps = PServerProvider.instance().pserver(pserverId).join();
            var type = ps.type().join();

            if (type == Type.GAMEMODE) {
                var entry = ps.storage().get(InventoryGameServerSelection.SERVICE, InventoryGameServerSelection.SERVICE_TYPE);
                var b = getDisplayItemGamemode(user, entry);
                b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
                return b;
            }
            var b = ItemBuilder.item(SkullCache.getCachedItem(user.uniqueId()));
            b.displayname(Item.WORLD_PSERVER.getDisplayName(user));
            b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
            return b;
        }
        return null;
    }

    public static ItemBuilder getDisplayItemGamemode(@NotNull User user, @Nullable RegistryEntry entry) {
        if (entry == null) {
            entry = new RegistryEntry("unknown", "unconverted", new JsonObject());
        }

        if (Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(entry.taskName())) {
            return new InventoryGameServerSelectionWoolBattle.Func().apply(user, entry);
        }
        var b = ItemBuilder.item(Material.BARRIER);
        b.displayname(Component.text("Task not found: " + entry.taskName()).color(NamedTextColor.RED));
        return b;
    }
}
