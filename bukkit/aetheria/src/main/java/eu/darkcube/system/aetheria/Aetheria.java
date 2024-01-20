/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.aetheria.commands.*;
import eu.darkcube.system.aetheria.handler.EntityDamageHandler;
import eu.darkcube.system.aetheria.handler.LevelXPHandler;
import eu.darkcube.system.aetheria.manager.player.CoreManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterLevelManager;
import eu.darkcube.system.aetheria.manager.WorldManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterNameManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterSpawnManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterXPManager;
import eu.darkcube.system.aetheria.manager.player.*;
import eu.darkcube.system.aetheria.other.ActionBarUtil;
import eu.darkcube.system.aetheria.other.PlayerJoinListener;
import eu.darkcube.system.aetheria.other.ResourcePackUtil;
import eu.darkcube.system.aetheria.ruler.MainRuler;
import eu.darkcube.system.aetheria.ruler.MainWorldRuler;
import eu.darkcube.system.aetheria.ruler.MonsterWorldRuler;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.glyphwidthloader.GlyphWidthManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Aetheria extends DarkCubePlugin {
    private final GlyphWidthManager glyphWidthManager = new GlyphWidthManager();
    private static Aetheria instance;
    private ResourcePackUtil resourcePackUtil;
    private final GlyphCommand glyphCommand = new GlyphCommand(this);
    private final ResourcePackCommand resourcePackCommand = new ResourcePackCommand(this);

    public Aetheria() {
        super("aetheria");
        instance = this;
    }

    public static Aetheria getInstance() {
        return instance;
    }

    public ResourcePackUtil resourcePackUtil() {
        return resourcePackUtil;
    }

    @Override public void onEnable() {
        WorldManager.loadWorlds();

        resourcePackUtil = new ResourcePackUtil(this);
        try {
            glyphWidthManager.loadGlyphDataFromClassLoader(getClassLoader(), "glyph-widths.bin");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        var levelManager = new LevelManager(this);
        var mainRuler = new MainRuler(levelManager);
        var mainWorldRuler = new MainWorldRuler();
        var monsterWorldRuler = new MonsterWorldRuler(this);
        var flyCommand = new FlyCommand(this);
        var healthManager = new HealthManager(this);
        var maxHealthManager = new MaxHealthManager(this);
        var coreManager = new CoreManager(this);
        var damageManager = new DamageManager(this);
        var xpManager = new XPManager(this, levelManager);
        var regenerationManager = new RegenerationManager(this);
        var playerManager = new PlayerManager(this, healthManager, coreManager, damageManager, xpManager, levelManager, maxHealthManager, regenerationManager);
        var monsterLevelManager = new MonsterLevelManager(healthManager, maxHealthManager, levelManager, damageManager);
        var monsterXPManager = new MonsterXPManager(xpManager, monsterLevelManager);
        var playerRegenerationManager = new PlayerRegenerationManager(this, healthManager, maxHealthManager, regenerationManager);
        var playerDeathManager = new PlayerDeathManager(coreManager, xpManager, healthManager, maxHealthManager);
        var entityDamageHandler = new EntityDamageHandler(healthManager, damageManager, monsterXPManager, playerRegenerationManager, playerDeathManager);
        var dataModeCommand = new DataModeCommand(monsterLevelManager);
        var monsterNameManager = new MonsterNameManager(monsterLevelManager);
        var monsterSpawnManager = new MonsterSpawnManager(monsterLevelManager);
        var playerJoinListener = new PlayerJoinListener(this, playerRegenerationManager);
        var actionBarUtil = new ActionBarUtil(this, healthManager, levelManager, maxHealthManager, xpManager);

        instance.getCommand("gm").setExecutor(new GMCommand());
        instance.getCommand("world").setExecutor(new WorldCommand());
        instance.getCommand("god").setExecutor(new GodCommand());
        instance.getCommand("fly").setExecutor(flyCommand);
        instance.getCommand("stats").setExecutor(new StatsCommand(playerManager));
        instance.getCommand("setdamage").setExecutor(new SetDamageCommand(damageManager));
        instance.getCommand("sethealth").setExecutor(new SetHealthCommand(healthManager, maxHealthManager));
        instance.getCommand("mobspawn").setExecutor(new MobSpawnCommand(monsterLevelManager));
        instance.getCommand("killmobs").setExecutor(new KillMobsCommand());
        instance.getCommand("setxp").setExecutor(new SetXPCommand(xpManager));
        instance.getCommand("setregeneration").setExecutor(new SetRegenerationCommand(regenerationManager));
        instance.getCommand("setlevel").setExecutor(new SetLevelCommand(levelManager));
        instance.getCommand("datamode").setExecutor(dataModeCommand);
        instance.getCommand("setcore").setExecutor(new SetCoreCommand(coreManager));

        instance.getServer().getPluginManager().registerEvents(playerJoinListener, this);
        instance.getServer().getPluginManager().registerEvents(monsterXPManager, this);
        instance.getServer().getPluginManager().registerEvents(monsterSpawnManager, this);
        instance.getServer().getPluginManager().registerEvents(monsterNameManager, this);
        instance.getServer().getPluginManager().registerEvents(dataModeCommand, this);
        instance.getServer().getPluginManager().registerEvents(entityDamageHandler, this);
        instance.getServer().getPluginManager().registerEvents(playerManager, this);
        instance.getServer().getPluginManager().registerEvents(flyCommand, this);
        instance.getServer().getPluginManager().registerEvents(monsterWorldRuler, this);
        instance.getServer().getPluginManager().registerEvents(mainWorldRuler, this);
        instance.getServer().getPluginManager().registerEvents(mainRuler, this);

        CommandAPI.instance().register(glyphCommand);
        CommandAPI.instance().register(resourcePackCommand);
    }

    @Override public void onDisable() {
        CommandAPI.instance().unregister(glyphCommand);
        CommandAPI.instance().unregister(resourcePackCommand);
    }

    public GlyphWidthManager glyphWidthManager() {
        return glyphWidthManager;
    }

}
