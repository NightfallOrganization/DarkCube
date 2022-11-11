package eu.darkcube.system;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonElement;

import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.ReflectionUtils.PackageType;
import eu.darkcube.system.loader.ReflectionClassLoader;

public abstract class Plugin extends JavaPlugin {

	private static HashMap<YamlConfiguration, File> fileFromConfig = new HashMap<>();

	private static HashMap<String, YamlConfiguration> configFromName = new HashMap<>();

//	private static final Class<?> CLASS_PLAYERCONNECTION = getVersionClass(MINECRAFT_PREFIX, "PlayerConnection");
//	private static final Class<?> CLASS_CRAFTPLAYER = ReflectionUtils.get;

//	private static final Class<?> CLASS_ENTITYPLAYER = getVersionClass(MINECRAFT_PREFIX, "EntityPlayer");

	private static final Method METHOD_GETHANDLE = ReflectionUtils.getMethod("CraftPlayer",
			PackageType.CRAFTBUKKIT_ENTITY, "getHandle");

//	private static final Method METHOD_GETHANDLE = ReflectionUtils.getMethod(Plugin.CLASS_CRAFTPLAYER, "getHandle");

//	private static final Field FIELD_PLAYERCONNECTION = ReflectionUtils.getField(Plugin.CLASS_ENTITYPLAYER,
//			"playerConnection");

	private static final Field FIELD_PLAYERCONNECTION = ReflectionUtils.getField("EntityPlayer",
			PackageType.MINECRAFT_SERVER, false, "playerConnection");

//	private static final Class<?> CLASS_PACKET = getVersionClass(MINECRAFT_PREFIX, "Packet");

//	private static final Class<?> CLASS_PLAYERCONNECTION = getVersionClass(MINECRAFT_PREFIX, "PlayerConnection");

//	private static final Method METHOD_SENDPACKET = ReflectionUtils.getMethod(Plugin.CLASS_PLAYERCONNECTION,
//			"sendPacket", Plugin.CLASS_PACKET);

	private static final Class<?> CLASS_PACKET = ReflectionUtils.getClass("Packet", PackageType.MINECRAFT_SERVER);

	private static final Method METHOD_SENDPACKET = ReflectionUtils.getMethod("PlayerConnection",
			PackageType.MINECRAFT_SERVER, "sendPacket", Plugin.CLASS_PACKET);

	private static final Class<?> CLASS_PACKETPLAYOUTTITLE = ReflectionUtils.getClass("PacketPlayOutTitle",
			PackageType.MINECRAFT_SERVER);

	private static final Constructor<?> CONSTRUCTOR_PACKETPLAYOUTTITLE = ReflectionUtils
			.getConstructor(Plugin.CLASS_PACKETPLAYOUTTITLE, int.class, int.class, int.class);

	private static final Class<?> CLASS_ICHATBASECOMPONENT = ReflectionUtils.getClass("IChatBaseComponent",
			PackageType.MINECRAFT_SERVER);

	private static final Class<?> CLASS_PACKETPLAYOUTCHAT = ReflectionUtils.getClass("PacketPlayOutChat",
			PackageType.MINECRAFT_SERVER);

	private static final Constructor<?> CONSTRUCTOR_PACKETPLAYOUTCHAT = ReflectionUtils
			.getConstructor(Plugin.CLASS_PACKETPLAYOUTCHAT, Plugin.CLASS_ICHATBASECOMPONENT);

	public abstract String getCommandPrefix();

	private ReflectionClassLoader loader;

	public Plugin() {
		this.loader = new ReflectionClassLoader(this);
	}

	public static final Object playerConnection(Player p) {
		return ReflectionUtils.getValue(Plugin.getHandle(p), Plugin.FIELD_PLAYERCONNECTION);
	}

	public static final Object getHandle(Player p) {
		return ReflectionUtils.invokeMethod(p, Plugin.METHOD_GETHANDLE);
	}

	public static final void sendPacket(Object playerConnection, Object packet) {
		ReflectionUtils.invokeMethod(playerConnection, Plugin.METHOD_SENDPACKET, packet);
	}

	public static final void sendTitleTimes(Player p, int income, int stay, int outgoe) {
		Plugin.sendPacket(Plugin.playerConnection(p),
				ReflectionUtils.instantiateObject(Plugin.CONSTRUCTOR_PACKETPLAYOUTTITLE, income, stay, outgoe));
	}

	public Plugin saveDefaultConfig(String path) {
		path += ".yml";
		File f = new File(this.getDataFolder().getPath() + "/" + path);
		if (!f.exists()) {
			this.saveResource(path, true);
		}
		return this;
	}

	public Plugin createConfig(String name) {
		name += ".yml";
		File f = new File(this.getDataFolder().getPath() + "/" + name);
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		Plugin.fileFromConfig.put(cfg, f);
		Plugin.configFromName.put(name, cfg);

		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public void loadDependency(java.nio.file.Path file) {
		this.loader.loadJar(file);
	}

	public YamlConfiguration getConfig(String name) {
		name += ".yml";
		return Plugin.configFromName.get(name);
	}

	public Plugin saveConfig(YamlConfiguration cfg) {
		try {
			cfg.save(Plugin.fileFromConfig.get(cfg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public YamlConfiguration reloadConfig(String name) {
		name += ".yml";
		File f = new File(this.getDataFolder().getPath() + "/" + name);
		Plugin.fileFromConfig.remove(Plugin.configFromName.get(name));
		Plugin.configFromName.remove(name);
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		Plugin.configFromName.put(name, cfg);
		Plugin.fileFromConfig.put(cfg, f);
		return cfg;
	}

	public Plugin reloadConfigs() {
		for (String name : Plugin.configFromName.keySet()) {
			File f = new File(this.getDataFolder().getPath() + "/" + name);
			Plugin.fileFromConfig.remove(Plugin.configFromName.get(name));
			Plugin.configFromName.remove(name);
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
			Plugin.configFromName.put(name, cfg);
			Plugin.fileFromConfig.put(cfg, f);
		}
		return this;
	}

	public final void sendConsole(String msg, String iprefix) {
		this.sendMessageBridge(("§7> " + this.getCommandPrefix() + ("§6 " + iprefix + " §7| ").replace("  ", " ") + msg)
				.replace("§r", "§r§7")
				.replace("§f", "§7"), Bukkit.getConsoleSender());
	}

	public final void sendConsole(String msg) {
		this.sendConsole(msg, "");
	}

	public final void sendMessage(String msg) {
		this.sendMessageBridgePrefix(msg);
	}

	public final void sendMessage(ChatEntry... entries) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.sendMessage(entries, p);
		}
		this.sendMessage(entries, Bukkit.getConsoleSender());
	}

	public final void sendMessage(ChatEntry[] entries, CommandSender sender) {
		if (sender instanceof Player) {
			ChatEntry[] insertion = new ChatEntry.Builder().text("§7» §8[§6" + this.getCommandPrefix() + "§8] §7┃ ")
					.build();
			ChatEntry[] newEntries = new ChatEntry[entries.length + insertion.length];
			for (int i = 0; i < insertion.length; i++)
				newEntries[i] = insertion[i];

			for (int i = insertion.length; i < entries.length + insertion.length; i++) {
				newEntries[i] = entries[i - insertion.length];
			}

			this.sendMessageWithoutPrefix(newEntries, sender);
		} else {
			this.sendMessageWithoutPrefix(entries, sender);
		}
	}

	public final void sendMessageWithoutPrefix(ChatEntry[] entries) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.sendMessageWithoutPrefix(entries, p);
		}
		this.sendMessageWithoutPrefix(entries, Bukkit.getConsoleSender());
	}

	public final void sendMessageWithoutPrefix(ChatEntry[] entries, CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
//			playerConnection(p).sendPacket(new PacketPlayOutChat(ChatUtils.chat(entries)));
			Plugin.sendPacket(Plugin.playerConnection(p),
					ReflectionUtils.instantiateObject(Plugin.CONSTRUCTOR_PACKETPLAYOUTCHAT, ChatUtils.chat(entries)));
		} else {
			String totalmsg = "";
			for (int i = 0; i < entries.length; i++) {
				ChatEntry entry = entries[i];
				JsonElement element = entry.getJson().get("text");
				String msg = element.getAsString();
				msg = ("§7" + msg).replace("§r", "§r§7").replace("§f", "§7");
				totalmsg += msg;
			}
			totalmsg = Plugin.translateAlternateColorCodesForCloudNet('§', totalmsg);
			sender.sendMessage(totalmsg);
		}
	}

	public final void sendMessageWithoutPrefix(String msg) {
		this.sendMessageBridge(msg);
	}

	public final void sendMessageWithoutPrefix(String msg, Collection<? extends CommandSender> receivers) {
		receivers.forEach(r -> this.sendMessageBridge(msg, r));
	}

	public final void sendConsoleWithoutPrefix(String msg) {
		this.sendMessageBridge(msg, Bukkit.getConsoleSender());
	}

	public final void sendMessageWithoutPrefix(String msg, CommandSender sender) {
		sender.sendMessage(msg);
	}

	public final void sendMessage(String msg, Collection<? extends CommandSender> receivers) {
		receivers.forEach(r -> this.sendMessage(msg, r));
	}

	public final void sendMessage(String msg, CommandSender sender) {
		this.sendMessageBridgePrefix(msg, sender);
	}

	private final void sendMessageBridgePrefix(String msg, CommandSender sender) {
		if (sender instanceof Player) {
			msg = "§7» §8[§6" + this.getCommandPrefix() + "§8] §7┃ " + msg;
		} else if (sender instanceof ConsoleCommandSender) {
			this.sendConsole(msg);
			return;
		}
		this.sendMessageBridge(msg, sender);
	}

	private final void sendMessageBridgePrefix(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.sendMessageBridgePrefix(msg, p);
		}
		this.sendMessageBridgePrefix(msg, Bukkit.getConsoleSender());
	}

	private final void sendMessageBridge(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.sendMessageBridge(msg, p);
		}
		this.sendMessageBridge(msg, Bukkit.getConsoleSender());
	}

	private final void sendMessageBridge(String msg, CommandSender sender) {
		this.sendMessageWithoutPrefix(msg, sender);
	}

	public static String translateAlternateColorCodesForCloudNet(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = '§';
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

}
