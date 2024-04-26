/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.onlinetime;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.vanillaaddons.module.Module;
import eu.darkcube.system.vanillaaddons.util.Message;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class OnlinetimeModule extends Command implements Module {
    private static final Message ONLINETIME = new Message("COMMAND_ONLINETIME");

    public OnlinetimeModule() {
        super("vanillaaddons", "onlinetime", new String[]{"ontime"}, b -> b.executes(ctx -> {
            Player player = ctx.getSource().asPlayer();
            int totalSecs = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;// 36
            int days = totalSecs / 86400;
            int hours = totalSecs % 86400 / 3600;// 37
            int minutes = totalSecs % 3600 / 60;// 38

            ctx.getSource().sendMessage(ONLINETIME, days, hours, minutes);

            //			player.sendMessage(
            //					ChatColor.WHITE + "You have been playing for " + ChatColor.GREEN +
            //					builder);
            return 0;
        }));
    }

    @Override public void onEnable() {
        CommandAPI.instance().register(this);
    }

    @Override public void onDisable() {
        CommandAPI.instance().unregister(this);
    }
}
