/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.inventory;

import static eu.darkcube.system.bauserver.heads.database.DatabaseStorage.tokenize;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.bauserver.heads.database.DatabaseStorage;
import eu.darkcube.system.bauserver.heads.remote.Providers;
import eu.darkcube.system.bauserver.heads.remote.RemoteHeadProvider;
import eu.darkcube.system.bauserver.util.Message;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryCapabilities;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
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

    private static final DataKey<Boolean> BACK = DataKey.of(Key.key(Main.getInstance(), "back"), PersistentDataTypes.BOOLEAN);
    private static final DataKey<Boolean> SEARCH = DataKey.of(Key.key(Main.getInstance(), "search"), PersistentDataTypes.BOOLEAN);
    private static final DataKey<String> LAST_SEARCH = DataKey.of(Key.key(Main.getInstance(), "head_database_last_search"), PersistentDataTypes.STRING);

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
            template.setItem(0, 6 * 9 - 1, back());
            template.addListener(back(DATABASE_ROOT));
            DarkCubeInventoryTemplates.Paged.configure6x9(template);

            var categoryKey = DataKey.of(Key.key(Main.getInstance(), "category"), PersistentDataTypes.STRING);
            var content = template.pagination().content();
            var categoryTemplates = new HashMap<String, InventoryTemplate>();
            addCategory(template, content, provider, null, categoryKey, categoryTemplates);
            for (var category : provider.categories()) {
                addCategory(template, content, provider, category, categoryKey, categoryTemplates);
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

    private static Object back() {
        return (Function<User, Object>) user -> ItemBuilder.item(Material.RED_BED).set(ItemComponent.ITEM_NAME, Message.BACK.getMessage(user)).persistentDataStorage().iset(BACK.key(), BACK.dataType(), true).builder();
    }

    private static Object search() {
        return (Function<User, Object>) user -> ItemBuilder.item(Material.COMPASS).set(ItemComponent.ITEM_NAME, Message.SEARCH.getMessage(user)).persistentDataStorage().iset(SEARCH.key(), SEARCH.dataType(), true).builder();
    }

    private static TemplateInventoryListener back(InventoryTemplate prev) {
        return new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                if (!item.persistentDataStorage().has(BACK)) return;
                prev.open(user);
            }
        };
    }

    private static void addCategory(InventoryTemplate prev, PagedInventoryContent content, RemoteHeadProvider provider, @Nullable String category, DataKey<String> categoryKey, Map<String, InventoryTemplate> categoryTemplates) {
        add(content, category, categoryKey);
        var searchTemplate = Inventory.createTemplate(Key.key(Main.getInstance(), "search"), InventoryType.of(org.bukkit.event.inventory.InventoryType.ANVIL));
        searchTemplate.setItem(0, 0, user -> {
            var item = ItemBuilder.item(Material.PAPER).set(ItemComponent.CUSTOM_NAME, Component.empty()).hideTooltip();
            if (user.persistentData().has(LAST_SEARCH)) {
                item.set(ItemComponent.CUSTOM_NAME, Component.text(Objects.requireNonNull(user.persistentData().get(LAST_SEARCH))));
            }
            return item;
        });
        searchTemplate.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                var searchText = ((InventoryCapabilities.Anvil) inventory.capabilities()).renameText();
                try {
                    if (searchText != null) {
                        user.persistentData().set(LAST_SEARCH, searchText);
                    } else {
                        user.persistentData().remove(LAST_SEARCH);
                    }
                    var tokens = tokenize(searchText);
                    var t = createSearchInventory(prev, provider, category, searchTemplate, tokens.toArray(DatabaseStorage.Token[]::new));
                    t.open(user);
                } catch (DatabaseStorage.BadInputException e) {
                    user.sendMessage(Component.text("Bad input: " + e.getMessage()));
                } catch (DatabaseStorage.InvalidTypeException e) {
                    user.sendMessage(Component.text("Invalid type: " + e.getMessage()));
                } catch (DatabaseStorage.TokenizerException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        var categoryTemplate = createSearchInventory(prev, provider, category, searchTemplate, null);
        categoryTemplates.put(category == null ? "all" : category, categoryTemplate);
    }

    private static InventoryTemplate createSearchInventory(InventoryTemplate prev, RemoteHeadProvider provider, String category, InventoryTemplate searchTemplate, @Nullable DatabaseStorage.Token[] tokens) {
        var template = Inventory.createChestTemplate(Key.key(Main.getInstance(), "category"), 6 * 9);
        var dp = category == null ? new DatabaseHeadProvider(provider.name()) : new DatabaseHeadProvider(provider.name(), category);
        if (tokens != null) {
            dp.tokens(tokens);
        }
        template.pagination().content().provider(dp);
        template.title(new BaseMessage() {
            @Override
            public @NotNull String key() {
                return "";
            }

            @Override
            public @NotNull Component getMessage(@NotNull Language language, @NotNull String @NotNull [] prefixes, Object @NotNull ... args) {
                var storage = Main.getInstance().databaseStorage();
                int size;
                if (tokens == null) {
                    if (category == null) {
                        size = storage.size(provider.name());
                    } else {
                        size = storage.size(provider.name(), category);
                    }
                } else {
                    if (category == null) {
                        size = storage.size(provider.name(), tokens);
                    } else {
                        size = storage.size(provider.name(), category, tokens);
                    }
                }
                return text(category == null ? "All" : category, NamedTextColor.GOLD).append(text(" (" + size + ")", NamedTextColor.GRAY));
            }
        });
        template.setItems(-1, DarkCubeItemTemplates.Gray.TEMPLATE_6);
        template.setItem(0, 53, back());
        template.setItem(0, 8, search());
        DarkCubeInventoryTemplates.Paged.configure6x9(template);
        template.pagination().content().makeAsync();
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                if (!item.persistentDataStorage().has(SEARCH)) return;
                searchTemplate.open(user);
            }
        });
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
                if (!item.persistentDataStorage().has(DatabaseHeadProvider.IS_HEAD)) return;
                var player = Bukkit.getPlayer(user.uniqueId());
                if (player == null) return;
                item.remove(ItemComponent.CUSTOM_DATA);
                player.getInventory().addItem(item.<ItemStack>build());
            }
        });
        template.addListener(back(prev));
        return template;
    }

    private static void add(PagedInventoryContent content, @Nullable String category, DataKey<String> categoryKey) {
        var name = category == null ? "All" : category;
        var item = ItemBuilder.item(Material.PAPER);
        item.set(ItemComponent.ITEM_NAME, text(name, NamedTextColor.GOLD));
        item.persistentDataStorage().set(categoryKey, category == null ? "all" : category);
        content.addStaticItem(item);
    }
}
