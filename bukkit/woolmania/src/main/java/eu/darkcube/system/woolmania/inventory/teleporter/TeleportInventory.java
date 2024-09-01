/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.teleporter;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;
import static eu.darkcube.system.woolmania.enums.Names.TELEPORTER;
import static eu.darkcube.system.woolmania.enums.Sounds.NO;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportInventory implements TemplateInventoryListener {
    private final InventoryTemplate inventoryTemplate;

    public String getKeyValue() {
        return inventoryTemplate.key().value();
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public TeleportInventory() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "teleporter"), 27);
        inventoryTemplate.title(TELEPORTER.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        DarkCubeInventoryTemplates.Paged.configure3x9(inventoryTemplate);

        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_3);
        inventoryTemplate.setItem(1, 11, INVENTORY_TELEPORT_NONE);
        inventoryTemplate.setItem(1, 13, INVENTORY_TELEPORT_SPAWN);
        inventoryTemplate.setItem(1, 15, INVENTORY_TELEPORT_HALLS);

        inventoryTemplate.addListener(this);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String itemId = InventoryItems.getItemID(item);
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);

        if (itemId == null) return;

        if (itemId.equals(INVENTORY_TELEPORT_NONE.itemID())) {
            NO.playSound(player);
        } else if (itemId.equals(INVENTORY_TELEPORT_SPAWN.itemID())) {
            woolManiaPlayer.teleportToSpawn();
        } else if (itemId.equals(INVENTORY_TELEPORT_HALLS.itemID())) {
            WoolMania.getInstance().getTeleportHallsInventory().openInventory(player);
        }
    }
}
