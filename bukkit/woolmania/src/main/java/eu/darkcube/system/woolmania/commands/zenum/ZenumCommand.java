/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands.zenum;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.commands.WoolManiaCommand;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.entity.Player;

public class ZenumCommand extends WoolManiaCommand {
    public ZenumCommand() {
        // @formatter:off
        super("zenum",builder-> builder
                .executes(context -> {
                    // @formatter:on
                    Player player = context.getSource().asPlayer();
                    int money = WoolMania.getStaticPlayer(player).getMoney();
                    context.getSource().sendMessage(Message.ZENUM_OWN, money);
                    return 0;
                    // @formatter:off
                })

                .then(new SetZenumCommand().builder())

        );
        // @formatter:on
    }
}

// public class WoolBattleRootCommand extends WoolBattleCommand {
//     public WoolBattleRootCommand(CommonWoolBattleApi woolbattle) {
//         // @formatter:off
//         super("woolbattle", b -> b
//                 .then(new MigrateMapsCommand(woolbattle).builder())
//                 .then(new SetupCommand(woolbattle).builder())
//                 .then(new GameCommand(woolbattle).builder())
//                 .then(new LobbyCommand(woolbattle).builder())
//                 .then(new TeamsCommand(woolbattle).builder())
//         );
//         // @formatter:on
//     }
// }
