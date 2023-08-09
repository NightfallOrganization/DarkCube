/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.Plugin;
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
        return this.defenseManager;
    }

    public NamespacedKey getAroundDamageKey() {
        return this.aroundDamageKey;
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
        this.levelXPManager = new LevelXPManager(this);
        CustomHealthManager healthManager = new CustomHealthManager(this);
        AttributeCommand attributeCommand = new AttributeCommand(this, healthManager);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");
        AroundDamageCommands aroundDamageCommands = new AroundDamageCommands(this);
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");
        this.defenseManager = new DefenseManager(this);
        instance.getCommand("gm").setExecutor(new GM());
        instance.getCommand("heal").setExecutor(new Heal(healthManager));
        instance.getCommand("day").setExecutor(new Day());
        instance.getCommand("night").setExecutor(new Night());
        instance.getCommand("god").setExecutor(new God());
        instance.getCommand("fly").setExecutor(new Fly());
        instance.getCommand("feed").setExecutor(new Feed());
        instance.getCommand("max").setExecutor(new Max());
        instance.getCommand("trash").setExecutor(new Trash());
        instance.getCommand("warp").setExecutor(new Warp());
        instance.getCommand("spawn").setExecutor(new SpawnCommand());
        instance.getCommand("getitem").setExecutor(new GetItem());
        instance.getCommand("killmobs").setExecutor(new KillMobs());
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

        (new ActionBarTask("Ⲓ", "Ⲕ")).runTaskTimer(this, 0L, 3L);
        this.customItemManager = new CustomItemManager(this);
        this.customItemManager.registerItems();
        ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
        this.playerOnlineTimeTracker = new PlayerOnlineTimeTracker(scoreboardHandler);
        Iterator var6 = this.getServer().getOnlinePlayers().iterator();

        while(var6.hasNext()) {
            Player player = (Player)var6.next();
            scoreboardHandler.showPlayerLevelScoreboard(player);
        }

        Fly flyCommand = new Fly();// 98
        this.getServer().getPluginManager().registerEvents(flyCommand, this);
        this.getCommand("fly").setExecutor(flyCommand);
        (new RingOfHealingEffectApplier(this)).runTaskTimer(this, 0L, 1L);
        LevelXPManager levelXPManager = new LevelXPManager(this);
        MonsterLevelHandler monsterLevelHandler = new MonsterLevelHandler(levelXPManager, healthManager);
        this.getServer().getPluginManager().registerEvents(monsterLevelHandler, this);
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");
        AroundDamageHandler aroundDamageHandlerWithKey = new AroundDamageHandler(this, this.getAroundDamageKey());
        AroundDamageHandler aroundDamageHandler = new AroundDamageHandler(this, this.getAroundDamageKey());
        new BagListener(this);

        this.getServer().getPluginManager().registerEvents(new EntityHealListener(), this);
  //      this.getServer().getPluginManager().registerEvents(new SchadensAnzeige(), this);
     //   this.getServer().getPluginManager().registerEvents(new KnockbackListener(), this);
        this.getServer().getPluginManager().registerEvents(new SoundListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinHealthSetupListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(aroundDamageHandler, this);
        this.getServer().getPluginManager().registerEvents(aroundDamageHandlerWithKey, this);
        this.getServer().getPluginManager().registerEvents(new DefenseListener(this.defenseManager), this);
        this.getServer().getPluginManager().registerEvents(new DefenseListener(this.defenseManager), this);
        this.getServer().getPluginManager().registerEvents(new AroundDamageHandler(this, this.getAroundDamageKey()), this);
        this.getServer().getPluginManager().registerEvents(new NoEffectPlugin(), this);
        this.getServer().getPluginManager().registerEvents(new ConstantHunger(), this);
        this.getServer().getPluginManager().registerEvents(new CustomDeathMessage(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(new DamageListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinHealthSetupListener(healthManager), this);
        this.getServer().getPluginManager().registerEvents(attributeCommand, this);
        this.getServer().getPluginManager().registerEvents(new CustomMonsterSpawn(), this);
        this.getServer().getPluginManager().registerEvents(new NoMonsterSpawn(), this);
        this.getServer().getPluginManager().registerEvents(new NoLeafDecayListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoMobDropsListener(), this);
        this.getServer().getPluginManager().registerEvents(levelXPManager, this);
        this.getServer().getPluginManager().registerEvents(new EndermanBlockPickupListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoXpListener(), this);
        this.getServer().getPluginManager().registerEvents(new NoFriendlyFireHandler(), this);
        this.getServer().getPluginManager().registerEvents(new TimeHandler(), this);
        this.getServer().getPluginManager().registerEvents(new SwiftSwordListener(), this);
        this.getServer().getPluginManager().registerEvents(new RingOfHealingSwapHandler(), this);
        this.getServer().getPluginManager().registerEvents(new RingOfHealingListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EnderBagListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(scoreboardHandler), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLevelChangeListener(scoreboardHandler), this);
        this.getServer().getPluginManager().registerEvents(this.playerOnlineTimeTracker, this);
        (new ScoreboardUpdater(scoreboardHandler)).runTaskTimer(this, 0L, 9000L);
    }

    @Override public void onDisable() {
        CommandAPI.instance().unregister(glyphCommand);

        customItemManager.unloadRecipes();
    }

    public GlyphWidthManager glyphWidthManager() {
        return glyphWidthManager;
    }
}
