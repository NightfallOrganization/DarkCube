/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dasbabypixel.prefixplugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public abstract class IScoreboardManager implements Listener {

	private static final LuckPerms api = LuckPermsProvider.get();
	private static Main main = Main.getPlugin();

	private final Map<UUID, String> PREFIX_BY_UUID = new HashMap<>();
	private final Map<UUID, String> SUFFIX_BY_UUID = new HashMap<>();

	private final Map<UUID, Scoreboard> SCOREBOARD_BY_UUID = new HashMap<>();

	private String JOIN_MESSAGE = main.cfg.getString("joinmessage");
	private String QUIT_MESSAGE = main.cfg.getString("quitmessage");
	private String CHAT_FORMAT = main.cfg.getString("chatformat");

	private boolean LP_PREFIX_ENABLED = main.cfg.getBoolean("luckpermsprefix");
	private boolean LP_SUFFIX_ENABLED = main.cfg.getBoolean("luckpermssuffix");

	private String CONSOLE_CHAR = main.cfg.getString("console-color-char");

	public Map<String, Object> names = null;

	public void setLuckPermsPrefixEnabled(boolean luckPermsPrefixEnabled) {
		LP_PREFIX_ENABLED = luckPermsPrefixEnabled;
	}

	public void setLuckPermsSuffixEnabled(boolean luckPermsSuffixEnabled) {
		LP_SUFFIX_ENABLED = luckPermsSuffixEnabled;
	}

	public boolean isLuckPermsPrefixEnabled() {
		return LP_PREFIX_ENABLED;
	}

	public boolean isLuckPermsSuffixEnabled() {
		return LP_SUFFIX_ENABLED;
	}

	public IScoreboardManager() {
		EventBus bus = api.getEventBus();
		bus.subscribe(main, GroupDataRecalculateEvent.class, e -> {
			new BukkitRunnable() {
				@Override
				public void run() {
					reload();
				}
			}.runTask(main);
		});
		bus.subscribe(main, UserDataRecalculateEvent.class, event -> {
			User user = event.getUser();
			UUID uuid = user.getUniqueId();
			if (!SCOREBOARD_BY_UUID.containsKey(uuid)) {
				return;
			}
			String oldPrefix = getPrefix(uuid);
			String oldSuffix = getSuffix(uuid);
			reloadLuckpermsPrefix(uuid);
			reloadLuckpermsSuffix(uuid);
			String newPrefix = getPrefix(uuid);
			String newSuffix = getSuffix(uuid);
			if (!newPrefix.equals(oldPrefix) || !newSuffix.equals(oldSuffix)) {
				ReloadSinglePrefixEvent e = new ReloadSinglePrefixEvent(uuid, newPrefix, newSuffix);
				Bukkit.getPluginManager().callEvent(e);
				setPrefix(uuid, e.getNewPrefix());
				setSuffix(uuid, e.getNewSuffix());
			}
		});

	}

	public synchronized void reload() {
		ReloadPrefixPluginEvent event = new ReloadPrefixPluginEvent();
		Bukkit.getPluginManager().callEvent(event);

		PREFIX_BY_UUID.clear();
		SUFFIX_BY_UUID.clear();

		for (UUID uuid : SCOREBOARD_BY_UUID.keySet()) {
			for (Scoreboard sb : SCOREBOARD_BY_UUID.values()) {
				sb.getTeam(ScoreboardTag.getScoreboardTag(uuid).toString()).unregister();
			}
			ScoreboardTag.getScoreboardTag(uuid).unregister();
		}

		SCOREBOARD_BY_UUID.clear();

		main.reloadConfig();
		main.cfg = (YamlConfiguration) main.getConfig();
		ConfigurationSection sec = main.cfg.getConfigurationSection("names");
		if (sec != null) {
			names = sec.getValues(false);
		} else {
			names = new HashMap<>();
		}

		LP_PREFIX_ENABLED = main.cfg.getBoolean("luckpermsprefix");
		LP_SUFFIX_ENABLED = main.cfg.getBoolean("luckpermssuffix");

		JOIN_MESSAGE = main.cfg.getString("joinmessage", null);
		QUIT_MESSAGE = main.cfg.getString("quitmessage", null);

		CONSOLE_CHAR = main.cfg.getString("console-color-char");

		for (Player p : Bukkit.getOnlinePlayers()) {
			SCOREBOARD_BY_UUID.put(p.getUniqueId(), p.getScoreboard());
		}

		for (UUID uuid : SCOREBOARD_BY_UUID.keySet()) {
			reloadLuckpermsPrefix(uuid);
			reloadLuckpermsSuffix(uuid);
		}
		for (UUID uuid : SCOREBOARD_BY_UUID.keySet()) {
			Scoreboard sb = SCOREBOARD_BY_UUID.get(uuid);
			loadPlayers(sb);
		}
		for (UUID uuid : SCOREBOARD_BY_UUID.keySet()) {
			setPrefix(uuid, getPrefix(uuid));
			setSuffix(uuid, getSuffix(uuid));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChangeDimension(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String oldPrefix = getPrefix(uuid);
		String oldSuffix = getSuffix(uuid);
		reloadLuckpermsPrefix(uuid);
		reloadLuckpermsSuffix(uuid);
		String newPrefix = getPrefix(uuid);
		String newSuffix = getSuffix(uuid);
		if (!newPrefix.equals(oldPrefix)) {
			ReloadSinglePrefixEvent event = new ReloadSinglePrefixEvent(uuid, newPrefix, oldSuffix);
			Bukkit.getPluginManager().callEvent(event);
			setPrefix(uuid, event.getNewPrefix());
		}
		if (!newSuffix.equals(oldSuffix)) {
			ReloadSinglePrefixEvent event = new ReloadSinglePrefixEvent(uuid, newPrefix, newSuffix);
			Bukkit.getPluginManager().callEvent(event);
			setSuffix(uuid, event.getNewSuffix());
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		if (p.getScoreboard() == null || p.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}

		Scoreboard sb = p.getScoreboard();

		loadPlayers(sb);

		for (Scoreboard scoreboard : SCOREBOARD_BY_UUID.values()) {
			loadPlayer(scoreboard, p);
		}
		SCOREBOARD_BY_UUID.put(uuid, sb);

		e.setJoinMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinMessage(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String msg = JOIN_MESSAGE;

		String oldPrefix = getPrefix(uuid);
		String oldSuffix = getSuffix(uuid);
		reloadLuckpermsPrefix(uuid);
		reloadLuckpermsSuffix(uuid);
		String newPrefix = getPrefix(uuid);
		String newSuffix = getSuffix(uuid);
		if (!newPrefix.equals(oldPrefix) || !newSuffix.equals(oldSuffix)) {
			ReloadSinglePrefixEvent event = new ReloadSinglePrefixEvent(uuid, newPrefix, newSuffix);
			Bukkit.getPluginManager().callEvent(event);
			setPrefix(uuid, event.getNewPrefix());
			setSuffix(uuid, event.getNewSuffix());
		}
		msg = replacePlaceHolders(p, msg);
		if (msg != null) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(msg);
			}
			msg = colorSafe(msg, JOIN_MESSAGE, e.getPlayer());
			Bukkit.getConsoleSender().sendMessage(msg);
		}
		for (String key : names.keySet()) {
			if (key.equalsIgnoreCase(e.getPlayer().getName())) {
				e.getPlayer().setPlayerListName(names.get(key).toString());
				e.getPlayer().setCustomName(names.get(key).toString());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		SCOREBOARD_BY_UUID.remove(uuid);

		Scoreboard sb = p.getScoreboard();
		for (Team team : sb.getTeams()) {
			team.unregister();
		}
		ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
		for (Scoreboard scoreboard : SCOREBOARD_BY_UUID.values()) {
			scoreboard.getTeam(tag.toString()).unregister();
		}
		tag.unregister();
		e.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuitMessage(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String msg = QUIT_MESSAGE;
		msg = replacePlaceHolders(p, msg);
		if (msg != null) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(msg);
			}
			msg = colorSafe(msg, QUIT_MESSAGE, p);
			Bukkit.getConsoleSender().sendMessage(msg);
		}
		PREFIX_BY_UUID.remove(p.getUniqueId());
		SUFFIX_BY_UUID.remove(p.getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {

		if (!e.isCancelled()) {
			String msg = getChatMessage(e.getPlayer(), e.getMessage());
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(msg);
			}

			msg = colorSafe(e.getMessage(), CHAT_FORMAT, e.getPlayer());
			e.setFormat(msg);
			Bukkit.getConsoleSender().sendMessage(msg);
			e.setCancelled(true);
		}
	}

	String colorSafe(String msg, String format, Player p) {
		format = replacePlaceHolders(p, format);
		format = format.replace(">>", "»");
		String c = msg;

		String result = "";
		int length = c.length();
		for (int index = length - 1; index > -1; index--) {
			char section = c.charAt(index);
			String toAdd = String.valueOf(section);
			if (section == CONSOLE_CHAR.charAt(0) && index < length - 1) {
				char code = c.charAt(index + 1);
				ChatColor color = ChatColor.getByChar(code);
				if (color != null) {
					toAdd += getLastColorsForCloudNet(c.substring(0, index));
				}
			}
			result = toAdd + result;
		}

		format = format.replace("%message%", result);
		format = format.replace("%", "%%");
		format = translateAlternateColorCodesForCloudNet('§', format);
		result = format.replace('┃', '|');
		return result;
	}

	String getChatMessage(Player p, String message) {
		String msg = CHAT_FORMAT;
		msg = replacePlaceHolders(p, msg);
		msg = msg.replace(">>", "»");
		msg = msg.replace("%message%", message);
		msg = msg.replace("%", "%%");
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		return msg;
	}

	String replacePlaceHolders(Player p, String msg) {
		if (msg == null)
			return null;
		String text = msg;
		UUID uuid = p.getUniqueId();
		String prefix = getPrefix(uuid);
		String suffix = getSuffix(uuid);
		text = text.replace("%player%", p.getName())
				.replace("%prefix%", prefix == null ? "" : prefix)
				.replace("%suffix%", suffix == null ? "" : suffix);
		text = ChatColor.translateAlternateColorCodes('&', text);
		if (text.equalsIgnoreCase("null")) {
			text = null;
		}
		return text;
	}

	void loadPlayers(Scoreboard sb) {
		for (Player p : Bukkit.getOnlinePlayers())
			loadPlayer(sb, p);
	}

	void loadPlayer(Scoreboard sb, Player p) {
		UUID uuid = p.getUniqueId();
		String teamname = ScoreboardTag.getScoreboardTag(uuid).toString();
		if (sb.getTeam(teamname) == null) {
			sb.registerNewTeam(teamname);
			Team team = sb.getTeam(teamname);

			team.setCanSeeFriendlyInvisibles(false);

			String prefix = getPrefix(uuid);
			setPrefix(team, prefix);
			String suffix = getSuffix(uuid);
			setSuffix(team, suffix);
		}
		if (!sb.getTeam(teamname).hasEntry(p.getName()))
			sb.getTeam(teamname).addEntry(p.getName());
	}

	CachedDataManager getCachedData(Group group) {
		return group.getCachedData();
	}

	CachedDataManager getCachedData(UUID uuid) {
		return getCachedData(getUser(uuid));
	}

	CachedDataManager getCachedData(Player p) {
		return getCachedData(getUser(p));
	}

	CachedDataManager getCachedData(User user) {
		return user.getCachedData();
	}

	User getUser(Player p) {
		return getUser(p.getUniqueId());
	}

	User getUser(UUID uuid) {
		return api.getUserManager().getUser(uuid);
	}

	String getPrefix(UUID uuid) {
		return PREFIX_BY_UUID.getOrDefault(uuid, "");
	}

	String getSuffix(UUID uuid) {
		return SUFFIX_BY_UUID.getOrDefault(uuid, "");
	}

	void setPrefixAfter1_13(UUID uuid, String prefix) {
		char code = 'f';
		for (int i = 0; i < prefix.length(); i++) {
			if (prefix.charAt(i) == 167) {
				code = prefix.charAt(i + 1);
			}
		}
		try {
			ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
			for (Scoreboard sb : SCOREBOARD_BY_UUID.values()) {
				Team team = sb.getTeam(tag.toString());
				if (team != null) {
					Method method = Team.class.getMethod("setColor", ChatColor.class);
					method.invoke(team, ChatColor.getByChar(code));
				}
			}
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
	}

	private void setPrefix(Team team, String prefix) {
		if (team != null) {
			team.setPrefix(shorten(prefix, 16));
		}
	}

	private void setSuffix(Team team, String prefix) {
		if (team != null) {
			team.setSuffix(shorten(prefix, 16));
		}
	}

	void setPrefix(UUID uuid, String prefix) {
		PREFIX_BY_UUID.put(uuid, prefix);
		ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
		for (Scoreboard sb : SCOREBOARD_BY_UUID.values()) {
			setPrefix(sb.getTeam(tag.toString()), prefix);
		}
	}

	private String shorten(String text, int length) {
		if (text.length() > length) {
			text = text.substring(0, length);
		}
		return text;
	}

	void setSuffix(UUID uuid, String suffix) {
		SUFFIX_BY_UUID.put(uuid, suffix);
		ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
		for (Scoreboard sb : SCOREBOARD_BY_UUID.values()) {
			setSuffix(sb.getTeam(tag.toString()), suffix);
		}
	}

	/*
	 * void reloadLuckpermsPrefix(UUID uuid) { reloadLuckpermsPrefix(uuid,
	 * getCachedData(uuid)); }
	 */

	/*
	 * void reloadLuckpermsSuffix(UUID uuid) { reloadLuckpermsSuffix(uuid,
	 * getCachedData(uuid)); }
	 */

	void reloadLuckpermsPrefix(UUID uuid) {
		if (isLuckPermsPrefixEnabled()) {
			String prefix = getMetaData(uuid).getPrefix();
			if (prefix == null)
				prefix = "";
			PREFIX_BY_UUID.put(uuid, ChatColor.translateAlternateColorCodes('&', prefix));
//			setPrefix(uuid, ChatColor.translateAlternateColorCodes('&', prefix));
		}
	}

	void reloadLuckpermsSuffix(UUID uuid) {
		if (isLuckPermsSuffixEnabled()) {
			String suffix = getMetaData(uuid).getSuffix();
			if (suffix == null)
				suffix = "";
			SUFFIX_BY_UUID.put(uuid, ChatColor.translateAlternateColorCodes('&', suffix));
//			setSuffix(uuid, ChatColor.translateAlternateColorCodes('&', suffix));
		}
	}

	CachedMetaData getMetaData(UUID uuid) {
		return getMetaData(getUser(uuid));
	}

	CachedMetaData getMetaData(User user) {
		return getCachedData(user).getMetaData();
	}

	/*
	 * ContextSet getContext(Player p) { return getContext(p.getUniqueId()); }
	 * 
	 * ContextSet getContext(UUID uuid) { return getContext(getUser(uuid)); }
	 * 
	 * ContextSet getContext(User user) { ContextManager manager =
	 * api.getContextManager(); return
	 * manager.getContext(user).orElseGet(manager::getStaticContext); }
	 */

	String translateAlternateColorCodesForCloudNet(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = CONSOLE_CHAR.charAt(0);
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b).replace('┃', '|');
	}

	String getLastColorsForCloudNet(String input) {
		String result = CONSOLE_CHAR.charAt(0) + "7";
		int length = input.length();

		// Search backwards from the end as it is faster
		for (int index = length - 1; index > -1; index--) {
			char section = input.charAt(index);
			if (section == '§' && index < length - 1) {
				char c = input.charAt(index + 1);
				ChatColor color = ChatColor.getByChar(c);
				if (color != null) {
					result = Character.toString(CONSOLE_CHAR.charAt(0)) + color.getChar();
					return result;
				}
			}
		}
		return result;
	}
}
