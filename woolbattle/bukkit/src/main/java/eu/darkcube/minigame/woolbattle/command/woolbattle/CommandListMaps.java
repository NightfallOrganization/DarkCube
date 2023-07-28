/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

import java.util.Collection;

public class CommandListMaps extends WBCommandExecutor {
    public CommandListMaps() {
        super("listMaps", b -> b.executes(ctx -> {
            Component c = Component.empty();
            Collection<? extends Map> maps = WoolBattle.instance().mapManager().getMaps();
            if (maps.isEmpty()) {
                c = Component.text("Es sind keine Maps erstellt");
            } else {
                for (Map map : maps) {
                    c = c.append(Component.text(" - " + map.serialize()))
                            .append(Component.newline());
                }
            }
            ctx.getSource().sendMessage(c);
            return 0;
        }));
    }
    //	public CommandListMaps() {
    //		super(WoolBattle.getInstance(), "listMaps", new Command[0], "Listet alle Maps auf");
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		Collection<? extends Map> maps = WoolBattle.getInstance().getMapManager().getMaps();
    //		if (maps.size() == 0) {
    //			sender.sendMessage("§cEs sind keine Teams erstellt!");
    //			return true;
    //		}
    //		StringBuilder b = new StringBuilder();
    //		for (Map m : maps) {
    //			b.append("§7- Name: '§5").append(m.getName()).append("§7', DeathHeight: §5").append(m.getDeathHeight())
    //					.append("§7, Icon: ").append(m.getIcon()).append("\n");
    //		}
    //		sender.sendMessage(b.toString());
    //		return true;
    //	}

}
