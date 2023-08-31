/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.jumpleaguemodules;

import eu.darkcube.system.DarkCubePlugin;

public class Main extends DarkCubePlugin {

    private static Main instance;

    public Main() {
        super("jumpleaguemodules");
        instance = this;
    }

    @Override
    public void onEnable() {
        this.getCommand("getrespawn").setExecutor(new GetRespawnCommand());
        getServer().getPluginManager().registerEvents(new CustomItem(), this);
    }

    @Override
    public void onDisable() {

    }

}

