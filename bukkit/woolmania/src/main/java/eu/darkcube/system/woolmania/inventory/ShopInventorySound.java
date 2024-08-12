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
import eu.darkcube.system.server.inventory.InventoryMask;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Items;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopInventorySound {

    private static final String MASK = """
            .........
            .........
            .#######.
            .#######.
            .........
            """;

    public void openInventory(User user) {
        InventoryTemplate inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop_sound"), 45);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);

        var disc = Items.INVENTORY_SHOP_SOUNDS.createItem(user).<ItemStack>build();
        disc.editMeta(meta -> {
            var jukebox = meta.getJukeboxPlayable();
            jukebox.setShowInTooltip(false);
            meta.setJukeboxPlayable(jukebox);
            // meta.setHideTooltip(true);
        });

        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, disc);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();

        content.addStaticItem(Items.INVENTORY_SHOP_SOUNDS);

        inventoryTemplate.open(user);
    }

}
