package eu.darkcube.minigame.woolbattle.common;

import java.util.logging.Logger;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskScheduleProvider;
import eu.darkcube.minigame.woolbattle.common.event.CommonEventHandler;
import eu.darkcube.minigame.woolbattle.common.setup.SetupMode;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserInventoryAccess;
import eu.darkcube.minigame.woolbattle.common.user.UserPermissions;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.TaskScheduleProviderImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public abstract class CommonWoolBattle {

    private final Logger LOGGER = Logger.getLogger("WoolBattle");
    private final @NotNull CommonEventHandler eventHandler;
    private final @NotNull SetupMode setupMode;

    public CommonWoolBattle() {
        eventHandler = new CommonEventHandler(this);
        setupMode = new SetupMode(this);
        InjectionLayer.ext().install(BindingBuilder.create().bind(TaskScheduleProvider.class).toInstance(new TaskScheduleProviderImpl()));
    }

    public void start() {
        var api = api();
        api.teamRegistry().init();
        api.lobbySystemLink().enable();
        api.commands().registerDefaults(api);
    }

    public void stop() {
        var api = api();
        api.lobbySystemLink().disable();
    }

    public Logger logger() {
        return LOGGER;
    }

    public @NotNull SetupMode setupMode() {
        return setupMode;
    }

    public @NotNull CommonEventHandler eventHandler() {
        return eventHandler;
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
}
