/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Locale;

public class CommandInfo extends WBCommandExecutor {
    public CommandInfo() {
        super("info", b -> b.executes(ctx -> {
            Map map = MapArgument.getMap(ctx, "map");
            String sb = "§aMap: " + map.getName() + "\nIcon: " + map.getIcon().getType().name().toLowerCase(Locale.ROOT) +
                    "DeathHeight: " + (map.ingameData() == null ? "Unknown" : map.deathHeight()) + "\nAktiviert: " +
                    map.isEnabled();
            ctx.getSource().sendMessage(
                    LegacyComponentSerializer.legacySection().deserialize(sb));
            return 0;
        }));
    }
    //	public CommandInfo() {
    //		super(WoolBattle.getInstance(), "info", new Command[0], "Ruft die Informationen der Map ab");
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		if (args.length == 0) {
    //			Map map = WoolBattle.getInstance().getMapManager().getMap(getSpaced());
    //			if (map == null) {
    //				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
    //				return true;
    //			}
    //			StringBuilder b = new StringBuilder();
    //			b.append("§aMap: ").append(map.getName()).append("\nIcon: ").append(map.getIcon()).append("DeathHeight")
    //					.append(map.getDeathHeight()).append("\nAktiviert: ").append(map.isEnabled());
    //			sender.sendMessage(b.toString());
    //			return true;
    //		}
    //		return false;
    //	}

}
