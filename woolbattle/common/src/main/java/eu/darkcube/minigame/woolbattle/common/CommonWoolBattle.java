/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common;

import static eu.darkcube.minigame.woolbattle.api.util.LogUtil.*;
import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;
import static eu.darkcube.system.server.inventory.Inventory.createChestTemplate;

import java.nio.file.Path;

import eu.darkcube.minigame.woolbattle.api.command.CommandSender;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.util.PerkUtils;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskScheduleProvider;
import eu.darkcube.minigame.woolbattle.api.util.translation.Message;
import eu.darkcube.minigame.woolbattle.common.event.CommonEventHandler;
import eu.darkcube.minigame.woolbattle.common.inventory.SettingsInventory;
import eu.darkcube.minigame.woolbattle.common.perk.CommonActivationTypeItemProvider;
import eu.darkcube.minigame.woolbattle.common.setup.SetupMode;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserFactory;
import eu.darkcube.minigame.woolbattle.common.user.UserPermissions;
import eu.darkcube.minigame.woolbattle.common.user.UserPlatformAccess;
import eu.darkcube.minigame.woolbattle.common.util.ChatHandler;
import eu.darkcube.minigame.woolbattle.common.util.PerkUtilsImplementation;
import eu.darkcube.minigame.woolbattle.common.util.item.CommonItem;
import eu.darkcube.minigame.woolbattle.common.util.item.DefaultInventorySettings;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.TaskScheduleProviderImpl;
import eu.darkcube.minigame.woolbattle.common.util.schematic.Schematic;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicReader;
import eu.darkcube.minigame.woolbattle.common.util.translation.LanguageRegistry;
import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.KeyPattern;
import eu.darkcube.system.libs.net.kyori.adventure.key.Namespaced;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.userapi.User;
import org.slf4j.Logger;

public abstract class CommonWoolBattle implements Namespaced {

    private final @NotNull CommonEventHandler eventHandler;
    private final @NotNull SetupMode setupMode;
    private final @NotNull LanguageRegistry languageRegistry;
    private final @NotNull Path mapsDirectory = Path.of("maps");
    private final @NotNull ItemTemplate defaultItemTemplate;
    private final @NotNull ItemTemplate defaultPagedItemTemplate;
    private final @NotNull ItemTemplate defaultSinglePagedItemTemplate;
    private final @NotNull InventoryTemplate settingsInventoryTemplate;
    private final @NotNull InventoryTemplate settingsHeightDisplayInventoryTemplate;
    private final @NotNull InventoryTemplate settingsHeightDisplayColorInventoryTemplate;
    private final @NotNull InventoryTemplate settingsWoolDirectionInventoryTemplate;
    private final @NotNull Schematic lobbySchematic;
    private final @NotNull ChatHandler chatHandler;

    public CommonWoolBattle() {
        eventHandler = new CommonEventHandler(this);
        setupMode = new SetupMode(this);
        WoolBattleProvider.PROVIDER.register(TaskScheduleProvider.class, new TaskScheduleProviderImpl());
        WoolBattleProvider.PROVIDER.register(ActivationType.ItemProvider.class, new CommonActivationTypeItemProvider());
        WoolBattleProvider.PROVIDER.register(PerkUtils.Implementation.class, new PerkUtilsImplementation());
        this.languageRegistry = new LanguageRegistry();

        this.defaultItemTemplate = DefaultInventorySettings.create();
        this.defaultPagedItemTemplate = DefaultInventorySettings.create();
        this.defaultSinglePagedItemTemplate = DefaultInventorySettings.create();

        this.settingsInventoryTemplate = InventoryTemplate.lazy(() -> SettingsInventory.create(this));
        this.settingsHeightDisplayInventoryTemplate = InventoryTemplate.lazy(() -> SettingsInventory.createHeightDisplay(this));
        this.settingsHeightDisplayColorInventoryTemplate = InventoryTemplate.lazy(() -> SettingsInventory.createHeightDisplayColor(this));
        this.settingsWoolDirectionInventoryTemplate = InventoryTemplate.lazy(() -> SettingsInventory.createWoolDirection(this));
        this.chatHandler = new ChatHandler();
        this.lobbySchematic = SchematicReader.read(Path.of("lobby.litematic"));
    }

    public void start() {
        Items.loadItems();
        var api = api();
        api.teamRegistry().init();
        api.lobbySystemLink().enable();
        api.commands().registerDefaults(api);
        languageRegistry.register();
        DefaultInventorySettings.setup(this.defaultItemTemplate);
        DefaultInventorySettings.setupPaged(this.defaultPagedItemTemplate);
        DefaultInventorySettings.setup(this.defaultSinglePagedItemTemplate);
    }

    public void stop() {
        var api = api();
        api.lobbySystemLink().disable();
        languageRegistry.unregister();
    }

    public @NotNull ChatHandler chatHandler() {
        return chatHandler;
    }

    public @NotNull Logger logger() {
        return LOGGER;
    }

    public @NotNull SetupMode setupMode() {
        return setupMode;
    }

    public @NotNull CommonEventHandler eventHandler() {
        return eventHandler;
    }

    public @NotNull Schematic lobbySchematic() {
        return lobbySchematic;
    }

    public abstract @NotNull SetupMode.Implementation setupModeImplementation();

    /**
     * Both nullable, oldTeam may be null if the initial team is set, newTeam may be null if the player is quitting.
     * Both may never be null.
     *
     * @param oldTeam the old team, may be null
     * @param newTeam the new team, may be null
     */
    public abstract void broadcastTeamUpdate(@NotNull CommonWBUser user, @Nullable CommonTeam oldTeam, @Nullable CommonTeam newTeam);

    public abstract @NotNull UserFactory userFactory();

    public abstract @NotNull CommonWoolBattleApi api();

    public abstract @NotNull CommandSender consoleSender();

    public @NotNull InventoryTemplate settingsInventoryTemplate() {
        return settingsInventoryTemplate;
    }

    public @NotNull InventoryTemplate settingsHeightDisplayInventoryTemplate() {
        return settingsHeightDisplayInventoryTemplate;
    }

    public @NotNull InventoryTemplate settingsHeightDisplayColorInventoryTemplate() {
        return settingsHeightDisplayColorInventoryTemplate;
    }

    public @NotNull InventoryTemplate settingsWoolDirectionInventoryTemplate() {
        return settingsWoolDirectionInventoryTemplate;
    }

    public abstract @NotNull UserPlatformAccess createInventoryAccessFor(@NotNull CommonWBUser user);

    public abstract @NotNull UserPermissions createPermissionsFor(@NotNull CommonWBUser user);

    public @NotNull ItemTemplate defaultInventoryTemplate() {
        return this.defaultItemTemplate;
    }

    public @NotNull ItemTemplate defaultSinglePagedInventoryTemplate() {
        return this.defaultSinglePagedItemTemplate;
    }

    public @NotNull ItemTemplate defaultPagedInventoryTemplate() {
        return this.defaultPagedItemTemplate;
    }

    public @NotNull Path mapsDirectory() {
        return mapsDirectory;
    }

    public void configureDefaultSinglePagedInventory(@NotNull InventoryTemplate template) {
        template.setItems(0, defaultSinglePagedInventoryTemplate());
        configurePaged(template);
    }

    public void configureDefaultPagedInventory(@NotNull InventoryTemplate template) {
        template.setItems(0, defaultPagedInventoryTemplate());
        configurePaged(template);
    }

    public InventoryTemplate createDefaultTemplate(@KeyPattern.Value String key, Message title, @Nullable CommonItem displayItem) {
        var template = createChestTemplate(key(this, key), 5 * 9);
        template.title(title);
        configureDefaultSinglePagedInventory(template);
        if (displayItem != null) {
            template.setItem(10, 4, displayItem.withoutId());
        }
        return template;
    }

    private void configurePaged(@NotNull InventoryTemplate template) {
        template.animation().calculateManifold(22, 1);
        template.addListener(TemplateInventoryListener.ofStateful(() -> new TemplateInventoryListener() {
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
                if (!finished) {
                    var wbUser = api().user(user);
                    if (wbUser == null) return;
                    wbUser.platformAccess().playInventorySound();
                }
            }
        }));
        var pagination = template.pagination();
        pagination.pageSlots(DefaultInventorySettings.PAGE_SLOTS_5x9);
        pagination.previousButton().setItem(Items.PREV_PAGE);
        pagination.previousButton().slots(DefaultInventorySettings.SLOTS_PAGED_PREV_5x9);
        pagination.nextButton().setItem(Items.NEXT_PAGE);
        pagination.nextButton().slots(DefaultInventorySettings.SLOTS_PAGED_NEXT_5x9);
    }
}
