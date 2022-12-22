/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.miners.command.CommandTeam;
import eu.darkcube.system.miners.command.CommandTest;
import eu.darkcube.system.miners.command.CommandTimer;
import eu.darkcube.system.miners.gamephase.GameUpdater;
import eu.darkcube.system.miners.gamephase.end.EndPhase;
import eu.darkcube.system.miners.gamephase.lobbyphase.Lobbyphase;
import eu.darkcube.system.miners.gamephase.miningphase.Miningphase;
import eu.darkcube.system.miners.gamephase.pvpphase.PVPPhase;
import eu.darkcube.system.miners.listener.*;
import eu.darkcube.system.miners.player.Message;
import eu.darkcube.system.miners.player.PlayerManager;
import eu.darkcube.system.miners.player.TNTManager;
import eu.darkcube.system.miners.player.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Miners extends DarkCubePlugin {

    private static Miners instance;

    private static int gamephase = 0;

    private static PlayerManager playerManager;
    private static TeamManager teamManager;

    private static Lobbyphase gamephaseLobby;
    private static Miningphase gamephaseMining;
    private static PVPPhase gamephasePVP;
    private static EndPhase gamephaseEnd;
    private static GameUpdater gameUpdater;

    private static MinersConfig minersConfig;

    public static final String MINING_WORLD_NAME = "MinersCubes";
    public static final String PVP_WORLD_NAME = "MinersPVP";


    public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Miners" + ChatColor.GRAY + "] " + ChatColor.RESET;

    public Miners() {
        instance = this;
    }

    @Override
    public void onLoad() {
        this.saveDefaultConfig("config");
        this.createConfig("config");
        minersConfig = new MinersConfig();

        try {
            Language.GERMAN.registerLookup(this.getClassLoader(), "messages_de.properties", Message.KEY_MODIFIER);
            Language.ENGLISH.registerLookup(this.getClassLoader(), "messages_en.properties", Message.KEY_MODIFIER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        playerManager = new PlayerManager();
        teamManager = new TeamManager();

        CommandAPI api = CommandAPI.getInstance();
        api.register(new CommandTest());
        api.register(new CommandTimer());
        api.register(new CommandTeam());

        registerListeners(new ListenerPlayerQuit(), new ListenerPlayerLogin(), new ListenerPlayerJoin(), new ListenerBlockBreak(), new ListenerPlaceBlock(),
                new ListenerItemInteract(), new ListenerPlayerDamage(), new ListenerChatMessage(), new ListenerCrafting(), new ListenerItemPickup());
        registerListeners(new TNTManager());

        Bukkit.createWorld(new WorldCreator(MINING_WORLD_NAME));
        Bukkit.createWorld(new WorldCreator(PVP_WORLD_NAME));

        gamephaseLobby = new Lobbyphase();
        gamephaseMining = new Miningphase();
        gamephasePVP = new PVPPhase();
        gamephaseEnd = new EndPhase();
        gameUpdater = new GameUpdater();

        Bukkit.getWorlds().forEach(w -> {
            w.setGameRuleValue("doMobSpawning", "false");
            w.setGameRuleValue("doTileDrops", "false");
            w.setGameRuleValue("naturalRegeneration", "false");
        });

    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer(""));
    }

    public static Miners getInstance() {
        return instance;
    }

    public static MinersConfig getMinersConfig() {
        return minersConfig;
    }

    public static int getGamephase() {
        return gamephase;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static TeamManager getTeamManager() {
        return teamManager;
    }

    public static Lobbyphase getLobbyPhase() {
        return gamephaseLobby;
    }

    public static Miningphase getMiningPhase() {
        return gamephaseMining;
    }

    public static PVPPhase getPVPPhase() {
        return gamephasePVP;
    }

    public static EndPhase getEndPhase() {
        return gamephaseEnd;
    }

    public static void nextGamephase() {
        switch (gamephase) {
            case 0:
                gamephaseLobby.disable();
                gamephaseMining.enable();
                gameUpdater.start();
                break;
            case 1:
                gamephaseMining.disable();
                gamephasePVP.enable();
                break;
            case 2:
                gamephaseEnd.enable();
                gameUpdater.stop();
                break;
        }
        gamephase++;
    }

    public static void endGame() {
        gamephase = 3;
        gamephaseLobby.disable();
        gamephaseMining.disable();
        gamephasePVP.disable();
        gamephaseEnd.enable();
        gameUpdater.stop();
    }

    private static void registerListeners(Listener... listeners) {
        for (Listener l : listeners)
            Miners.getInstance().getServer().getPluginManager().registerEvents(l, Miners.getInstance());
    }

    public static ArrayList<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static void sendTranslatedMessage(Player player, Message message, Object... replacements) {
        player.sendMessage(PREFIX + message.getMessage(player, replacements));
    }

    public static void sendTranslatedMessageAll(Message message, Object... replacements) {
        Bukkit.getOnlinePlayers().forEach(p -> sendTranslatedMessage(p, message, replacements));
        getInstance().sendConsole(message.getServerMessage(replacements));
    }

    public static void log(Object o) {
        System.out.println("[Miners] " + o.toString());
    }

}
