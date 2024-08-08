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
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.woolmania.commands.ResetHallsCommand;
import eu.darkcube.system.woolmania.commands.zenum.ZenumCommand;
import eu.darkcube.system.woolmania.listener.BlockBreakListener;
import eu.darkcube.system.woolmania.listener.JoinListener;
import eu.darkcube.system.woolmania.listener.LeaveListener;
import eu.darkcube.system.woolmania.manager.NPCListeners;
import eu.darkcube.system.woolmania.manager.NPCManager;
import eu.darkcube.system.woolmania.manager.NPCRemover;
import eu.darkcube.system.woolmania.manager.WorldManager;
import eu.darkcube.system.woolmania.npc.NPCCreator;
import eu.darkcube.system.woolmania.util.Ruler;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.WoolRegenerationTimer;
import eu.darkcube.system.woolmania.util.message.LanguageHelper;
import org.bukkit.entity.Player;

public class WoolMania extends DarkCubePlugin {
    private static WoolMania instance;
    private NPCCreator npcCreator;
    public Map<Player, WoolManiaPlayer> woolManiaPlayerMap = new HashMap<>();

    public WoolMania() {
        super("woolmania");
        instance = this;
    }

    @Override
    public void onEnable() {
        LanguageHelper.initialize();
        WorldManager.loadWorlds();
        WoolRegenerationTimer.startFirstTimer();
        var npcManager = new NPCManager(this);
        npcCreator = new NPCCreator(npcManager);
        NPCListeners.register(npcManager, npcCreator);

        npcCreator.createNPC();

        var joinListener = new JoinListener();
        var leaveListener = new LeaveListener();
        var blockBreakListener = new BlockBreakListener();
        var ruler = new Ruler();

        instance.getServer().getPluginManager().registerEvents(blockBreakListener, this);
        instance.getServer().getPluginManager().registerEvents(joinListener, this);
        instance.getServer().getPluginManager().registerEvents(leaveListener, this);
        instance.getServer().getPluginManager().registerEvents(ruler, this);

        CommandAPI.instance().register(new ResetHallsCommand());
        CommandAPI.instance().register(new ZenumCommand());
    }

    @Override
    public void onDisable() {
        NPCRemover.destoryNPCs(npcCreator);
    }

    public static WoolMania getInstance() {
        return instance;
    }

    public WoolManiaPlayer getPlayer(Player player) {
        return woolManiaPlayerMap.get(player);
    }

    public static WoolManiaPlayer getStaticPlayer(Player player) {
        return WoolMania.getInstance().woolManiaPlayerMap.get(player);
    }

}
