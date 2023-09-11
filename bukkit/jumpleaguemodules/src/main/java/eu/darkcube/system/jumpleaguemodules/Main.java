/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.jumpleaguemodules;

import eu.darkcube.system.DarkCubePlugin;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.UUID;

public class Main extends DarkCubePlugin {

    private static Main instance;
    public static HashMap<UUID, Location> respawnLocations = new HashMap<>();

    public Main() {
        super("jumpleaguemodules");
        instance = this;
    }

    @Override
    public void onEnable() {
        this.getCommand("setheight").setExecutor(new HeightCommand(this));
        this.getCommand("getrespawn").setExecutor(new GetRespawnCommand());

        getServer().getPluginManager().registerEvents(new CustomItem(), this);
        getServer().getPluginManager().registerEvents(new CustomItemListener(this), this);
    }

    @Override
    public void onDisable() {

    }

}

