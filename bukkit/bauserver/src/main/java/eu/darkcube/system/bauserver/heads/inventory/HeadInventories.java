/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.inventory;

import java.util.List;
import java.util.Objects;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.ClickData;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.userapi.User;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class HeadInventories {
    public static final InventoryTemplate STORED_LIST;

    static {
        STORED_LIST = Inventory.createChestTemplate(Key.key(Main.getInstance(), "stored_list"), 6 * 9);
        STORED_LIST.setItems(-1, DarkCubeItemTemplates.Gray.TEMPLATE_6);
        DarkCubeInventoryTemplates.Paged.configure6x9(STORED_LIST);
        STORED_LIST.pagination().content().provider(new StoredHeadProvider());
        STORED_LIST.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                if (!item.persistentDataStorage().has(StoredHeadProvider.INDEX)) return;
                int index = Objects.requireNonNull(item.persistentDataStorage().get(StoredHeadProvider.INDEX));
                if (index >= Main.getInstance().headStorage().size()) {
                    inventory.pagedController().publishUpdateAll();
                    return;
                }

                if (clickData.isRight() && clickData.isShift()) {
                    // Try remove
                    Main.getInstance().headStorage().removeHead(index);
                    inventory.pagedController().publishUpdateAll();
                    return;
                }
                var player = Bukkit.getPlayer(user.uniqueId());
                if (player == null) return;
                item.set(ItemComponent.LORE, List.of());
                item.remove(ItemComponent.CUSTOM_DATA);
                player.getInventory().addItem(item.<ItemStack>build());
            }
        });
    }
}
