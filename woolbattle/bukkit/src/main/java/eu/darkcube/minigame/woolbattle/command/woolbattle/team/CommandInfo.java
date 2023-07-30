/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Locale;

public class CommandInfo extends WBCommandExecutor {
    public CommandInfo() {
        super("info", b -> b.executes(ctx -> {
            TeamType team = TeamTypeArgument.teamType(ctx, "team");
            String sb = "§aTeam: " + team.getDisplayNameKey()
                    + "\nNamensfarbe: " + team.getNameColor() + team.getNameColor().name().toLowerCase(Locale.ROOT)
                    + "\n§aWollfarbe: " + team.getWoolColor().name().toLowerCase(Locale.ROOT)
                    + "\nSortierung: " + team.getWeight()
                    + "\nMaximale Spieleranzahl: " + team.getMaxPlayers()
                    + "\nAktiviert: " + team.isEnabled();
            ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection().deserialize(sb));
            return 0;
        }));
    }
}
