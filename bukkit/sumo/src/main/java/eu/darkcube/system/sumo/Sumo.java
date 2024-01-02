/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.sumo.commands.MapVoteCommand;
import eu.darkcube.system.sumo.commands.ShowActiveMapCommand;
import eu.darkcube.system.sumo.commands.TpActiveWorldCommand;
import eu.darkcube.system.sumo.loader.MapLoader;
import eu.darkcube.system.sumo.ruler.MainRuler;
import eu.darkcube.system.sumo.ruler.MapRuler;

public class Sumo extends DarkCubePlugin {
    private static Sumo instance;


    public Sumo() {
        super("sumo");
    }

    @Override
    public void onEnable() {
        instance = this;

        MapLoader mapLoader = new MapLoader();
        mapLoader.loadWorlds();
        MainRuler mainRuler = new MainRuler();

        getServer().getPluginManager().registerEvents(new MapRuler(mainRuler), this);

        instance.getCommand("mapvote").setExecutor(new MapVoteCommand(mainRuler));
        instance.getCommand("showactivemap").setExecutor(new ShowActiveMapCommand(mainRuler));
        instance.getCommand("tpactiveworld").setExecutor(new TpActiveWorldCommand(mainRuler));
    }


}