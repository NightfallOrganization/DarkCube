package eu.darkcube.system;

import static eu.darkcube.system.Reflection.*;

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
import eu.darkcube.system.loader.ReflectionClassLoader;

public abstract class Plugin extends JavaPlugin {

	private static HashMap<YamlConfiguration, File> fileFromConfig = new HashMap<>();
	private static HashMap<String, YamlConfiguration> configFromName = new HashMap<>();

//	private static final Class<?> CLASS_PLAYERCONNECTION = getVersionClass(MINECRAFT_PREFIX, "PlayerConnection");
	private static final Class<?> CLASS_CRAFTPLAYER = getVersionClass(CRAFTBUKKIT_PREFIX, "entity.CraftPlayer");
	private static final Class<?> CLASS_ENTITYPLAYER = getVersionClass(MINECRAFT_PREFIX, "EntityPlayer");
	private static final Method METHOD_GETHANDLE = getMethod(CLASS_CRAFTPLAYER, "getHandle");
	private static final Field FIELD_PLAYERCONNECTION = getField(CLASS_ENTITYPLAYER, "playerConnection");
	private static final Class<?> CLASS_PACKET = getVersionClass(MINECRAFT_PREFIX, "Packet");
	private static final Class<?> CLASS_PLAYERCONNECTION = getVersionClass(MINECRAFT_PREFIX, "PlayerConnection");
	private static final Method METHOD_SENDPACKET = getMethod(CLASS_PLAYERCONNECTION, "sendPacket", CLASS_PACKET);
	private static final Class<?> CLASS_PACKETPLAYOUTTITLE = getVersionClass(MINECRAFT_PREFIX, "PacketPlayOutTitle");
	private static final Constructor<?> CONSTRUCTOR_PACKETPLAYOUTTITLE = getConstructor(CLASS_PACKETPLAYOUTTITLE,
			int.class, int.class, int.class);
	private static final Class<?> CLASS_ICHATBASECOMPONENT = getVersionClass(MINECRAFT_PREFIX, "IChatBaseComponent");
	private static final Class<?> CLASS_PACKETPLAYOUTCHAT = getVersionClass(MINECRAFT_PREFIX, "PacketPlayOutChat");
	private static final Constructor<?> CONSTRUCTOR_PACKETPLAYOUTCHAT = getConstructor(CLASS_PACKETPLAYOUTCHAT,
			CLASS_ICHATBASECOMPONENT);

	public abstract String getCommandPrefix();

	private ReflectionClassLoader loader;

	public Plugin() {
		loader = new ReflectionClassLoader(this);
	}

	public static final Object playerConnection(Player p) {
		return getFieldValue(FIELD_PLAYERCONNECTION, getHandle(p));
	}

	public static final Object getHandle(Player p) {
		return invokeMethod(METHOD_GETHANDLE, p);
	}

	public static final void sendPacket(Object playerConnection, Object packet) {
		invokeMethod(METHOD_SENDPACKET, playerConnection, packet);
	}

	public static final void sendTitleTimes(Player p, int income, int stay, int outgoe) {
		sendPacket(playerConnection(p), newInstance(CONSTRUCTOR_PACKETPLAYOUTTITLE, income, stay, outgoe));
	}

	public Plugin saveDefaultConfig(String path) {
		path += ".yml";
		File f = new File(getDataFolder().getPath() + "/" + path);
		if (!f.exists()) {
			saveResource(path, true);
		}
		return this;
	}

	public Plugin createConfig(String name) {
		name += ".yml";
		File f = new File(getDataFolder().getPath() + "/" + name);
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		fileFromConfig.put(cfg, f);
		configFromName.put(name, cfg);

		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public void loadDependency(java.nio.file.Path file) {
		loader.loadJar(file);
	}

	public YamlConfiguration getConfig(String name) {
		name += ".yml";
		return configFromName.get(name);
	}

	public Plugin saveConfig(YamlConfiguration cfg) {
		try {
			cfg.save(fileFromConfig.get(cfg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public YamlConfiguration reloadConfig(String name) {
		name += ".yml";
		File f = new File(getDataFolder().getPath() + "/" + name);
		fileFromConfig.remove(configFromName.get(name));
		configFromName.remove(name);
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		configFromName.put(name, cfg);
		fileFromConfig.put(cfg, f);
		return cfg;
	}

	public Plugin reloadConfigs() {
		for (String name : configFromName.keySet()) {
			File f = new File(getDataFolder().getPath() + "/" + name);
			fileFromConfig.remove(configFromName.get(name));
			configFromName.remove(name);
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
			configFromName.put(name, cfg);
			fileFromConfig.put(cfg, f);
		}
		return this;
	}

	public final void sendConsole(String msg, String iprefix) {
		sendMessageBridge(("§7> " + getCommandPrefix() + ("§6 " + iprefix + " §7| ").replace("  ", " ") + msg)
				.replace("§r", "§r§7")
				.replace("§f", "§7"), Bukkit.getConsoleSender());
	}

	public final void sendConsole(String msg) {
		sendConsole(msg, "");
	}

	public final void sendMessage(String msg) {
		sendMessageBridgePrefix(msg);
	}

	public final void sendMessage(ChatEntry... entries) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendMessage(entries, p);
		}
		sendMessage(entries, Bukkit.getConsoleSender());
	}

	public final void sendMessage(ChatEntry[] entries, CommandSender sender) {
		if (sender instanceof Player) {
			ChatEntry[] insertion = new ChatEntry.Builder().text("§7» §8[§6" + getCommandPrefix() + "§8] §7┃ ").build();
			ChatEntry[] newEntries = new ChatEntry[entries.length + insertion.length];
			for (int i = 0; i < insertion.length; i++)
				newEntries[i] = insertion[i];

			for (int i = insertion.length; i < entries.length + insertion.length; i++) {
				newEntries[i] = entries[i - insertion.length];
			}

			sendMessageWithoutPrefix(newEntries, sender);
		} else {
			sendMessageWithoutPrefix(entries, sender);
		}
	}

	public final void sendMessageWithoutPrefix(ChatEntry[] entries) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendMessageWithoutPrefix(entries, p);
		}
		sendMessageWithoutPrefix(entries, Bukkit.getConsoleSender());
	}

	public final void sendMessageWithoutPrefix(ChatEntry[] entries, CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
//			playerConnection(p).sendPacket(new PacketPlayOutChat(ChatUtils.chat(entries)));
			sendPacket(playerConnection(p), newInstance(CONSTRUCTOR_PACKETPLAYOUTCHAT, ChatUtils.chat(entries)));
		} else {
			String totalmsg = "";
			for (int i = 0; i < entries.length; i++) {
				ChatEntry entry = entries[i];
				JsonElement element = entry.getJson().get("text");
				String msg = element.getAsString();
				msg = ("§7" + msg).replace("§r", "§r§7").replace("§f", "§7");
				totalmsg += msg;
			}
			totalmsg = translateAlternateColorCodesForCloudNet('§', totalmsg);
			sender.sendMessage(totalmsg);
		}
	}

	public final void sendMessageWithoutPrefix(String msg) {
		sendMessageBridge(msg);
	}

	public final void sendMessageWithoutPrefix(String msg, Collection<? extends CommandSender> receivers) {
		receivers.forEach(r -> sendMessageBridge(msg, r));
	}

	public final void sendConsoleWithoutPrefix(String msg) {
		sendMessageBridge(msg, Bukkit.getConsoleSender());
	}

	public final void sendMessageWithoutPrefix(String msg, CommandSender sender) {
		sender.sendMessage(msg);
	}

	public final void sendMessage(String msg, Collection<? extends CommandSender> receivers) {
		receivers.forEach(r -> sendMessage(msg, r));
	}

	public final void sendMessage(String msg, CommandSender sender) {
		sendMessageBridgePrefix(msg, sender);
	}

	private final void sendMessageBridgePrefix(String msg, CommandSender sender) {
		if (sender instanceof Player) {
			msg = "§7» §8[§6" + getCommandPrefix() + "§8] §7┃ " + msg;
		} else if (sender instanceof ConsoleCommandSender) {
			sendConsole(msg);
			return;
		}
		sendMessageBridge(msg, sender);
	}

	private final void sendMessageBridgePrefix(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendMessageBridgePrefix(msg, p);
		}
		sendMessageBridgePrefix(msg, Bukkit.getConsoleSender());
	}

	private final void sendMessageBridge(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendMessageBridge(msg, p);
		}
		sendMessageBridge(msg, Bukkit.getConsoleSender());
	}

	private final void sendMessageBridge(String msg, CommandSender sender) {
		sendMessageWithoutPrefix(msg, sender);
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
