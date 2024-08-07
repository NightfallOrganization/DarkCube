/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api;

import eu.darkcube.system.bukkit.Plugin;
import eu.darkcube.system.bukkit.commandapi.deprecated.CommandAPI;
import eu.darkcube.system.stats.api.Arrays.ConvertingRule;
import eu.darkcube.system.stats.api.command.*;
import eu.darkcube.system.stats.api.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class StatsPlugin extends Plugin implements Listener {

    private static StatsPlugin instance;

    private MySQL mysql;

    public StatsPlugin() {
        super("stats");
        instance = this;
    }

    public static StatsPlugin getInstance() {
        return instance;
    }

    @Override public void onEnable() {

        saveDefaultConfig("mysql");
        createConfig("mysql");
        YamlConfiguration cfg = getConfig("mysql");
        mysql = new MySQL(cfg.getString("host"), cfg.getString("port"), cfg.getString("database"), cfg.getString("username"), cfg.getString("password"));
        mysql.connect();
        Bukkit.getPluginManager().registerEvents(this, this);

        Arrays.addConvertingRule(new ConvertingRule<Duration>() {
            @Override public Class<Duration> getConvertingClass() {
                return Duration.class;
            }

            @Override public String convert(Duration object) {
                return object.toString();
            }
        });

        CommandAPI.enable(this, CommandStatsTop.INSTANCE);

        CommandAPI.enable(this, new CommandStats());
        CommandAPI.enable(this, new CommandHStats());
        CommandAPI.enable(this, new CommandDStats());
        CommandAPI.enable(this, new CommandWStats());
        CommandAPI.enable(this, new CommandMStats());
        CommandAPI.enable(this, new CommandYStats());
        CommandAPI.enable(this, new CommandAStats());

    }

    @Override public String getCommandPrefix() {
        return "Stats";
    }

    public MySQL getMySQL() {
        return mysql;
    }
}
