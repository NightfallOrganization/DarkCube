/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory;

import static eu.darkcube.system.woolmania.enums.Items.*;
import static eu.darkcube.system.woolmania.enums.Names.ZINUS;
import static eu.darkcube.system.woolmania.enums.Sounds.FARMING_SOUND_STANDARD;
import static eu.darkcube.system.woolmania.enums.Sounds.FARMING_SOUND_WOOLBATTLE;
import static eu.darkcube.system.woolmania.util.message.Message.ITEM_BUY_BOUGHT;
import static eu.darkcube.system.woolmania.util.message.Message.ITEM_BUY_COST;

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
import eu.darkcube.system.woolmania.enums.Items;
import eu.darkcube.system.woolmania.enums.Sounds;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopInventorySound implements TemplateInventoryListener {

    private static final String MASK = """
            .........
            .........
            .#######.
            .#######.
            .........
            """;
    private final InventoryTemplate inventoryTemplate;

    public ShopInventorySound() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop_sound"), 45);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        var disc = INVENTORY_SHOP_SOUNDS.createItem(user).<ItemStack>build();
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, disc);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();

        content.addStaticItem(getDisplayItem(user, INVENTORY_SOUNDS_STANDARD));
        content.addStaticItem(getDisplayItem(user, INVENTORY_SOUNDS_WOOLBATTLE));

        inventoryTemplate.addListener(this);
    }

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String clickedItem = Items.getItemID(item);
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        woolManiaPlayer.isLockedSound();

        soudBuyOrSelect(clickedItem, player, INVENTORY_SOUNDS_STANDARD, FARMING_SOUND_STANDARD, woolManiaPlayer);
        soudBuyOrSelect(clickedItem, player, INVENTORY_SOUNDS_WOOLBATTLE, FARMING_SOUND_WOOLBATTLE, woolManiaPlayer);
    }

    private ItemBuilder getDisplayItem(User user, Items items) {
        return items.getItem(user, !items.isUnlocked() ? ITEM_BUY_BOUGHT : ITEM_BUY_COST.getMessage(user, items.getCost()));
    }

    private void soudBuyOrSelect(String clickedItem, Player player, Items item, Sounds sound, WoolManiaPlayer woolManiaPlayer) {
        if (clickedItem.equals(item.itemID()) && !woolManiaPlayer.isLockedSound(sound)) {
            WoolMania.getStaticPlayer(player).setFarmingSound(sound);
            WoolMania.getStaticPlayer(player).removeMoney(item.getCost(), player);
            woolManiaPlayer.unlockSound(sound);
            player.sendMessage("§7Gekauft und gesetzt!");
        } else if (clickedItem.equals(item.itemID()) && woolManiaPlayer.isLockedSound(sound)) {
            WoolMania.getStaticPlayer(player).setFarmingSound(sound);
            player.sendMessage("§7Gesetzt!");
        }
    }

}
