package building.oneblock;

import building.oneblock.commands.*;
import building.oneblock.commands.CorCommand;
import building.oneblock.gui.MenuEditGUI;
import building.oneblock.gui.MenuGUI;
import building.oneblock.items.CrookItem;
import building.oneblock.items.RodOfTheSkyItem;
import building.oneblock.listener.CraftingTableListener;
import building.oneblock.listener.MenuGUIListener;
import building.oneblock.manager.*;
import building.oneblock.manager.player.*;
import building.oneblock.npc.NPCCreator;
import building.oneblock.ruler.MainRuler;
import building.oneblock.ruler.OneBlockWorldRuler;
import building.oneblock.ruler.SpawnRuler;
import building.oneblock.util.CorTabCompleter;
import building.oneblock.util.Message;
import building.oneblock.util.StartWorld;
import building.oneblock.util.ability.ItemCollector;
import building.oneblock.util.ability.SneakGrow;
import building.oneblock.util.ability.WoodBlockBreaker;
import eu.darkcube.system.glyphwidthloader.GlyphWidthManager;
import eu.darkcube.system.util.Language;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;

public final class OneBlock extends JavaPlugin {
    private static OneBlock instance;
    private final GlyphWidthManager glyphWidthManager = new GlyphWidthManager();

    public OneBlock() {
        instance = this;
    }

    public static OneBlock getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        WorldManager.loadWorlds();

        try {
            glyphWidthManager.loadGlyphDataFromClassLoader(getClassLoader(), "glyph-widths.bin");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        var itemManager = new ItemManager(this);
        itemManager.createAllRecipes();

        try {
            Language.GERMAN.registerLookup(this.getClassLoader(), "messages_de.properties", s -> Message.KEY_PREFIX + s);
            Language.ENGLISH.registerLookup(this.getClassLoader(), "messages_en.properties", s -> Message.KEY_PREFIX + s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        var spawnManager = new SpawnManager();
        var dataStorageManager = new DataStorageManager();
        var oneBlockManager = new OneBlockManager(dataStorageManager);
        var joinQuitManager = new JoinQuitManager(spawnManager);
        var itemCollector = new ItemCollector();
        var spawnRuler = new SpawnRuler();
        var mainRuler = new MainRuler();
        var oneBlockWorldRuler = new OneBlockWorldRuler();
        var sneakGrow = new SneakGrow();
        var crookItem = new CrookItem();
        var rodOfTheSkyitem = new RodOfTheSkyItem();
        var woodBlockBreaker = new WoodBlockBreaker();
        var respawnManager = new RespawnManager();
        var npcManager = new NPCManager(this);
        var npcCreator = new NPCCreator(npcManager);
        var flyCommand = new FlyCommand(this);
        var coreManager = new CoreManager(this);
        var levelManager = new LevelManager(this);
        var scoreboardManager = new ScoreboardManager();
        var playerManager = new PlayerManager(this, coreManager, levelManager, scoreboardManager);
        var menuEditGUI = new MenuEditGUI();
        var worldSlotManager = new WorldSlotManager();
        var menuGUI = new MenuGUI(playerManager, worldSlotManager);
        var startWorld = new StartWorld(worldSlotManager);
        var menuGUIListener = new MenuGUIListener(menuGUI, menuEditGUI, worldSlotManager);
        var craftingTableListener = new CraftingTableListener();

        npcCreator.createNPC();

        instance.getServer().getPluginManager().registerEvents(menuGUIListener, this);
        instance.getServer().getPluginManager().registerEvents(playerManager, this);
        instance.getServer().getPluginManager().registerEvents(oneBlockManager, this);
        instance.getServer().getPluginManager().registerEvents(joinQuitManager, this);
        instance.getServer().getPluginManager().registerEvents(itemCollector, this);
        instance.getServer().getPluginManager().registerEvents(spawnRuler, this);
        instance.getServer().getPluginManager().registerEvents(mainRuler, this);
        instance.getServer().getPluginManager().registerEvents(oneBlockWorldRuler, this);
        instance.getServer().getPluginManager().registerEvents(sneakGrow, this);
        instance.getServer().getPluginManager().registerEvents(crookItem, this);
        instance.getServer().getPluginManager().registerEvents(woodBlockBreaker, this);
        instance.getServer().getPluginManager().registerEvents(respawnManager, this);
        instance.getServer().getPluginManager().registerEvents(rodOfTheSkyitem, this);
        instance.getServer().getPluginManager().registerEvents(flyCommand, this);
        instance.getServer().getPluginManager().registerEvents(craftingTableListener, this);

        instance.getCommand("menu").setExecutor(new MenuCommand(menuGUI));
        instance.getCommand("killmobs").setExecutor(new KillMobsCommand());
        instance.getCommand("gm").setExecutor(new GMCommand());
        instance.getCommand("tpworld").setExecutor(new TPWorldCommand());
        instance.getCommand("god").setExecutor(new GodCommand());
        instance.getCommand("fly").setExecutor(new FlyCommand(this));
        instance.getCommand("startworld").setExecutor(new StartWorldCommand());
        instance.getCommand("loadworld").setExecutor(new LoadWorldCommand());
        instance.getCommand("deleteworld").setExecutor(new DeleteWorldCommand(worldSlotManager));
        instance.getCommand("heal").setExecutor(new HealCommand());
        instance.getCommand("feed").setExecutor(new FeedCommand());
        instance.getCommand("max").setExecutor(new MaxCommand());
        instance.getCommand("day").setExecutor(new DayCommand());
        instance.getCommand("night").setExecutor(new NightCommand());
        instance.getCommand("spawn").setExecutor(new SpawnCommand(spawnManager));
        instance.getCommand("cor").setExecutor(new CorCommand(coreManager));
        instance.getCommand("myslots").setExecutor(new MySlotsCommand(worldSlotManager));
        instance.getCommand("setblocksmined").setExecutor(new SetBlocksMinedCommand(dataStorageManager));

        instance.getCommand("cor").setTabCompleter(new CorTabCompleter());
    }

    @Override
    public void onDisable() {
//        var itemManager = new ItemManager(this);
//        itemManager.removeAllRecipes();
    }

    public GlyphWidthManager glyphWidthManager() {
        return glyphWidthManager;
    }

}
