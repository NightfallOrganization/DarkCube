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
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.DyeColor;

public class CommandSetWoolColor extends WBCommandExecutor {
    public CommandSetWoolColor() {
        super("setWoolColor", b -> b.then(Commands.argument("woolColor",
                EnumArgument.enumArgument(DyeColor.values(),
                        c -> new String[]{c.name().toLowerCase()})).executes(ctx -> {
            TeamType team = TeamTypeArgument.teamType(ctx, "team");
            DyeColor woolColor = EnumArgument.getEnumArgument(ctx, "woolColor", DyeColor.class);
            team.setWoolColor(woolColor);
            ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection().deserialize(
                    "§7Du hast die Wollfarbe des Teams " + team.getDisplayNameKey() + " zu "
                            + woolColor.name() + "§7 geändert."));
            return 0;
        })));
    }
    //	public CommandSetWoolColor() {
    //		super(WoolBattle.getInstance(), "setWoolColor", new Command[0], "Setzt die Wollfarbe", CommandArgument.WOOL_COLOR);
    //	}
    //
    //	@Override
    //	public List<String> onTabComplete(String[] args) {
    //		if (args.length == 1) {
    //			return Arrays.toSortedStringList(DyeColor.values(), args[0]);
    //		}
    //		return super.onTabComplete(args);
    //	}
    //
    //	@SuppressWarnings("deprecation")
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		if (args.length == 1) {
    //			TeamType team = TeamType.byDisplayNameKey(getSpaced());
    //			if (team == null || team.isDeleted()) {
    //				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
    //				return true;
    //			}
    //			DyeColor dyeColor = DyeColor.getByData(ColorUtil.byDyeColor(args[0]));
    //			if (dyeColor == null) {
    //				sender.sendMessage("§cBitte gib eine gültige Wollfarbe an!");
    //				return true;
    //			}
    //			team.setWoolColor(dyeColor);
    //			sender.sendMessage("§7Du hast die Wollfarbe des Teams " + team.getDisplayNameKey() + " zu "
    //					+ dyeColor.name() + "§7 geändert.");
    //			return true;
    //		}
    //		return false;
    //	}
}
