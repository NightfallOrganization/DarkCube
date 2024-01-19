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
import eu.darkcube.system.aetheria.manager.CoreManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterLevelManager;
import eu.darkcube.system.aetheria.manager.WorldManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterNameManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterSpawnManager;
import eu.darkcube.system.aetheria.manager.monster.MonsterXPManager;
import eu.darkcube.system.aetheria.manager.player.*;
import eu.darkcube.system.aetheria.other.MonsterLevelListener;
import eu.darkcube.system.aetheria.other.ResourcePackUtil;
import eu.darkcube.system.aetheria.ruler.MainRuler;
import eu.darkcube.system.aetheria.ruler.MainWorldRuler;
import eu.darkcube.system.aetheria.ruler.MonsterWorldRuler;
import eu.darkcube.system.glyphwidthloader.GlyphWidthManager;

public class Aetheria extends DarkCubePlugin {
    private final GlyphWidthManager glyphWidthManager = new GlyphWidthManager();
    private static Aetheria instance;
    private ResourcePackUtil resourcePackUtil;

    public Aetheria() {
        super("aetheria");
        instance = this;
    }

    @Deprecated public static Aetheria getInstance() {
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

        LevelManager levelManager = new LevelManager(this);
        MainRuler mainRuler = new MainRuler(levelManager);
        MainWorldRuler mainWorldRuler = new MainWorldRuler();
        MonsterWorldRuler monsterWorldRuler = new MonsterWorldRuler(this);
        FlyCommand flyCommand = new FlyCommand(this);
        HealthManager healthManager = new HealthManager(this);
        MaxHealthManager maxHealthManager = new MaxHealthManager(this);
        CoreManager coreManager = new CoreManager(this);
        DamageManager damageManager = new DamageManager(this);
        XPManager xpManager = new XPManager(this);
        RegenerationManager regenerationManager = new RegenerationManager(this);
        PlayerManager playerManager = new PlayerManager(this, healthManager, coreManager, damageManager, xpManager, levelManager, maxHealthManager, regenerationManager);
        MonsterLevelManager monsterLevelManager = new MonsterLevelManager(healthManager, maxHealthManager, levelManager, damageManager);
        LevelXPHandler levelXPHandler = new LevelXPHandler(xpManager, levelManager);
        MonsterXPManager monsterXPManager = new MonsterXPManager(xpManager, monsterLevelManager);
        PlayerRegenerationManager playerRegenerationManager = new PlayerRegenerationManager(this, healthManager, maxHealthManager, regenerationManager);
        EntityDamageHandler entityDamageHandler = new EntityDamageHandler(healthManager, damageManager, monsterXPManager, playerRegenerationManager);
        MonsterLevelListener monsterLevelListener = new MonsterLevelListener(monsterLevelManager);
        MonsterNameManager monsterNameManager = new MonsterNameManager(monsterLevelManager);
        MonsterSpawnManager monsterSpawnManager = new MonsterSpawnManager(monsterLevelManager);


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

        instance.getServer().getPluginManager().registerEvents(monsterXPManager, this);
        instance.getServer().getPluginManager().registerEvents(monsterSpawnManager, this);
        instance.getServer().getPluginManager().registerEvents(monsterNameManager, this);
        instance.getServer().getPluginManager().registerEvents(monsterLevelListener, this);
        instance.getServer().getPluginManager().registerEvents(entityDamageHandler, this);
        instance.getServer().getPluginManager().registerEvents(playerManager, this);
        instance.getServer().getPluginManager().registerEvents(flyCommand, this);
        instance.getServer().getPluginManager().registerEvents(monsterWorldRuler, this);
        instance.getServer().getPluginManager().registerEvents(mainWorldRuler, this);
        instance.getServer().getPluginManager().registerEvents(mainRuler, this);
    }

    @Override public void onDisable() {

    }

    public GlyphWidthManager glyphWidthManager() {
        return glyphWidthManager;
    }

}
