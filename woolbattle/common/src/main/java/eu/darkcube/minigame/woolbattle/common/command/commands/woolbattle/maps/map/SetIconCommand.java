package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.maps.map;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.maps.MapCommand;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.server.item.material.Material;

public class SetIconCommand extends WoolBattleCommand {
    public SetIconCommand(CommonWoolBattleApi woolbattle) {
        super("setIcon", b -> b.requires(s -> s.source().isPlayer() && s.source() instanceof CommonWBUser).executes(ctx -> {
            var map = WoolBattleArguments.map(ctx, MapCommand.MAP_NAME);
            var user = (CommonWBUser) ctx.getSource().source();
            var icon = user.platformAccess().itemInHand();
            if (icon.material() == Material.air()) {
                user.sendMessage(Messages.AIR_IN_HAND);
                return 0;
            }
            map.icon(icon);
            user.sendMessage(Messages.MAP_ICON_SET, map.name());
            return 0;
        }));
    }
}
