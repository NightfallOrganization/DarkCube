package eu.darkcube.minigame.woolbattle.api.command;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface WoolBattleCommands {
    @Api
    void register(@NotNull WoolBattleCommand command);

    @Api
    void unregister(@NotNull WoolBattleCommand command);
}
