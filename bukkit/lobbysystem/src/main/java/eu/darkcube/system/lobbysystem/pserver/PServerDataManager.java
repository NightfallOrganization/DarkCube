/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.pserver;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceTask;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;

import java.util.concurrent.ExecutionException;

public class PServerDataManager {

    public static ItemBuilder getDisplayItem(User user, UniqueId pserverId) {
        if (pserverId != null) {
            try {
                PServerExecutor ps = PServerProvider.instance().pserver(pserverId).get();
                Type type = ps.type().get();

                if (type == Type.GAMEMODE) {
                    ItemBuilder b = getDisplayItemGamemode(user, ps.taskName().get());
                    b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
                    return b;
                }
                ItemBuilder b = ItemBuilder.item(SkullCache.getCachedItem(user.getUniqueId()));
                b.displayname(Item.WORLD_PSERVER.getDisplayName(user));
                b.lore(Component.text("ID: " + pserverId).color(NamedTextColor.GRAY));
                return b;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static ItemBuilder getDisplayItemGamemode(User user, String task) {
        if (task == null) {
            return null;
        }
        if (Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(task)) {
            ServiceTask stask = InjectionLayer.boot().instance(ServiceTaskProvider.class).serviceTask(task);
            if (stask != null) {
                return new InventoryGameServerSelectionWoolBattle.Func().apply(user, stask);
            }
        }
        ItemBuilder b = ItemBuilder.item(Material.BARRIER);
        b.displayname(Component.text("Task not found: " + task).color(NamedTextColor.RED));
        return b;
    }
}
