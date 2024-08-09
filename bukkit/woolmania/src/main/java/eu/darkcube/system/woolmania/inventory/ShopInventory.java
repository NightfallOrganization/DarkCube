/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;
import static eu.darkcube.system.woolmania.enums.Names.TELEPORTER;
import static eu.darkcube.system.woolmania.enums.Names.ZINUS;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopInventory {
    public void openInventory(Player player) {
        InventoryTemplate inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop"), 27);
        inventoryTemplate.title(ZINUS.getName());

        inventoryTemplate.animation().calculateManifold(4, 1);

        var disc = Items.INVENTORY_SHOP_MUSIC_DISK.createItem(player).<ItemStack>build();
        disc.editMeta(meta -> {
            var jukebox = meta.getJukeboxPlayable();
            jukebox.setShowInTooltip(false);
            meta.setJukeboxPlayable(jukebox);
            // meta.setHideTooltip(true);
        });
        inventoryTemplate.addListener(TemplateInventoryListener.ofStateful(() -> new TemplateInventoryListener() {
            private boolean finished = false;
            private User user;

            @Override
            public void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                this.user = user;
            }

            @Override
            public void onOpenAnimationFinished(@NotNull TemplateInventory inventory) {
                finished = true;
            }

            @Override
            public void onUpdate(@NotNull TemplateInventory inventory) {
                System.out.println("Update " + finished + " " + user);
                if (!finished) {
                    Player player = Bukkit.getPlayer(user.uniqueId());
                    if (player != null) {
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 100, 1);
                    }
                }
            }
        }));
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_3);
        inventoryTemplate.setItem(1, 11, Items.INVENTORY_SHOP_STEAK);
        inventoryTemplate.setItem(1, 13, Items.INVENTORY_SHOP_SHEARS);
        inventoryTemplate.setItem(1, 15, disc);
        inventoryTemplate.open(player);
    }

}
