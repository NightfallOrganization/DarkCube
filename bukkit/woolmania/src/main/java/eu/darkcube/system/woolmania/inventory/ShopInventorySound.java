/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory;

import static eu.darkcube.system.woolmania.enums.Names.ZINUS;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Items;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopInventorySound {

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

        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_3);
        DarkCubeInventoryTemplates.Paged.configure3x9(inventoryTemplate, disc);

        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        PagedInventoryContent content = pagination.content();

        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_STEAK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);
        content.addStaticItem(Items.INVENTORY_SHOP_MUSIC_DISK);

        inventoryTemplate.open(player);
    }

}
