/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.pserver;

import eu.cloudnetservice.driver.document.Document;
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
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;

public class PServerDataManager {

    public static ItemBuilder getDisplayItem(User user, UniqueId pserverId) {
        if (pserverId != null) {
            PServerExecutor ps = PServerProvider.instance().pserver(pserverId).join();
            Type type = ps.type().join();

            if (type == Type.GAMEMODE) {
                var entry = ps.storage().get(InventoryGameServerSelection.SERVICE, InventoryGameServerSelection.SERVICE_TYPE);
                ItemBuilder b = getDisplayItemGamemode(user, entry);
                b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
                return b;
            }
            ItemBuilder b = ItemBuilder.item(SkullCache.getCachedItem(user.uniqueId()));
            b.displayname(Item.WORLD_PSERVER.getDisplayName(user));
            b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
            return b;
        }
        return null;
    }

    public static ItemBuilder getDisplayItemGamemode(@NotNull User user, @Nullable RegistryEntry entry) {
        if (entry == null) {
            entry = new RegistryEntry("unknown", "unconverted", Document.newJsonDocument());
        }

        if (Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(entry.taskName())) {
            return new InventoryGameServerSelectionWoolBattle.Func().apply(user, entry);
        }
        ItemBuilder b = ItemBuilder.item(Material.BARRIER);
        b.displayname(Component.text("Task not found: " + entry.taskName()).color(NamedTextColor.RED));
        return b;
    }
}
