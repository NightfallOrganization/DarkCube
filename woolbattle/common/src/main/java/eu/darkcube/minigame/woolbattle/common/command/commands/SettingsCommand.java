package eu.darkcube.minigame.woolbattle.common.command.commands;

import java.util.Objects;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class SettingsCommand extends WoolBattleCommand {
    public SettingsCommand() {
        super("settings", b -> b.requires(s -> {
            if (s.source() instanceof CommonWBUser user) {
                var game = user.game();
                return game != null;
            }
            return false;
        }).executes(ctx -> {
            var user = (CommonWBUser) ctx.getSource().source();
            @NotNull var game = Objects.requireNonNull(user.game());
            game.woolbattle().woolbattle().settingsInventoryTemplate().open(user.user());
            return 0;
        }));
    }
}
