/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import eu.darkcube.system.citybuild.commands.*;
import eu.darkcube.system.citybuild.scheduler.ActionBarTask;
import eu.darkcube.system.citybuild.scheduler.RingOfHealingEffectApplier;
import eu.darkcube.system.citybuild.scheduler.ScoreboardUpdater;
import eu.darkcube.system.citybuild.util.CustomHealthManager;
import eu.darkcube.system.citybuild.util.CustomItemManager;
import eu.darkcube.system.citybuild.util.DefenseManager;
import eu.darkcube.system.citybuild.util.LevelXPManager;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.glyphwidthloader.GlyphWidthManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public class Citybuild extends JavaPlugin {

    private static Citybuild instance;
    private final GlyphWidthManager glyphWidthManager = new GlyphWidthManager();
    private final GlyphCommand glyphCommand = new GlyphCommand(this);
    private LevelXPManager levelXPManager;
    private PlayerOnlineTimeTrackerListener playerOnlineTimeTrackerListener;
    private CustomItemManager customItemManager;
    private DefenseManager defenseManager;
    private NamespacedKey aroundDamageKey;

    public Citybuild() {
        instance = this;
    }

    private CustomHealthManager healthManager;

    public static Citybuild getInstance() {
        return instance;
    }

    public DefenseManager getDefenseManager() {
        return this.defenseManager;
    }

    public NamespacedKey getAroundDamageKey() {
        return this.aroundDamageKey;
    }

    public PlayerOnlineTimeTrackerListener getPlayerOnlineTimeTracker() {
        return playerOnlineTimeTrackerListener;
    }

    @Override public void onEnable() {
        try {
            glyphWidthManager.loadGlyphDataFromClassLoader(getClassLoader(), "glyph-widths.bin");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        this.levelXPManager = new LevelXPManager(this);
        healthManager = new CustomHealthManager(this);
        AttributeCommand attributeCommand = new AttributeCommand(this, healthManager);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");
        AroundDamageCommands aroundDamageCommands = new AroundDamageCommands(this);
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");
        this.defenseManager = new DefenseManager(this);

        instance.getCommand("gm").setExecutor(new GMCommand());
        instance.getCommand("heal").setExecutor(new HealCommand(healthManager));
        instance.getCommand("day").setExecutor(new DayCommand());
        instance.getCommand("night").setExecutor(new NightCommand());
        instance.getCommand("god").setExecutor(new GodCommand());
        instance.getCommand("fly").setExecutor(new FlyCommand());
        instance.getCommand("feed").setExecutor(new FeedCommand());
        instance.getCommand("max").setExecutor(new MaxCommand());
        instance.getCommand("trash").setExecutor(new TrashCommand());
        instance.getCommand("warp").setExecutor(new WarpCommand());
        instance.getCommand("spawn").setExecutor(new SpawnCommand());
        instance.getCommand("getitem").setExecutor(new GetItemCommand());
        instance.getCommand("killmobs").setExecutor(new KillMobsCommand());
        instance.getCommand("addxp").setExecutor(new AddXPCommand(this.levelXPManager));
        instance.getCommand("mylevel").setExecutor(new MyLevelCommand(this.levelXPManager));
        instance.getCommand("resetlevel").setExecutor(new ResetLevelCommand(this.levelXPManager, healthManager, this.defenseManager, this));
        instance.getCommand("myxp").setExecutor(new MyXPCommand(this.levelXPManager));
        instance.getCommand("myap").setExecutor(new MyAPCommand(this.levelXPManager));
        instance.getCommand("attribute").setExecutor(attributeCommand);
        instance.getCommand("addhealth").setExecutor(new AddHealthCommand(healthManager));
        instance.getCommand("myhealth").setExecutor(new MyHealthCommand(healthManager));
        instance.getCommand("addregeneration").setExecutor(new RegenerationCommand(healthManager));
        instance.getCommand("addarounddamage").setExecutor(aroundDamageCommands);
        instance.getCommand("myarounddamage").setExecutor(aroundDamageCommands);
        instance.getCommand("adddefense").setExecutor(new AddDefenseCommand(this));
        instance.getCommand("mydefense").setExecutor(new MyDefenseCommand(this.defenseManager));
        instance.getCommand("myregeneration").setExecutor(new MyRegenCommand(healthManager));

        CommandAPI.instance().register(glyphCommand);

        this.customItemManager = new CustomItemManager(this);
        this.customItemManager.registerItems();
        ScoreboardListener scoreboardListener = new ScoreboardListener();
        this.playerOnlineTimeTrackerListener = new PlayerOnlineTimeTrackerListener(scoreboardListener);
        Iterator var6 = this.getServer().getOnlinePlayers().iterator();

        while(var6.hasNext()) {
            Player player = (Player)var6.next();
            scoreboardListener.showPlayerLevelScoreboard(player);
        }

        FlyCommand flyCommand = new FlyCommand();// 98
        this.getServer().getPluginManager().registerEvents(flyCommand, this);
        this.getCommand("fly").setExecutor(flyCommand);
        (new RingOfHealingEffectApplier(this)).runTaskTimer(this, 0L, 1L);
        LevelXPManager levelXPManager = new LevelXPManager(this);
        MonsterLevelListener monsterLevelListener = new MonsterLevelListener(levelXPManager, healthManager);
        this.getServer().getPluginManager().registerEvents(monsterLevelListener, this);
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");
        AroundDamageListener aroundDamageListenerWithKey = new AroundDamageListener(this, this.getAroundDamageKey());
        AroundDamageListener aroundDamageListener = new AroundDamageListener(this, this.getAroundDamageKey());
        new BagListener(this);

        ActionBarTask actionBarTask = new ActionBarTask(this, healthManager, levelXPManager);
        actionBarTask.runTaskTimer(this, 0L, 3L);

        this.getServer().getPluginManager().registerEvents(new SchadensAnzeigeListener(), this);
        this.getServer().getPluginManager().registerEvents(new CraftingTableListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityHealListener(), this);
     //   this.getServer().getPluginManager().registerEvents(new KnockbackListener(), this);
        this.getServer().getPluginManager().registerEvents(new SoundListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinHealthSetupListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(aroundDamageListener, this);
        this.getServer().getPluginManager().registerEvents(aroundDamageListenerWithKey, this);
        this.getServer().getPluginManager().registerEvents(new DefenseListener(this.defenseManager), this);
        this.getServer().getPluginManager().registerEvents(new DefenseListener(this.defenseManager), this);
        this.getServer().getPluginManager().registerEvents(new AroundDamageListener(this, this.getAroundDamageKey()), this);
        this.getServer().getPluginManager().registerEvents(new NoEffectListener(), this);
        this.getServer().getPluginManager().registerEvents(new ConstantHungerListener(), this);
        this.getServer().getPluginManager().registerEvents(new CustomDeathMessageListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(new DamageListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinHealthSetupListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(attributeCommand, this);
        this.getServer().getPluginManager().registerEvents(new CustomMonsterSpawnListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoMonsterSpawnListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoLeafDecayListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoMobDropsListener(), this);
        this.getServer().getPluginManager().registerEvents(levelXPManager, this);
        this.getServer().getPluginManager().registerEvents(new EndermanBlockPickupListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoXpListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoFriendlyFireListener(), this);
        this.getServer().getPluginManager().registerEvents(new TimeListener(), this);
        this.getServer().getPluginManager().registerEvents(new SwiftSwordListener(), this);
        this.getServer().getPluginManager().registerEvents(new RingOfHealingSwapListener(), this);
        this.getServer().getPluginManager().registerEvents(new RingOfHealingListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EnderBagListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(scoreboardListener), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLevelChangeListener(scoreboardListener), this);
        this.getServer().getPluginManager().registerEvents(this.playerOnlineTimeTrackerListener, this);
        (new ScoreboardUpdater(scoreboardListener)).runTaskTimer(this, 0L, 9000L);
    }

    @Override public void onDisable() {
        CommandAPI.instance().unregister(glyphCommand);

        customItemManager.unloadRecipes();
    }
    public CustomHealthManager getHealthManager(){
        return healthManager;
    }
    public GlyphWidthManager glyphWidthManager() {
        return glyphWidthManager;
    }
}
