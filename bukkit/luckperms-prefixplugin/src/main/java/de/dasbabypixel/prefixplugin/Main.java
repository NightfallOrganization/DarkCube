package de.dasbabypixel.prefixplugin;

import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main main;
	protected static String version;
	protected static Stream<String> scoreboardStream;

	public YamlConfiguration cfg;
	private CommandHandler commandHandler;
	private IScoreboardManager scoreboardManager;
	private String commandPrefix = getConfig().getString("commandprefix");

	@Override
	public void onEnable() {
		main = this;
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		commandHandler = new CommandHandler();
		saveDefaultConfig();
		cfg = (YamlConfiguration) getConfig();
		cfg.options().copyDefaults(true);

		if (commandPrefix == null)
			commandPrefix = "";
		commandPrefix = ChatColor.translateAlternateColorCodes('&', commandPrefix);

		/*getCommand("addname").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command var2, String var3, String[] args) {
				if (args.length >= 2) {
					String playername = args[0];
					String name = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
					scoreboardManager.names.put(playername, ChatColor.translateAlternateColorCodes('&', name));
					sender.sendMessage("New name for " + playername + ": " + name);
					cfg.set("names", scoreboardManager.names);
					return true;
				}
				return false;
			}
		});
		getCommand("removename").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command var2, String var3, String[] args) {
				if (args.length == 1) {
					String playername = args[0];
					if (!scoreboardManager.names.containsKey(playername)) {
						sender.sendMessage("Playername does not exist in names");
						return true;
					}
					scoreboardManager.names.remove(playername);
					sender.sendMessage("Playername removed!");
					cfg.set("names", scoreboardManager.names);
					return true;
				}
				return false;
			}
		});
		getCommand("listnames").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command var2, String var3, String[] args) {
				for (String playername : scoreboardManager.names.keySet()) {
					sender.sendMessage(playername + ": " + scoreboardManager.names.get(playername));
				}
				return true;
			}
		});*/

		ScoreboardManager simpleSBManager = new ScoreboardManager(this);
		boolean failed = simpleSBManager.failed;
		FailureCause cause = simpleSBManager.cause;
		if (failed) {
			switch (cause) {
			case CLASS_NOT_FOUND_EXCEPTION:
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.RED + "[" + this.getName() + "] Need Class for Version '" + version
								+ "'. Please contact Plugin Developer");
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.RED + "[" + this.getName()
								+ "] Plugin could not find Server version. Disabling (402)");
				Bukkit.getPluginManager().disablePlugin(this);
				break;
			case MANAGER_COULD_NOT_LOAD:
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.RED + "[" + this.getName()
								+ "] Plugin could not register Manager. Disabling (401)");
				Bukkit.getPluginManager().disablePlugin(this);
				break;
			}
		}
		try {
			PluginDescriptionFile yml = this.getDescription();
			for (String cmd : yml.getCommands().keySet()) {
				getCommand(cmd).setExecutor(commandHandler);
				getLogger().log(Level.INFO, "Found command \"" + cmd + "\" and enabled it");
			}
		} catch (Exception ex) {
		}
	}

	@Override
	public void onDisable() {
	}

	public IScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public void setScoreboardManager(IScoreboardManager scoreboardManager) {
		this.scoreboardManager = scoreboardManager;
		this.scoreboardManager.reload();
	}

	public static Main getPlugin() {
		return main;
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

//	private static String translateAlternateColorCodesForCloudNet(char altColorChar, String textToTranslate) {
//		char[] b = textToTranslate.toCharArray();
//
//		for (int i = 0; i < b.length - 1; ++i) {
//			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
//				b[i] = getPlugin().getConfig().getString("console-color-char").charAt(0);
//				b[i + 1] = Character.toLowerCase(b[i + 1]);
//			}
//		}
//		return new String(b);
//	}
}
