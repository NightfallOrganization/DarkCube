/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.glyphwidthloader.GlyphWidthManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class Citybuild extends JavaPlugin {

    private static Citybuild instance;
    private final GlyphWidthManager glyphWidthManager = new GlyphWidthManager();
    private final GlyphCommand glyphCommand = new GlyphCommand(this);
    private LevelXPManager levelXPManager;
    private PlayerOnlineTimeTracker playerOnlineTimeTracker;
    private CustomItemManager customItemManager;
    private DefenseManager defenseManager;
    private NamespacedKey aroundDamageKey;

    public Citybuild() {
        instance = this;
    }

    public static Citybuild getInstance() {
        return instance;
    }

    public DefenseManager getDefenseManager() {
        return this.defenseManager;// 33
    }

    public NamespacedKey getAroundDamageKey() {
        return this.aroundDamageKey;// 37
    }

    public PlayerOnlineTimeTracker getPlayerOnlineTimeTracker() {
        return playerOnlineTimeTracker;
    }

    @Override public void onEnable() {
        try {
            glyphWidthManager.loadGlyphDataFromClassLoader(getClassLoader(), "glyph-widths.bin");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.levelXPManager = new LevelXPManager(this);// 42
        CustomHealthManager healthManager = new CustomHealthManager(this);// 43
        AttributeCommand attributeCommand = new AttributeCommand(this, healthManager);// 44
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");// 46
        AroundDamageCommands aroundDamageCommands = new AroundDamageCommands(this);// 47
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");// 48
        this.defenseManager = new DefenseManager(this);// 49
        instance.getCommand("gm").setExecutor(new GM());// 51
        instance.getCommand("heal").setExecutor(new Heal(healthManager));// 52
        instance.getCommand("day").setExecutor(new Day());// 53
        instance.getCommand("night").setExecutor(new Night());// 54
        instance.getCommand("god").setExecutor(new God());// 55
        instance.getCommand("fly").setExecutor(new Fly());// 56
        instance.getCommand("feed").setExecutor(new Feed());// 57
        instance.getCommand("max").setExecutor(new Max());// 58
        instance.getCommand("trash").setExecutor(new Trash());// 59
        instance.getCommand("warp").setExecutor(new Warp());// 60
        instance.getCommand("spawn").setExecutor(new SpawnCommand());// 61
        instance.getCommand("getitem").setExecutor(new GetItem());// 62
        instance.getCommand("killmobs").setExecutor(new KillMobs());// 63
        instance.getCommand("addxp").setExecutor(new AddXPCommand(this.levelXPManager));// 64
        instance.getCommand("mylevel").setExecutor(new MyLevelCommand(this.levelXPManager));// 65
        instance.getCommand("resetlevel").setExecutor(new ResetLevelCommand(this.levelXPManager, healthManager, this.defenseManager));// 66
        instance.getCommand("myxp").setExecutor(new MyXPCommand(this.levelXPManager));// 67
        instance.getCommand("myap").setExecutor(new MyAPCommand(this.levelXPManager));// 68
        instance.getCommand("attribute").setExecutor(attributeCommand);// 69
        instance.getCommand("addhealth").setExecutor(new AddHealthCommand(healthManager));// 70
        instance.getCommand("myhealth").setExecutor(new MyHealthCommand(healthManager));// 71
        instance.getCommand("addregeneration").setExecutor(new RegenerationCommand(healthManager));// 72
        instance.getCommand("addarounddamage").setExecutor(aroundDamageCommands);// 73
        instance.getCommand("myarounddamage").setExecutor(aroundDamageCommands);// 74
        instance.getCommand("adddefense").setExecutor(new AddDefenseCommand(this));// 75
        instance.getCommand("mydefense").setExecutor(new MyDefenseCommand(this.defenseManager));// 76
        instance.getCommand("myregeneration").setExecutor(new MyRegenCommand(healthManager));// 77

        CommandAPI.instance().register(glyphCommand);

        (new ActionBarTask("Ⲓ", "Ⲕ")).runTaskTimer(this, 0L, 3L);// 79
        this.customItemManager = new CustomItemManager(this);// 81
        this.customItemManager.registerItems();// 82
        BackpackListener backpackListener = new BackpackListener(this.customItemManager);// 85
        backpackListener.loadInventories();// 86
        this.getServer().getPluginManager().registerEvents(backpackListener, this);// 89
        ScoreboardHandler scoreboardHandler = new ScoreboardHandler();// 91
        this.playerOnlineTimeTracker = new PlayerOnlineTimeTracker(scoreboardHandler);// 92
        Iterator var6 = this.getServer().getOnlinePlayers().iterator();// 93

        while(var6.hasNext()) {
            Player player = (Player)var6.next();
            scoreboardHandler.showPlayerLevelScoreboard(player);// 94
        }

        Fly flyCommand = new Fly();// 98
        this.getServer().getPluginManager().registerEvents(flyCommand, this);// 99
        this.getCommand("fly").setExecutor(flyCommand);// 100
        (new RingOfHealingEffectApplier(this)).runTaskTimer(this, 0L, 1L);// 103
        LevelXPManager levelXPManager = new LevelXPManager(this);// 105
        MonsterLevelHandler monsterLevelHandler = new MonsterLevelHandler(levelXPManager);// 106
        this.getServer().getPluginManager().registerEvents(monsterLevelHandler, this);// 107
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");// 109
        AroundDamageHandler aroundDamageHandlerWithKey = new AroundDamageHandler(this, this.getAroundDamageKey());// 110
        AroundDamageHandler aroundDamageHandler = new AroundDamageHandler(this, this.getAroundDamageKey());// 111
        this.getServer().getPluginManager().registerEvents(aroundDamageHandler, this);// 112
        this.getServer().getPluginManager().registerEvents(aroundDamageHandlerWithKey, this);// 113
        this.getServer().getPluginManager().registerEvents(new DefenseListener(this.defenseManager), this);// 115
        this.getServer().getPluginManager().registerEvents(new DefenseListener(this.defenseManager), this);// 116
        this.getServer().getPluginManager().registerEvents(new AroundDamageHandler(this, this.getAroundDamageKey()), this);// 117
        this.getServer().getPluginManager().registerEvents(new NoEffectPlugin(), this);// 118
        this.getServer().getPluginManager().registerEvents(new ConstantHunger(), this);// 119
        this.getServer().getPluginManager().registerEvents(new CustomDeathMessage(), this);// 120
        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(healthManager), this);// 121
        this.getServer().getPluginManager().registerEvents(new DamageListener(healthManager), this);// 122
        this.getServer().getPluginManager().registerEvents(new PlayerJoinHealthSetupListener(healthManager), this);// 123
        this.getServer().getPluginManager().registerEvents(attributeCommand, this);// 124
        this.getServer().getPluginManager().registerEvents(new CustomMonsterSpawn(), this);// 125
        this.getServer().getPluginManager().registerEvents(new NoMonsterSpawn(), this);// 126
        this.getServer().getPluginManager().registerEvents(new NoLeafDecayListener(), this);// 127
        this.getServer().getPluginManager().registerEvents(new NoMobDropsListener(), this);// 128
        this.getServer().getPluginManager().registerEvents(levelXPManager, this);// 129
        this.getServer().getPluginManager().registerEvents(new EndermanBlockPickupListener(), this);// 130
        this.getServer().getPluginManager().registerEvents(new NoXpListener(), this);// 131
        this.getServer().getPluginManager().registerEvents(new NoFriendlyFireHandler(), this);// 132
        this.getServer().getPluginManager().registerEvents(new TimeHandler(), this);// 133
        this.getServer().getPluginManager().registerEvents(new SwiftSwordListener(), this);// 134
        this.getServer().getPluginManager().registerEvents(new RingOfHealingSwapHandler(), this);// 135
        this.getServer().getPluginManager().registerEvents(new RingOfHealingListener(this), this);// 136
        this.getServer().getPluginManager().registerEvents(new EnderBagListener(), this);// 137
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);// 138
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(scoreboardHandler), this);// 139
        this.getServer().getPluginManager().registerEvents(new PlayerLevelChangeListener(scoreboardHandler), this);// 140
        this.getServer().getPluginManager().registerEvents(this.playerOnlineTimeTracker, this);// 141
        (new ScoreboardUpdater(scoreboardHandler)).runTaskTimer(this, 0L, 9000L);// 144
    }// 146

    @Override public void onDisable() {
        CommandAPI.instance().unregister(glyphCommand);
        // Speichern der Inventare
        for (RegisteredListener registeredListener : HandlerList.getRegisteredListeners(this)) {
            Listener listener = registeredListener.getListener();
            if (listener instanceof BackpackListener) {
                ((BackpackListener) listener).saveInventories();
                break;
            }
        }
        customItemManager.unloadRecipes();
    }

    public GlyphWidthManager glyphWidthManager() {
        return glyphWidthManager;
    }
}
