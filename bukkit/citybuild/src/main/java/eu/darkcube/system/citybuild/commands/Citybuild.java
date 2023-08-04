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
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Citybuild extends JavaPlugin {

    private static Citybuild instance;
    private final GlyphWidthManager glyphWidthManager = new GlyphWidthManager();
    private final GlyphCommand glyphCommand = new GlyphCommand(this);
    private LevelXPManager levelXPManager;
    private PlayerOnlineTimeTracker playerOnlineTimeTracker;
    private CustomItemManager customItemManager;

    public Citybuild() {
        instance = this;
    }

    public static Citybuild getInstance() {
        return instance;
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
        levelXPManager = new LevelXPManager(this);
        AttributeCommand attributeCommand = new AttributeCommand(this);
        CustomHealthManager healthManager = new CustomHealthManager(this);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");

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
        instance.getCommand("addxp").setExecutor(new AddXPCommand(levelXPManager));
        instance.getCommand("mylevel").setExecutor(new MyLevelCommand(levelXPManager));
        instance.getCommand("resetlevel").setExecutor(new ResetLevelCommand(levelXPManager, healthManager));
        instance.getCommand("myxp").setExecutor(new MyXPCommand(levelXPManager));
        instance.getCommand("myap").setExecutor(new MyAPCommand(levelXPManager));
        instance.getCommand("attribute").setExecutor(attributeCommand);
        instance.getCommand("addhealth").setExecutor(new AddHealthCommand(healthManager));
        instance.getCommand("myhealth").setExecutor(new MyHealthCommand(healthManager));
        instance.getCommand("addregeneration").setExecutor(new RegenerationCommand(healthManager));

        CommandAPI.instance().register(glyphCommand);

        new ActionBarTask("Ⲓ", "Ⲕ").runTaskTimer(this, 0L, 3L);

        customItemManager = new CustomItemManager(this);
        customItemManager.registerItems();

        // Erstelle den BackpackListener und lade die gespeicherten Inventare
        BackpackListener backpackListener = new BackpackListener(customItemManager);
        backpackListener.loadInventories();

        // Registriere den BackpackListener
        getServer().getPluginManager().registerEvents(backpackListener, this);

        ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
        playerOnlineTimeTracker = new PlayerOnlineTimeTracker(scoreboardHandler);
        for (Player player : getServer().getOnlinePlayers()) {
            scoreboardHandler.showPlayerLevelScoreboard(player);
        }

        Fly flyCommand = new Fly();
        getServer().getPluginManager().registerEvents(flyCommand, this);
        getCommand("fly").setExecutor(flyCommand);

        new RingOfHealingEffectApplier(this).runTaskTimer(this, 0L, 1L);

        LevelXPManager levelXPManager = new LevelXPManager(this);
        MonsterLevelHandler monsterLevelHandler = new MonsterLevelHandler(levelXPManager);
        getServer().getPluginManager().registerEvents(monsterLevelHandler, this);
        getServer().getPluginManager().registerEvents(new ConstantHunger(), this);
        getServer().getPluginManager().registerEvents(new CustomDeathMessage(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(healthManager), this);
        getServer().getPluginManager().registerEvents(new DamageListener(healthManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinHealthSetupListener(healthManager), this);
        getServer().getPluginManager().registerEvents(attributeCommand, this);
        getServer().getPluginManager().registerEvents(new CustomMonsterSpawn(), this);
        getServer().getPluginManager().registerEvents(new NoMonsterSpawn(), this);
        getServer().getPluginManager().registerEvents(new NoLeafDecayListener(), this);
        getServer().getPluginManager().registerEvents(new NoMobDropsListener(), this);
        getServer().getPluginManager().registerEvents(levelXPManager, this);
        getServer().getPluginManager().registerEvents(new EndermanBlockPickupListener(), this);
        getServer().getPluginManager().registerEvents(new NoXpListener(), this);
        getServer().getPluginManager().registerEvents(new NoFriendlyFireHandler(), this);
        getServer().getPluginManager().registerEvents(new TimeHandler(), this);
        getServer().getPluginManager().registerEvents(new SwiftSwordListener(), this);
        getServer().getPluginManager().registerEvents(new RingOfHealingSwapHandler(), this);
        getServer().getPluginManager().registerEvents(new RingOfHealingListener(this), this);
        getServer().getPluginManager().registerEvents(new EnderBagListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(scoreboardHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerLevelChangeListener(scoreboardHandler), this);
        getServer().getPluginManager().registerEvents(playerOnlineTimeTracker, this);

        // Hinzufügen der ScoreboardUpdater Task
        new ScoreboardUpdater(scoreboardHandler).runTaskTimer(this, 0L, 9000L); // Aktualisiert alle 20 Ticks, d.h. etwa alle Sekunden

    }

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
