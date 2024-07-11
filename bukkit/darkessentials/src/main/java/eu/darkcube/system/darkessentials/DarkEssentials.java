/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.darkessentials.command.DayCommand;
import eu.darkcube.system.darkessentials.command.FeedCommand;
import eu.darkcube.system.darkessentials.command.FlyCommand;
import eu.darkcube.system.darkessentials.command.GameModeCommand;
import eu.darkcube.system.darkessentials.command.HealCommand;
import eu.darkcube.system.darkessentials.command.InvseeCommand;
import eu.darkcube.system.darkessentials.command.MaxCommand;
import eu.darkcube.system.darkessentials.command.NightCommand;
import eu.darkcube.system.darkessentials.command.PingCommand;
import eu.darkcube.system.darkessentials.command.SpeedCommand;
import eu.darkcube.system.darkessentials.command.TpWorldCommand;
import eu.darkcube.system.darkessentials.manager.BanManager;
import eu.darkcube.system.darkessentials.util.LanguageHelper;

public class DarkEssentials extends DarkCubePlugin {
    private static DarkEssentials instance;

    public DarkEssentials() {
        super("darkessentials");
        instance = this;
    }

    @Override
    public void onEnable() {

        LanguageHelper.initialize();

        CommandAPI.instance().register(new DayCommand());
        CommandAPI.instance().register(new NightCommand());
        CommandAPI.instance().register(new GameModeCommand());
        CommandAPI.instance().register(new FlyCommand());
        CommandAPI.instance().register(new FeedCommand());
        CommandAPI.instance().register(new TpWorldCommand());
        CommandAPI.instance().register(new HealCommand());
        CommandAPI.instance().register(new MaxCommand());
        CommandAPI.instance().register(new InvseeCommand());
        CommandAPI.instance().register(new PingCommand());
        CommandAPI.instance().register(new SpeedCommand());

        BanManager banManager = new BanManager();

        instance.getServer().getPluginManager().registerEvents(banManager, this);

    }

}
