/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.bauserver;

import eu.darkcube.system.bauserver.command.CommandBauserver;
import eu.darkcube.system.bauserver.listener.WorldEventListener;
import eu.darkcube.system.bukkit.Plugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends Plugin {
    public Main() {
        super("bauserver");
    }

    public static Main getInstance() {
        return Main.getPlugin(Main.class);
    }

    @Override public void onEnable() {
        CommandAPI.instance().register(new CommandBauserver());
        Bukkit.getPluginManager().registerEvents(new WorldEventListener(), this);
        new BukkitRunnable() {
            @Override public void run() {
                for (World world : Bukkit.getWorlds()) {
                    setupWorld(world);
                }
            }
        }.runTask(this);
    }

    public void setupWorld(World world) {
        world.setFullTime(6000);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setKeepSpawnInMemory(false);
        world.setStorm(false);
        world.setThundering(false);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("mobGriefing", "false");
    }

    @Override public String getCommandPrefix() {
        return "BauServer";
    }

}
