package eu.darkcube.system.darkessentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.darkessentials.command.*;
import eu.darkcube.system.darkessentials.util.EssentialCollections;
import eu.darkcube.system.darkessentials.util.EssentialCollections.SortingRule;
import eu.darkcube.system.darkessentials.util.NumbzUtils;
import eu.darkcube.system.darkessentials.util.WarpPoint;

public class Main extends Plugin {

	public static FileConfiguration config;
	public static String colorFail, colorConfirm, colorValue;

	@Override
	public String getCommandPrefix() {
		return "§5Dark§dCube";
	}

	@Override
	public void onDisable() {
		saveConfig();
//		try {
//			config.save("config.yml");
//			System.out.println("Config saved.");
//		} catch (IOException e) {
//			System.out.println("Config not saved!");
//		}
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();

		colorConfirm = ChatColor.valueOf(config.getString("main.colors.confirmColor")).toString();
		colorFail = ChatColor.valueOf(config.getString("main.colors.failColor")).toString();
		colorValue = ChatColor.valueOf(config.getString("main.colors.valueColor")).toString();

//		System.out.println(config.getBoolean("command.burn.enabled"));

		EssentialCollections.addSortingRule(new SortingRule<World>() {
			@Override
			public String toString(World t) {
				return t.getName();
			}

			@Override
			public Class<World> getRuleClass() {
				return World.class;
			}

			@Override
			public boolean instanceOf(Object obj) {
				return obj instanceof World;
			}
		});
		EssentialCollections.addSortingRule(new SortingRule<Player>() {
			@Override
			public String toString(Player t) {
				return t.getName();
			}

			@Override
			public Class<Player> getRuleClass() {
				return Player.class;
			}

			@Override
			public boolean instanceOf(Object obj) {
				return obj instanceof World;
			}
		});
		EssentialCollections.addSortingRule(new SortingRule<EntityType>() {

			@Override
			public boolean instanceOf(Object obj) {
				return obj instanceof EntityType;
			}

			@SuppressWarnings("deprecation")
			@Override
			public String toString(EntityType t) {
				return t.getName();
			}

			@Override
			public Class<EntityType> getRuleClass() {
				return EntityType.class;
			}
		});
		EssentialCollections.addSortingRule(new SortingRule<WarpPoint>() {

			@Override
			public boolean instanceOf(Object obj) {
				return obj instanceof WarpPoint;
			}

			@Override
			public String toString(WarpPoint t) {
				return t.getName();
			}

			@Override
			public Class<WarpPoint> getRuleClass() {
				return WarpPoint.class;
			}
		});
		EssentialCollections.addSortingRule(new SortingRule<Material>() {

			@Override
			public boolean instanceOf(Object obj) {
				return obj instanceof Material;
			}

			@Override
			public String toString(Material t) {
				return t.toString();
			}

			@Override
			public Class<Material> getRuleClass() {
				return Material.class;
			}
		});
		if (config.getBoolean("command.burn.enabled"))
			CommandAPI.enable(this, new CommandBurn());
		if (config.getBoolean("command.day.enabled"))
			CommandAPI.enable(this, new CommandDay());
		CommandAPI.enable(this, new CommandNight());
		CommandAPI.enable(this, new CommandStop());
		CommandAPI.enable(this, new CommandEnderChest());
		CommandAPI.enable(this, new CommandHeal());
		CommandAPI.enable(this, new CommandFeed());
		CommandAPI.enable(this, new CommandTrash());
		CommandAPI.enable(this, new CommandPTime());
		CommandAPI.enable(this, new CommandPWeather());
		CommandAPI.enable(this, new CommandSpeed());
		CommandAPI.enable(this, new CommandXp());
		CommandAPI.enable(this, new CommandSpawner());
		CommandAPI.enable(this, new CommandFly());
		CommandAPI.enable(this, new CommandPing());
		CommandAPI.enable(this, new CommandRepair());
		CommandAPI.enable(this, new CommandGM());
		CommandAPI.enable(this, new CommandEnchant());
		CommandAPI.enable(this, new CommandPos());
		CommandAPI.enable(this, new CommandInvsee());
		CommandAPI.enable(this, new CommandHat());
		CommandAPI.enable(this, new CommandNear());
		CommandAPI.enable(this, new CommandSkull());
		CommandAPI.enable(this, new CommandOnlineplayers());
		CommandAPI.enable(this, new CommandItemclear());
		CommandAPI.enable(this, new CommandCraft());
		CommandAPI.enable(this, new CommandGms());
		CommandAPI.enable(this, new CommandGmc());
		CommandAPI.enable(this, new CommandGma());
		CommandAPI.enable(this, new CommandGmsp());
		CommandAPI.enable(this, new CommandTimefreeze());
		CommandAPI.enable(this, new CommandTeleportto());
		CommandAPI.enable(this, new CommandGamemodeAll());
		CommandAPI.enable(this, new CommandTeleportWorld());
		if (config.getBoolean("command.warp.enabled")) {
			CommandAPI.enable(this, new CommandWarp());
			CommandAPI.enable(this, new CommandWarpEdit());
		}

	}

	@Override
	public void onLoad() {
	}

	public static Main getInstance() {
		return Main.getPlugin(Main.class);
	}

	public static void sendTitle(String title, String subtitle, int come,
					int stay, int go) {
		Bukkit.getOnlinePlayers().forEach(p -> sendTitle(p, title, subtitle, come, stay, go));
	}

	@SuppressWarnings("deprecation")
	public static void sendTitle(Player p, String title, String subtitle,
					int come, int stay, int go) {
		Plugin.sendTitleTimes(p, come, stay, go);
		p.sendTitle(title, subtitle);
//		PacketPlayOutTitle packetTimes = new PacketPlayOutTitle(come, stay, go);
//		PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
//				ChatUtils.chat(subtitle == null ? "" : subtitle));
//		PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE,
//				ChatUtils.chat(title == null ? "" : title));
//		PlayerConnection con = ((CraftPlayer) p).getHandle().playerConnection;
//		con.sendPacket(packetTimes);
//		con.sendPacket(packetSubtitle);
//		con.sendPacket(packetTitle);
	}

	public static void sendMessagePlayernameRequired(CommandSender sender) {
		Main.getPlugin(Main.class).sendMessage(Main.cFail()
						+ "Du bist kein Spieler, deshalb musst du einen Spielernamen angeben!", sender);
	}

	public static void sendMessagePlayerNotFound(Set<String> unresolvedNames,
					CommandSender sender) {
		if (unresolvedNames.size() != 0) {
			StringBuilder sb = new StringBuilder();
			if (unresolvedNames.size() > 1) {
				sb.append(Main.cFail() + "Die Spieler " + ChatColor.GRAY
								+ "\"");
			} else {
				sb.append(Main.cFail() + "Der Spieler " + ChatColor.GRAY
								+ "\"");
			}
			for (String name : unresolvedNames) {
				sb.append(Main.cValue() + name + ChatColor.GRAY + "\", \"");
			}
			if (unresolvedNames.size() > 1) {
				Main.getPlugin(Main.class).sendMessage(sb.toString().substring(0, sb.toString().length()
								- 3) + Main.cFail()
								+ " konnten nicht gefunden werden!", sender);
			} else {
				Main.getPlugin(Main.class).sendMessage(sb.toString().substring(0, sb.toString().length()
								- 3) + Main.cFail()
								+ " konnte nicht gefunden werden!", sender);
			}
		}
	}

	public static boolean sendMessageAndReturnTrue(String message,
					CommandSender... senders) {
		Main.getPlugin(Main.class).sendMessage(message, Arrays.asList(senders));
		return true;
	}

	public static List<String> getPlayersStartWith(String[] args) {
		List<String> list = new ArrayList<>();
		for (Player current : Bukkit.getOnlinePlayers()) {
			String playerName = current.getName().toLowerCase(Locale.ENGLISH);
			if (playerName.startsWith(args[args.length
							- 1].toLowerCase(Locale.ENGLISH))
							&& !NumbzUtils.containsStringIgnoreCase(playerName, args)) {
				list.add(current.getName());
			}
		}
		return list;
	}

	public static Set<WarpPoint> updateWarps() {
		try {
			Set<WarpPoint> warps = new Gson().fromJson(Main.config.getString("warps"), new TypeToken<HashSet<WarpPoint>>() {
			}.getType());
			warps.forEach(w -> w.isValid());
			return warps;
		} catch (Exception e) {
			return new HashSet<>();
		}
	}

	public static String cConfirm() {
		return colorConfirm;
	}

	public static String cFail() {
		return colorFail;
	}

	public static String cValue() {
		return colorValue;
	}

}
