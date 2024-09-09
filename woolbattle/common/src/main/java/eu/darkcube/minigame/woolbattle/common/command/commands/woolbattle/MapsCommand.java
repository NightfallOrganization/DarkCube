package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.maps.MapCommand;

public class MapsCommand extends WoolBattleCommand {
    public MapsCommand(CommonWoolBattleApi woolbattle) {
        // @formatter:off
        super("maps", b -> b
                .then(new MapCommand(woolbattle).builder())
        );
        // @formatter:on
    }
}
