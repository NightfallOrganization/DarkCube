package eu.darkcube.minigame.woolbattle.common.command.commands;

import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;
import static eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType.integer;

import java.time.Duration;
import java.util.Objects;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;

public class TimerCommand extends WoolBattleCommand {
    public TimerCommand() {
        super("timer", b -> b.requires(s -> s.source() instanceof CommonWBUser user && user.game() != null).then(argument("time", integer(0, 10000)).executes(ctx -> {
            var user = (CommonWBUser) ctx.getSource().source();
            var game = user.game();
            var time = getInteger(ctx, "time");
            ((CommonLobby) Objects.requireNonNull(game).phase()).timer().overrideTimer(Duration.ofSeconds(time));
            ctx.getSource().sendMessage(Messages.TIMER_CHANGED, time);
            return 0;
        })));
    }
}
