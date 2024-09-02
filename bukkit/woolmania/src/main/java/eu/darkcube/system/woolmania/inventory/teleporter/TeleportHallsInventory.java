/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.teleporter;

import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_TELEPORT_HALLS;
import static eu.darkcube.system.woolmania.enums.Names.ZINUS;
import static eu.darkcube.system.woolmania.enums.Sounds.BUY;
import static eu.darkcube.system.woolmania.enums.Sounds.NO;
import static eu.darkcube.system.woolmania.util.message.Message.*;

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
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Halls;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportHallsInventory implements TemplateInventoryListener {
    private final InventoryTemplate inventoryTemplate;
    private static final String MASK = """
            .........
            .........
            .###.###.
            .###.###.
            .........
            """;

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    private static final DataKey<Halls> HALL = DataKey.of(Key.key(WoolMania.getInstance(), "teleport_halls_inventory"), PersistentDataTypes.enumType(Halls.class));

    public TeleportHallsInventory() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "teleporter_halls"), 45);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_TELEPORT_HALLS);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();
        pagination.previousButton().slots(new int[]{18, 27});
        pagination.nextButton().slots(new int[]{26, 35});

        for (Halls hall : Halls.values()) {
            content.addStaticItem(getDisplayItem(hall));
        }

        inventoryTemplate.addListener(this);
    }

    private Function<User, Object> getDisplayItem(Halls hall) {
        return user -> {

            Player player = Bukkit.getPlayer(user.uniqueId());
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
            ItemBuilder itemBuilder = hall.getWoolEntries().getFirst().handler().createItem();
            itemBuilder.persistentDataStorage().set(HALL, hall);
            itemBuilder.displayname(TO_HALL.getMessage(user, hall.getHallValue().getValue()));

            if (woolManiaPlayer.isHallUnlocked(hall)) {
                return itemBuilder;
            } else if (woolManiaPlayer.getMoney() < hall.getCost()) {
                return itemBuilder.lore(HALL_COST.getMessage(user, hall.getCost())).lore(HALL_LEVEL_COST.getMessage(user, hall.getLevel()));
            } else {
                return itemBuilder.lore(HALL_COST_ENOUGH.getMessage(user, hall.getCost())).lore(HALL_LEVEL_COST.getMessage(user, hall.getLevel()));
            }

        };
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        Halls hall = item.persistentDataStorage().get(HALL);

        if (hall != null) {

            if(woolManiaPlayer.isHallUnlocked(hall)) {
                woolManiaPlayer.teleportTo(hall);
            } else if (woolManiaPlayer.getMoney() > hall.getCost() && hall.getLevel() <= woolManiaPlayer.getLevel()) {
                woolManiaPlayer.unlockHall(hall);
                woolManiaPlayer.removeMoney(hall.getCost(), player);
                BUY.playSound(player);
                user.sendMessage(HALL_BOUGHT, hall.ordinal() + 1);
            } else if (woolManiaPlayer.getMoney() < hall.getCost()) {
                NO.playSound(player);
                user.sendMessage(NO_MONEY);
            } else {
                NO.playSound(player);
                user.sendMessage(LEVEL_TO_LOW);
            }
        }

        inventory.pagedController().publishUpdatePage();
    }
}
