/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.jumpleaguemodules;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import org.bukkit.Location;

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

        //        if (getServer().getWorld("world") != null) {
        //            // Wenn die Welt "world" existiert, dann lösche sie
        //            if (!deleteWorld(new File(getServer().getWorld("world").getName()))) {
        //                // Wenn es Probleme beim Löschen gibt, logge eine Nachricht
        //                getLogger().warning("Could not delete the existing 'world'.");
        //            }
        //        }
        //
        //        WorldCreator creator = new WorldCreator("world");
        //        creator.generator(new VoidWorldGenerator());
        //        getServer().createWorld(creator);
    }

    public boolean deleteWorld(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    @Override
    public void onDisable() {

    }

}

