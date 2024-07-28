/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common;

import static eu.darkcube.minigame.woolbattle.api.util.LogUtil.*;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskScheduleProvider;
import eu.darkcube.minigame.woolbattle.common.event.CommonEventHandler;
import eu.darkcube.minigame.woolbattle.common.perk.CommonActivationTypeItemProvider;
import eu.darkcube.minigame.woolbattle.common.setup.SetupMode;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserInventoryAccess;
import eu.darkcube.minigame.woolbattle.common.user.UserPermissions;
import eu.darkcube.minigame.woolbattle.common.util.item.DefaultInventorySettings;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.TaskScheduleProviderImpl;
import eu.darkcube.minigame.woolbattle.common.util.translation.LanguageRegistry;
import eu.darkcube.minigame.woolbattle.common.world.SimpleWorldDataProvider;
import eu.darkcube.minigame.woolbattle.common.world.WorldDataProvider;
import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Namespaced;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import org.slf4j.Logger;

public abstract class CommonWoolBattle implements Namespaced {

    private final @NotNull CommonEventHandler eventHandler;
    private final @NotNull SetupMode setupMode;
    private final @NotNull WorldDataProvider worldDataProvider;
    private final @NotNull LanguageRegistry languageRegistry;
    private final @NotNull ItemTemplate defaultItemTemplate;
    private final @NotNull ItemTemplate defaultPagedItemTemplate;
    private final @NotNull ItemTemplate defaultSinglePagedItemTemplate;

    public CommonWoolBattle() {
        eventHandler = new CommonEventHandler(this);
        setupMode = new SetupMode(this);
        WoolBattleProvider.PROVIDER.register(TaskScheduleProvider.class, new TaskScheduleProviderImpl());
        WoolBattleProvider.PROVIDER.register(ActivationType.ItemProvider.class, new CommonActivationTypeItemProvider());
        this.worldDataProvider = new SimpleWorldDataProvider();
        this.languageRegistry = new LanguageRegistry();

        this.defaultItemTemplate = DefaultInventorySettings.create();
        this.defaultPagedItemTemplate = DefaultInventorySettings.create();
        this.defaultSinglePagedItemTemplate = DefaultInventorySettings.create();
    }

    public void start() {
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

    public @NotNull Logger logger() {
        return LOGGER;
    }

    public @NotNull SetupMode setupMode() {
        return setupMode;
    }

    public @NotNull CommonEventHandler eventHandler() {
        return eventHandler;
    }

    public @NotNull WorldDataProvider worldDataProvider() {
        return worldDataProvider;
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

    public abstract @NotNull CommonWoolBattleApi api();

    public abstract @NotNull UserInventoryAccess createInventoryAccessFor(@NotNull CommonWBUser user);

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

    public void configureDefaultSinglePagedInventory(@NotNull InventoryTemplate template) {
        template.setItems(0, defaultSinglePagedInventoryTemplate());
        configurePaged(template);
    }

    public void configureDefaultPagedInventory(@NotNull InventoryTemplate template) {
        template.setItems(0, defaultPagedInventoryTemplate());
        configurePaged(template);
    }

    private void configurePaged(@NotNull InventoryTemplate template) {
        template.animation().calculateManifold(22, 1);
        var pagination = template.pagination();
        pagination.pageSlots(DefaultInventorySettings.PAGE_SLOTS_5x9);
        pagination.previousButton().setItem(Items.PREV_PAGE);
        pagination.previousButton().slots(DefaultInventorySettings.SLOTS_PAGED_PREV_5x9);
        pagination.nextButton().setItem(Items.NEXT_PAGE);
        pagination.nextButton().slots(DefaultInventorySettings.SLOTS_PAGED_NEXT_5x9);
    }
}
