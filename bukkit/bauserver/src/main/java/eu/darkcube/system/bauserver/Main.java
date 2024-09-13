/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver;

import eu.darkcube.system.bauserver.command.BauserverCommand;
import eu.darkcube.system.bauserver.command.HeadsCommand;
import eu.darkcube.system.bauserver.heads.HeadStorage;
import eu.darkcube.system.bauserver.listener.WorldEventListener;
import eu.darkcube.system.bauserver.util.Message;
import eu.darkcube.system.bukkit.Plugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends Plugin {
    private static Main instance;
    private final HeadStorage headStorage;

    public Main() {
        super("bauserver");
        instance = this;
        headStorage = new HeadStorage();
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Message.register();
        CommandAPI.instance().register(new BauserverCommand());
        CommandAPI.instance().register(new HeadsCommand());
        Bukkit.getPluginManager().registerEvents(new WorldEventListener(), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    setupWorld(world);
                }
            }
        }.runTask(this);
        headStorage.load();
    }

    @Override
    public void onDisable() {
        headStorage.save();
    }

    public void setupWorld(World world) {
        world.setFullTime(6000);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.SPAWN_RADIUS, 0);
        world.setStorm(false);
        world.setThundering(false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }

    @Override
    public String getCommandPrefix() {
        return "BauServer";
    }

    public HeadStorage headStorage() {
        return headStorage;
    }
}
