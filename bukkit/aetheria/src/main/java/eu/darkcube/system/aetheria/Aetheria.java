/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.aetheria.commands.*;
import eu.darkcube.system.aetheria.inventorys.UpgraderInventory;
import eu.darkcube.system.aetheria.items.CustomAxeManager;
import eu.darkcube.system.aetheria.items.CustomChestplateManager;
import eu.darkcube.system.aetheria.items.CustomPickaxeManager;
import eu.darkcube.system.aetheria.items.CustomSwordManager;
import eu.darkcube.system.aetheria.listener.*;
import eu.darkcube.system.aetheria.scheduler.RingOfHealingEffectApplier;
import eu.darkcube.system.aetheria.util.*;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.glyphwidthloader.GlyphWidthManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class Aetheria extends DarkCubePlugin {

    private static Aetheria instance;
    private final GlyphWidthManager glyphWidthManager = new GlyphWidthManager();
    private final GlyphCommand glyphCommand = new GlyphCommand(this);
    private final ResourcePackCommand resourcePackCommand = new ResourcePackCommand(this);
    private LevelXPManager levelXPManager;
    private NPCManagement npcManagement;
    private ActionBarUtil actionBarUtil;
    private CustomItemManager customItemManager;
    private DefenseManager defenseManager;
    private ResourcePackUtil resourcePackUtil;
    private NamespacedKey aroundDamageKey;
    private ScoreboardManager scoreboardManager;
    private CorManager corManager;
    private SchadensAnzeige schadensAnzeige;
    private CustomSwordManager customSwordManager;
    private CustomPickaxeManager customPickaxeManager;
    private CustomAxeManager customAxeManager;
    private CustomChestplateManager customChestplateManager;
    private DamageManager damageManager;
    private MonsterStatsManager monsterStatsManager;
    private CustomHealthManager healthManager;

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

    public DefenseManager getDefenseManager() {
        return this.defenseManager;
    }

    public NamespacedKey getAroundDamageKey() {
        return this.aroundDamageKey;
    }

    @Override public void onEnable() {
        resourcePackUtil = new ResourcePackUtil(this);
        try {
            glyphWidthManager.loadGlyphDataFromClassLoader(getClassLoader(), "glyph-widths.bin");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        SkillManager skillManager = new SkillManager(this);
        this.npcManagement = new NPCManagement(this);
        this.levelXPManager = new LevelXPManager(this, corManager);
        LevelXPManager levelXPManager = new LevelXPManager(this, corManager);
        healthManager = new CustomHealthManager(this);
        AttributeCommand attributeCommand = new AttributeCommand(this, healthManager);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");
        AroundDamageCommands aroundDamageCommands = new AroundDamageCommands(this);
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");
        this.defenseManager = new DefenseManager(this);
        corManager = new CorManager(this);
        scoreboardManager = new ScoreboardManager(this, levelXPManager, corManager);
        SkillsCommand skillsCommand = new SkillsCommand(skillManager);
        UpgraderInventory upgraderInventory = new UpgraderInventory();

        customSwordManager = new CustomSwordManager(this);
        customChestplateManager = new CustomChestplateManager(this);
        customPickaxeManager = new CustomPickaxeManager(this);
        customAxeManager = new CustomAxeManager(this);
        new TeleportManager(this);

        damageManager = new DamageManager(this);
        this.schadensAnzeige = new SchadensAnzeige();

        instance.getCommand("openinventory").setExecutor(new OpenInventoryCommand(upgraderInventory));
        instance.getCommand("skills").setExecutor(new SkillsCommand(skillManager));
        instance.getCommand("useskillslot").setExecutor(new UseSkillSlotCommand(skillManager));
        instance.getCommand("useskill").setExecutor(new UseSkillCommand(skillManager));
        instance.getCommand("myskills").setExecutor(new MySkillsCommand(skillManager));
        instance.getCommand("addskillslot").setExecutor(new AddSkillSlotCommand(skillManager));
        instance.getCommand("adddamage").setExecutor(new AddDamageCommand(this));
        instance.getCommand("mydamage").setExecutor(new MyDamageCommand(damageManager));
        instance.getCommand("setcor").setExecutor(new SetCorCommand(corManager));
        instance.getCommand("addcor").setExecutor(new AddCorCommand(corManager));
        instance.getCommand("mycor").setExecutor(new MyCorCommand(corManager));
        instance.getCommand("resetdurability").setExecutor(new ResetDurabilityCommand(customSwordManager, customAxeManager, customChestplateManager, customPickaxeManager));
        instance.getCommand("spawnupgrader").setExecutor(new SpawnUpgraderCommand(this));
        instance.getCommand("additemlevel").setExecutor(new AddItemLevelCommand(this));
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
        instance.getCommand("getitem").setExecutor(new GetItemCommand(this));
        instance.getCommand("killmobs").setExecutor(new KillMobsCommand());
        instance.getCommand("addxp").setExecutor(new AddXPCommand(this.levelXPManager));
        instance.getCommand("mylevel").setExecutor(new MyLevelCommand(this.levelXPManager));
        instance
                .getCommand("resetlevel")
                .setExecutor(new ResetLevelCommand(this.levelXPManager, healthManager, this.defenseManager, damageManager, this));
        instance.getCommand("myxp").setExecutor(new MyXPCommand(this.levelXPManager));
        instance.getCommand("myap").setExecutor(new MyAPCommand(this.levelXPManager));
        instance.getCommand("attribute").setExecutor(attributeCommand);
        instance.getCommand("addhealth").setExecutor(new AddHealthCommand(healthManager));
        instance.getCommand("myhealth").setExecutor(new MyHealthCommand(healthManager));
        instance.getCommand("addregeneration").setExecutor(new AddRegenerationCommand(healthManager));
        instance.getCommand("addarounddamage").setExecutor(aroundDamageCommands);
        instance.getCommand("myarounddamage").setExecutor(aroundDamageCommands);
        instance.getCommand("adddefense").setExecutor(new AddDefenseCommand(this));
        instance.getCommand("mydefense").setExecutor(new MyDefenseCommand(this.defenseManager));
        instance.getCommand("myregeneration").setExecutor(new MyRegenCommand(healthManager));

        CommandAPI.instance().register(glyphCommand);
        CommandAPI.instance().register(resourcePackCommand);

        this.customItemManager = new CustomItemManager(this);
        this.customItemManager.registerItems();

        FlyCommand flyCommand = new FlyCommand();// 98
        this.getCommand("fly").setExecutor(flyCommand);
        (new RingOfHealingEffectApplier(this)).runTaskTimer(this, 0L, 1L);
        monsterStatsManager = new MonsterStatsManager(this, healthManager);
        this.aroundDamageKey = new NamespacedKey(this, "AroundDamage");
        AroundDamageListener aroundDamageListenerWithKey = new AroundDamageListener(this, this.getAroundDamageKey());
        AroundDamageListener aroundDamageListener = new AroundDamageListener(this, this.getAroundDamageKey());
        new BagListener(this);

        actionBarUtil = new ActionBarUtil(this, healthManager, levelXPManager);

        this.getServer().getPluginManager().registerEvents(new CustomSwordManager(this), this);
        this.getServer().getPluginManager().registerEvents(new CustomPickaxeManager(this), this);
        this.getServer().getPluginManager().registerEvents(new CustomChestplateManager(this), this);
        this.getServer().getPluginManager().registerEvents(new CustomAxeManager(this), this);

        this.getServer().getPluginManager().registerEvents(upgraderInventory, this);
 //       this.getServer().getPluginManager().registerEvents(new TeleportManager(this), this);
        this.getCommand("myitemdurability").setExecutor(new MyItemDurabilityCommand(customPickaxeManager));
        this.getCommand("myitemlevel").setExecutor(new ItemLevelCommand(customSwordManager));
        this.getServer().getPluginManager().registerEvents(customChestplateManager, this);
        this.getServer().getPluginManager().registerEvents(skillsCommand, this);
        this.getServer().getPluginManager().registerEvents(new SkillClickListener(skillManager), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateScoreboardsForAllOnlinePlayers, 0L, 90000L);
        this.getServer().getPluginManager().registerEvents(damageManager, this);
        this.getServer().getPluginManager().registerEvents(schadensAnzeige, this);
        this.getServer().getPluginManager().registerEvents(flyCommand, this);
        this.getServer().getPluginManager().registerEvents(monsterStatsManager, this);
        this.getServer().getPluginManager().registerEvents(new CraftingTableListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityHealListener(), this);
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
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    @Override public void onDisable() {
        CommandAPI.instance().unregister(glyphCommand);
        CommandAPI.instance().unregister(resourcePackCommand);

        npcManagement.unload();
        customItemManager.unloadRecipes();
    }

    public void setScoreboardForPlayer(Player player) {
        scoreboardManager.createScoreboard(player);
    }

    public void updateScoreboardsForAllOnlinePlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            setScoreboardForPlayer(player);
        }
    }

    public MonsterStatsManager monsterStatsManager() {
        return monsterStatsManager;
    }

    public LevelXPManager levelXPManager() {
        return levelXPManager;
    }

    public CustomSwordManager customSwordManager() {
        return customSwordManager;
    }

    public CustomPickaxeManager customPickaxeManager() {
        return customPickaxeManager;
    }

    public CustomAxeManager customAxeManager() {
        return customAxeManager;
    }

    public CustomChestplateManager customChestplateManager() {
        return customChestplateManager;
    }

    public CorManager getCorManager() {
        return corManager;
    }

    public DamageManager getDamageManager() {
        return damageManager;
    }

    public NPCManagement npcManagement() {
        return npcManagement;
    }

    public ActionBarUtil actionBarUtil() {
        return actionBarUtil;
    }

    public SchadensAnzeige schadensAnzeige() {
        return schadensAnzeige;
    }

    public CustomHealthManager getHealthManager() {
        return healthManager;
    }

    public GlyphWidthManager glyphWidthManager() {
        return glyphWidthManager;
    }

}
