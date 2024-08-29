/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.shop;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;
import static eu.darkcube.system.woolmania.enums.Names.ZINUS;
import static eu.darkcube.system.woolmania.enums.Sounds.*;
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
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import eu.darkcube.system.woolmania.enums.Sounds;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ShopInventorySound implements TemplateInventoryListener {

    private static final String MASK = """
            .........
            .........
            .#######.
            .#######.
            .........
            """;
    private final InventoryTemplate inventoryTemplate;

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public ShopInventorySound() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop_sound"), 45);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_SHOP_SOUNDS);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();

        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_SOUNDS_STANDARD, FARMING_SOUND_STANDARD));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_SOUNDS_WOOLBATTLE, FARMING_SOUND_WOOLBATTLE));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_SOUNDS_ARMADILLO, FARMING_SOUND_ARMADILLO));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_SOUNDS_ARMADILLO_HIGH, FARMING_SOUND_ARMADILLO_HIGH));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_SOUNDS_SCAFFOLDING, FARMING_SOUND_SCAFFOLDING));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_SOUNDS_SCAFFOLDING_HIGH, FARMING_SOUND_SCAFFOLDING_HIGH));

        inventoryTemplate.addListener(this);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String clickedItem = InventoryItems.getItemID(item);
        if (clickedItem == null) return;
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);

        soudBuyOrSelect(clickedItem, player, INVENTORY_SHOP_SOUNDS_STANDARD, FARMING_SOUND_STANDARD, woolManiaPlayer, user);
        soudBuyOrSelect(clickedItem, player, INVENTORY_SHOP_SOUNDS_WOOLBATTLE, FARMING_SOUND_WOOLBATTLE, woolManiaPlayer, user);
        soudBuyOrSelect(clickedItem, player, INVENTORY_SHOP_SOUNDS_ARMADILLO, FARMING_SOUND_ARMADILLO, woolManiaPlayer, user);
        soudBuyOrSelect(clickedItem, player, INVENTORY_SHOP_SOUNDS_ARMADILLO_HIGH, FARMING_SOUND_ARMADILLO_HIGH, woolManiaPlayer, user);
        soudBuyOrSelect(clickedItem, player, INVENTORY_SHOP_SOUNDS_SCAFFOLDING, FARMING_SOUND_SCAFFOLDING, woolManiaPlayer, user);
        soudBuyOrSelect(clickedItem, player, INVENTORY_SHOP_SOUNDS_SCAFFOLDING_HIGH, FARMING_SOUND_SCAFFOLDING_HIGH, woolManiaPlayer, user);

        inventory.pagedController().publishUpdatePage();
    }

    private Function<User, Object> getDisplayItem(InventoryItems item, Sounds sound) {
        return user -> {
            WoolManiaPlayer player = WoolMania.getStaticPlayer(Bukkit.getPlayer(user.uniqueId()));

            if (sound.equals(player.getFarmingSound())) {
                return item.getItem(user,ITEM_BUY_SELECTED.getMessage(user)).glow(true);
            } else if (player.isSoundUnlocked(sound)) {
                return item.getItem(user, ITEM_BUY_BOUGHT.getMessage(user));
            } else {
                return item.getItem(user, ITEM_BUY_COST.getMessage(user, item.getCost()));
            }
        };
    }

    private void soudBuyOrSelect(String clickedItem, Player player, InventoryItems item, Sounds sound, WoolManiaPlayer woolManiaPlayer, User user) {
        if (woolManiaPlayer.getFarmingSound() == sound) return;
        if (clickedItem.equals(item.itemID()) && !woolManiaPlayer.isSoundUnlocked(sound)) {
            int cost = item.getCost();
            int playerMoney = WoolMania.getStaticPlayer(player).getMoney();

            if (cost > playerMoney) {
                user.sendMessage(NO_MONEY);
                NO.playSound(player);
                return;
            }

            WoolMania.getStaticPlayer(player).setFarmingSound(sound);
            WoolMania.getStaticPlayer(player).removeMoney(item.getCost(), player);
            BUY.playSound(player);
            woolManiaPlayer.unlockSound(sound);
            user.sendMessage(SOUND_BUYED, item.getItem(user, "").displayname(), item.getCost());
        } else if (clickedItem.equals(item.itemID()) && woolManiaPlayer.isSoundUnlocked(sound)) {
            WoolMania.getStaticPlayer(player).setFarmingSound(sound);
            SOUND_SET.playSound(player);
            user.sendMessage(SOUND_SELECTED, item.getItem(user, "").displayname());
        }
    }
}
