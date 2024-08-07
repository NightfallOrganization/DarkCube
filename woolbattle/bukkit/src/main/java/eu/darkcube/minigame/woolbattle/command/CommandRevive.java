/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.game.ingame.PlayerUtil;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CommandRevive extends Command {

    public CommandRevive(WoolBattleBukkit woolbattle) {
        super("woolbattle", "revive", "woolbattle.command.revive", new String[0], b -> b.then(Commands
                .argument("player", EntityArgument.player())
                .executes(context -> {
                    Player p = EntityArgument.getPlayer(context, "player");
                    WBUser user = WBUser.getUser(p);
                    if (!woolbattle.ingame().playerUtil().revive(user)) {
                        context.getSource().sendMessage(Component.text("Konnte team für spieler nicht finden!"));
                    } else {
                        context.getSource().sendMessage(Component.text("Spieler wiederbelebt!"));
                    }
                    return 0;
                })));
    }

}
