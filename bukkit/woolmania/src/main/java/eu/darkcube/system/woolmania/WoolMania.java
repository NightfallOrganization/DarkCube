/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania;

import java.util.HashMap;
import java.util.Map;
import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.woolmania.listener.JoinListener;
import eu.darkcube.system.woolmania.listener.LeaveListener;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.entity.Player;

public class WoolMania extends DarkCubePlugin {
    private static WoolMania instance;
    public static Map<Player, WoolManiaPlayer> woolManiaPlayerMap = new HashMap<>();

    public WoolMania() {
        super("woolmania");
        instance = this;
    }

    @Override
    public void onEnable() {

        JoinListener joinListener = new JoinListener();
        LeaveListener leaveListener = new LeaveListener();

        instance.getServer().getPluginManager().registerEvents(joinListener, this);
        instance.getServer().getPluginManager().registerEvents(leaveListener, this);

    }

    public static WoolMania getInstance() {
        return instance;
    }
}
