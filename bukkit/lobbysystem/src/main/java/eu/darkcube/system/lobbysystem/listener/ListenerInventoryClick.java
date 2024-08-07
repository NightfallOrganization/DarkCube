/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.InventorySettings;
import eu.darkcube.system.lobbysystem.inventory.InventorySumo;
import eu.darkcube.system.lobbysystem.inventory.InventoryWoolBattle;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServer;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerOwn;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListenerInventoryClick extends BaseListener {

    @EventHandler public void handle(InventoryClickEvent e) {
        var p = (Player) e.getWhoClicked();
        var user = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
        if (user.isBuildMode()) {
            return;
        }
        e.setCancelled(true);
        var item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        var itemid = Item.getItemId(item);
        language:
        {
            var languageId = ItemBuilder.item(item).persistentDataStorage().get(InventorySettings.language, PersistentDataTypes.STRING);
            if (languageId == null || languageId.isEmpty()) break language;
            var language = Language.fromString(languageId);
            var i = 0;
            for (; i < Language.values().length; i++) {
                if (Language.values()[i] == language) {
                    i++;
                    break;
                }
            }
            i %= Language.values().length;
            language = Language.values()[i];
            user.user().language(language);
            user.disableSounds(true);
            Lobby.getInstance().setItems(user);
            user.setOpenInventory(new InventorySettings(user.user()));
            p.setFlying(true);
            user.disableSounds(false);
        }
        if (itemid == null || itemid.isEmpty()) {
            return;
        }
        var close = false;
        if (itemid.equals(Item.INVENTORY_COMPASS_SPAWN.getItemId())) {
            user.teleport(Lobby.getInstance().getDataManager().getSpawn());
            close = true;
        } else if (itemid.equals(Item.INVENTORY_COMPASS_WOOLBATTLE.getItemId()) && !(user.getOpenInventory() instanceof InventoryWoolBattle)) {
            user.teleport(Lobby.getInstance().getDataManager().getWoolBattleSpawn());
            close = true;
        } else if (itemid.equals(Item.INVENTORY_COMPASS_JUMPANDRUN.getItemId())) {
            user.teleport(Lobby.getInstance().getDataManager().getJumpAndRunSpawn());
            close = true;
        } else if (itemid.equals(Item.INVENTORY_COMPASS_SUMO.getItemId()) && !(user.getOpenInventory() instanceof InventorySumo)) {
            user.teleport(Lobby.getInstance().getDataManager().getSumoSpawn());
            close = true;
        } else if (itemid.equals(Item.INVENTORY_COMPASS_FISHER.getItemId())) {
            user.teleport(Lobby.getInstance().getDataManager().getFisherSpawn());
            close = true;
        } else if (itemid.equals(Item.INVENTORY_SETTINGS_ANIMATIONS_ON.getItemId())) {
            user.setAnimations(false);
            user.setOpenInventory(new InventorySettings(user.user()));
        } else if (itemid.equals(Item.INVENTORY_SETTINGS_ANIMATIONS_OFF.getItemId())) {
            user.setAnimations(true);
            user.setOpenInventory(new InventorySettings(user.user()));
        } else if (itemid.equals(Item.INVENTORY_SETTINGS_SOUNDS_ON.getItemId())) {
            user.setSounds(false);
            user.setOpenInventory(new InventorySettings(user.user()));
        } else if (itemid.equals(Item.INVENTORY_SETTINGS_SOUNDS_OFF.getItemId())) {
            user.setSounds(true);
            user.setOpenInventory(new InventorySettings(user.user()));
        }

        // PagedInventories
        var inv = user.getOpenInventory();

        if (inv instanceof InventoryConfirm cinv) {
            if (itemid.equals(Item.CONFIRM.getItemId())) {
                cinv.onConfirm.run();
            } else if (itemid.equals(Item.CANCEL.getItemId())) {
                cinv.onCancel.run();
            }
        }

        if (inv instanceof InventoryPServer) {
            if (itemid.equals(Item.INVENTORY_PSERVER_PRIVATE.getItemId())) {
                user.setOpenInventory(new InventoryPServerOwn(user.user()));
            }
        } else if (inv instanceof InventoryPServerOwn) {
            if (itemid.equals(Item.INVENTORY_PSERVER_PUBLIC.getItemId())) {
                user.setOpenInventory(new InventoryPServer(user));
            }
        }
        if (close) {
            p.closeInventory();
        }
    }
}
