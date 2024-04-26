/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common;

import java.util.logging.Logger;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskScheduleProvider;
import eu.darkcube.minigame.woolbattle.common.command.arguments.CommonWoolBattleArguments;
import eu.darkcube.minigame.woolbattle.common.event.CommonEventHandler;
import eu.darkcube.minigame.woolbattle.common.setup.SetupMode;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserInventoryAccess;
import eu.darkcube.minigame.woolbattle.common.user.UserPermissions;
import eu.darkcube.minigame.woolbattle.common.util.item.DefaultItemTemplate;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.TaskScheduleProviderImpl;
import eu.darkcube.minigame.woolbattle.common.util.translation.LanguageRegistry;
import eu.darkcube.minigame.woolbattle.common.world.SimpleWorldDataProvider;
import eu.darkcube.minigame.woolbattle.common.world.WorldDataProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.item.ItemTemplate;

public abstract class CommonWoolBattle {

    private final @NotNull Logger logger = Logger.getLogger("WoolBattle");
    private final @NotNull CommonEventHandler eventHandler;
    private final @NotNull SetupMode setupMode;
    private final @NotNull WorldDataProvider worldDataProvider;
    private final @NotNull LanguageRegistry languageRegistry;
    private final @NotNull ItemTemplate defaultItemTemplate;

    public CommonWoolBattle() {
        eventHandler = new CommonEventHandler(this);
        setupMode = new SetupMode(this);
        InjectionLayer.ext().install(BindingBuilder.create().bind(TaskScheduleProvider.class).toInstance(new TaskScheduleProviderImpl()));
        InjectionLayer.ext().install(BindingBuilder.create().bind(WoolBattleArguments.class).toInstance(new CommonWoolBattleArguments()));
        this.worldDataProvider = new SimpleWorldDataProvider();
        this.languageRegistry = new LanguageRegistry();

        this.defaultItemTemplate = DefaultItemTemplate.create();
    }

    public void start() {
        var api = api();
        api.teamRegistry().init();
        api.lobbySystemLink().enable();
        api.commands().registerDefaults(api);
        languageRegistry.register();
        DefaultItemTemplate.setup(this.defaultItemTemplate);
    }

    public void stop() {
        var api = api();
        api.lobbySystemLink().disable();
        languageRegistry.unregister();
    }

    public @NotNull Logger logger() {
        return logger;
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
        return defaultItemTemplate;
    }
}
