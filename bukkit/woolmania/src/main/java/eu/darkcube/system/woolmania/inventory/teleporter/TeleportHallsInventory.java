/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.teleporter;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;
import static eu.darkcube.system.woolmania.enums.Names.ZINUS;

import java.util.function.Function;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryMask;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import eu.darkcube.system.woolmania.enums.hall.Halls;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportHallsInventory implements TemplateInventoryListener {
    private final InventoryTemplate inventoryTemplate;
    private static final String MASK = """
            .........
            .........
            .#######.
            .#######.
            .........
            """;

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public TeleportHallsInventory() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "teleporter_halls"), 45);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_TELEPORT_HALLS);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();

        // WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        // Hall hall = woolManiaPlayer.getHall();

        content.addStaticItem(getDisplayItem(INVENTORY_TELEPORT_HALLS_HALL_1, Halls.HALL1));
        content.addStaticItem(getDisplayItem(INVENTORY_TELEPORT_HALLS_HALL_2, Halls.HALL2));
        content.addStaticItem(getDisplayItem(INVENTORY_TELEPORT_HALLS_HALL_3, Halls.HALL3));

        inventoryTemplate.addListener(this);
    }

    private Function<User, Object> getDisplayItem(InventoryItems item, Halls hall) {
        return user -> {
            Player player = Bukkit.getPlayer(user.uniqueId());
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);

            if (woolManiaPlayer.isHallUnlocked(hall)) {
                return item.getItem(user);
            } else {
                return item.getItem(user).lore(Message.HALL_COST.getMessage(user, item.getCost()));
            }

        };
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String itemId = InventoryItems.getItemID(item);
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        if (itemId == null) return;

        if (itemId.equals(INVENTORY_TELEPORT_HALLS_HALL_1.itemID())) {
            woolManiaPlayer.teleportTo(Halls.HALL1);
        }

        if (itemId.equals(INVENTORY_TELEPORT_HALLS_HALL_2.itemID())) {
            woolManiaPlayer.teleportTo(Halls.HALL2);
        }

        if (itemId.equals(INVENTORY_TELEPORT_HALLS_HALL_3.itemID())) {
            woolManiaPlayer.teleportTo(Halls.HALL3);
        }
    }
}
