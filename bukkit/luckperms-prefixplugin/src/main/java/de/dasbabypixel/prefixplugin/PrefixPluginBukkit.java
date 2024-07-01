/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dasbabypixel.prefixplugin;

import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PrefixPluginBukkit extends JavaPlugin {

    protected static final String version;
    protected static Stream<String> scoreboardStream;
    private static PrefixPluginBukkit instance;
    public YamlConfiguration cfg;
    private CommandHandler commandHandler;
    private IScoreboardManager scoreboardManager;
    private String commandPrefix = getConfig().getString("commandprefix");

    public PrefixPluginBukkit() {
        instance = this;
    }

    public static PrefixPluginBukkit instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        commandHandler = new CommandHandler();
        saveDefaultConfig();
        cfg = (YamlConfiguration) getConfig();
        cfg.options().copyDefaults(true);

        if (commandPrefix == null) commandPrefix = "";
        commandPrefix = ChatColor.translateAlternateColorCodes('&', commandPrefix);

        var simpleSBManager = new ScoreboardManagerLoader();
        this.scoreboardManager = simpleSBManager.scoreboardManager;
        if (this.scoreboardManager != null) {
            Bukkit.getPluginManager().registerEvents(this.scoreboardManager, this);
            this.scoreboardManager.reload();
        }
        var failed = simpleSBManager.failed;
        var cause = simpleSBManager.cause;
        if (failed) {
            switch (cause) {
                case CLASS_NOT_FOUND_EXCEPTION:
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] Need Class for Version '" + version + "'. Please contact Plugin Developer");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] Plugin could not find Server version. Disabling (402)");
                    Bukkit.getPluginManager().disablePlugin(this);
                    break;
                case MANAGER_COULD_NOT_LOAD:
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] Plugin could not register Manager. Disabling (401)");
                    Bukkit.getPluginManager().disablePlugin(this);
                    break;
            }
        }
        try {
            var yml = this.getDescription();
            for (var cmd : yml.getCommands().keySet()) {
                getCommand(cmd).setTabCompleter(commandHandler);
                getCommand(cmd).setExecutor(commandHandler);
                getLogger().log(Level.INFO, "Found command \"" + cmd + "\" and enabled it");
            }
        } catch (Exception ignored) {
        }
    }

    public IScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    static {
        var versions = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        version = versions.length == 3 ? Bukkit.getMinecraftVersion().replace(".", "_") : versions[3];
    }
    //	public void sendMessage(String msg, CommandSender sender) {
    //		if (sender instanceof Player) {
    //			msg = "§7» §8[§6" + commandPrefix + "§8] §7┃ " + msg;
    //		}
    //		if (!(sender instanceof Player)) {
    //			msg = ("§7" + msg).replace("§r", "§r§7").replace("§f", "§7");
    //		}
    //
    //		msg = msg + "§7";
    //		if (sender instanceof ConsoleCommandSender) {
    //			msg = translateAlternateColorCodesForCloudNet('§', msg);
    //		}
    //
    //		sender.sendMessage(msg);
    //	}

    //	private static String translateAlternateColorCodesForCloudNet(char altColorChar, String
    //	textToTranslate) {
    //		char[] b = textToTranslate.toCharArray();
    //
    //		for (int i = 0; i < b.length - 1; ++i) {
    //			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1])
    //			> -1) {
    //				b[i] = getPlugin().getConfig().getString("console-color-char").charAt(0);
    //				b[i + 1] = Character.toLowerCase(b[i + 1]);
    //			}
    //		}
    //		return new String(b);
    //	}
}
