/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.bauserver.heads.remote.Providers;
import eu.darkcube.system.bauserver.heads.remote.RemoteHeadProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.ClickData;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HeadInventories {
    public static final InventoryTemplate STORED_LIST;
    public static final InventoryTemplate DATABASE_ROOT;

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

        DATABASE_ROOT = Inventory.createChestTemplate(Key.key(Main.getInstance(), "database_root"), 6 * 9);
        DATABASE_ROOT.setItems(-1, DarkCubeItemTemplates.Gray.TEMPLATE_6);
        DarkCubeInventoryTemplates.Paged.configure6x9(DATABASE_ROOT);
        var providerKey = DataKey.of(Key.key(Main.getInstance(), "provider"), PersistentDataTypes.STRING);
        var providerTemplates = new HashMap<String, InventoryTemplate>();
        for (var provider : Providers.providers()) {
            var item = ItemBuilder.item(Material.CHEST);
            item.set(ItemComponent.ITEM_NAME, text(provider.name(), NamedTextColor.GOLD));
            item.persistentDataStorage().set(providerKey, provider.name());
            DATABASE_ROOT.pagination().content().addStaticItem(item);

            var template = Inventory.createChestTemplate(Key.key(Main.getInstance(), "provider"), 6 * 9);
            template.setItems(-1, DarkCubeItemTemplates.Gray.TEMPLATE_6);
            DarkCubeInventoryTemplates.Paged.configure6x9(template);

            var categoryKey = DataKey.of(Key.key(Main.getInstance(), "category"), PersistentDataTypes.STRING);
            var content = template.pagination().content();
            var categoryTemplates = new HashMap<String, InventoryTemplate>();
            addCategory(content, provider, null, categoryKey, categoryTemplates);
            for (var category : provider.categories()) {
                addCategory(content, provider, category, categoryKey, categoryTemplates);
            }

            template.addListener(new TemplateInventoryListener() {
                @Override
                public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                    if (!item.persistentDataStorage().has(categoryKey)) return;
                    var category = Objects.requireNonNull(item.persistentDataStorage().get(categoryKey));
                    var template = categoryTemplates.get(category);
                    if (template == null) return;
                    template.open(user);
                }
            });

            providerTemplates.put(provider.name(), template);
        }
        DATABASE_ROOT.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                if (!item.persistentDataStorage().has(providerKey)) return;
                var template = providerTemplates.get(item.persistentDataStorage().get(providerKey));
                if (template == null) return;
                template.open(user);
            }
        });
    }

    private static void addCategory(PagedInventoryContent content, RemoteHeadProvider provider, @Nullable String category, DataKey<String> categoryKey, Map<String, InventoryTemplate> categoryTemplates) {
        add(content, category, categoryKey);

        var categoryTemplate = Inventory.createChestTemplate(Key.key(Main.getInstance(), "category"), 6 * 9);
        categoryTemplate.setItems(-1, DarkCubeItemTemplates.Gray.TEMPLATE_6);
        DarkCubeInventoryTemplates.Paged.configure6x9(categoryTemplate);
        var dp = category == null ? new DatabaseHeadProvider(provider.name()) : new DatabaseHeadProvider(provider.name(), category);
        categoryTemplate.pagination().content().provider(dp);
        categoryTemplate.title(new BaseMessage() {
            @Override
            public @NotNull String key() {
                return "";
            }

            @Override
            public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
                var storage = Main.getInstance().databaseStorage();
                var size = category == null ? storage.size(provider.name()) : storage.size(provider.name(), category);
                return text(category == null ? "All" : category, NamedTextColor.GOLD).append(text(" (" + size + ")", NamedTextColor.GRAY));
            }
        });
        categoryTemplate.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                if (!item.persistentDataStorage().has(DatabaseHeadProvider.IS_HEAD)) return;
                var player = Bukkit.getPlayer(user.uniqueId());
                if (player == null) return;
                item.remove(ItemComponent.CUSTOM_DATA);
                player.getInventory().addItem(item.<ItemStack>build());
            }
        });

        categoryTemplates.put(category == null ? "all" : category, categoryTemplate);
    }

    private static void add(PagedInventoryContent content, @Nullable String category, DataKey<String> categoryKey) {
        var name = category == null ? "All" : category;
        var item = ItemBuilder.item(Material.PAPER);
        item.set(ItemComponent.ITEM_NAME, text(name, NamedTextColor.GOLD));
        item.persistentDataStorage().set(categoryKey, category == null ? "all" : category);
        content.addStaticItem(item);
    }
}
