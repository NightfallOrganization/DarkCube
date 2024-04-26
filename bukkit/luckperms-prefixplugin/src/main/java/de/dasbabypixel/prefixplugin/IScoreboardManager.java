/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package de.dasbabypixel.prefixplugin;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("SameParameterValue")
@Api
public abstract class IScoreboardManager implements Listener {

    protected static final LuckPerms api = LuckPermsProvider.get();
    protected static PrefixPluginBukkit main = PrefixPluginBukkit.instance();

    protected final Map<UUID, String> PREFIX_BY_UUID = new HashMap<>();
    protected final Map<UUID, String> SUFFIX_BY_UUID = new HashMap<>();
    protected final Map<UUID, Scoreboard> SCOREBOARD_BY_UUID = new HashMap<>();
    /**
     * @deprecated custom names are only shown in tab and kinda broken
     */
    @Deprecated
    @ScheduledForRemoval
    public Map<String, Object> names = null;

    protected String JOIN_MESSAGE = main.cfg.getString("joinmessage");
    protected String QUIT_MESSAGE = main.cfg.getString("quitmessage");
    protected String CHAT_FORMAT = main.cfg.getString("chatformat");
    protected boolean LP_PREFIX_ENABLED = main.cfg.getBoolean("luckpermsprefix");
    protected boolean LP_SUFFIX_ENABLED = main.cfg.getBoolean("luckpermssuffix");
    protected String CONSOLE_CHAR = main.cfg.getString("console-color-char");

    public IScoreboardManager() {
        EventBus bus = api.getEventBus();
        bus.subscribe(main, GroupDataRecalculateEvent.class, e -> new BukkitRunnable() {
            @Override
            public void run() {
                reload();
            }
        }.runTask(main));
        bus.subscribe(main, UserDataRecalculateEvent.class, event -> new BukkitRunnable() {
            @Override
            public void run() {
                reload(event.getUser().getUniqueId());
            }
        }.runTask(main));
    }

    @Api
    public boolean luckPermsPrefixEnabled() {
        return LP_PREFIX_ENABLED;
    }

    @Api
    public void luckPermsPrefixEnabled(boolean luckPermsPrefixEnabled) {
        LP_PREFIX_ENABLED = luckPermsPrefixEnabled;
    }

    @Api
    public boolean luckPermsSuffixEnabled() {
        return LP_SUFFIX_ENABLED;
    }

    @Api
    public void luckPermsSuffixEnabled(boolean luckPermsSuffixEnabled) {
        LP_SUFFIX_ENABLED = luckPermsSuffixEnabled;
    }

    @Api
    public void reload() {
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
            ReloadSinglePrefixEvent e = new ReloadSinglePrefixEvent(uuid, getPrefix(uuid), getSuffix(uuid));
            Bukkit.getPluginManager().callEvent(e);
            PREFIX_BY_UUID.put(uuid, e.newPrefix());
            SUFFIX_BY_UUID.put(uuid, e.newSuffix());
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

    @Api
    public void unload(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;
        SCOREBOARD_BY_UUID.remove(uuid);
        PREFIX_BY_UUID.remove(p.getUniqueId());
        SUFFIX_BY_UUID.remove(p.getUniqueId());

        Scoreboard sb = p.getScoreboard();
        for (Team team : sb.getTeams()) {
            team.unregister();
        }
        ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
        for (Scoreboard scoreboard : SCOREBOARD_BY_UUID.values()) {
            Team team = scoreboard.getTeam(tag.toString());
            if (team != null) team.unregister();
        }
        tag.unregister();
    }

    @Api
    public void load(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;
        Scoreboard sb = p.getScoreboard();
        if (sb == null || sb.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            sb = Bukkit.getScoreboardManager().getNewScoreboard();
            p.setScoreboard(sb);
        }
        loadPlayers(sb);
        for (Scoreboard scoreboard : SCOREBOARD_BY_UUID.values()) loadPlayer(scoreboard, p);
        SCOREBOARD_BY_UUID.put(uuid, sb);
        reload(uuid);
    }

    @Api
    public void reload(UUID uuid) {
        if (!SCOREBOARD_BY_UUID.containsKey(uuid)) return;
        reloadLuckpermsPrefix(uuid);
        reloadLuckpermsSuffix(uuid);
        String newPrefix = getPrefix(uuid);
        String newSuffix = getSuffix(uuid);
        ReloadSinglePrefixEvent e = new ReloadSinglePrefixEvent(uuid, newPrefix, newSuffix);
        Bukkit.getPluginManager().callEvent(e);
        setPrefix(uuid, e.newPrefix());
        setSuffix(uuid, e.newSuffix());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeDimension(PlayerChangedWorldEvent e) {
        reload(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        load(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinMessage(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        String msg = JOIN_MESSAGE;

        msg = replacePlaceHolders(p, msg);
        if (msg != null && !msg.isEmpty()) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.sendMessage(msg);
            }
            msg = colorSafe(msg, JOIN_MESSAGE, e.getPlayer());
            Bukkit.getConsoleSender().sendMessage(msg);
        }
        for (String key : names.keySet()) {
            if (key.equalsIgnoreCase(e.getPlayer().getName())) {
                e.getPlayer().setPlayerListName(names.get(key).toString());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        unload(e.getPlayer().getUniqueId());
        e.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
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
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        String msg = getChatMessage(e.getPlayer(), e.getMessage());
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(msg);
        }

        msg = colorSafe(e.getMessage(), CHAT_FORMAT, e.getPlayer());
        e.setFormat(msg);
        Bukkit.getConsoleSender().sendMessage(msg);
        e.setCancelled(true);
    }

    protected String colorSafe(String msg, String format, Player p) {
        format = replacePlaceHolders(p, format);
        format = format.replace(">>", "»");

        StringBuilder result = new StringBuilder();
        int length = msg.length();
        for (int index = length - 1; index > -1; index--) {
            char section = msg.charAt(index);
            String toAdd = String.valueOf(section);
            if (section == CONSOLE_CHAR.charAt(0) && index < length - 1) {
                char code = msg.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(code);
                if (color != null) {
                    toAdd += getLastColorsForCloudNet(msg.substring(0, index));
                }
            }
            result.insert(0, toAdd);
        }

        format = format.replace("%message%", result.toString());
        format = format.replace("%", "%%");
        format = translateAlternateColorCodesForCloudNet('§', format);
        result = new StringBuilder(format.replace('┃', '|'));
        return result.toString();
    }

    protected String getChatMessage(Player p, String message) {
        String msg = CHAT_FORMAT;
        msg = replacePlaceHolders(p, msg);
        msg = msg.replace(">>", "»");
        msg = msg.replace("%message%", message);
        msg = msg.replace("%", "%%");
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }

    protected String replacePlaceHolders(Player p, String msg) {
        if (msg == null) return null;
        String text = msg;
        UUID uuid = p.getUniqueId();
        String prefix = getPrefix(uuid);
        String suffix = getSuffix(uuid);
        text = text.replace("%player%", p.getName()).replace("%prefix%", prefix == null ? "" : prefix).replace("%suffix%", suffix == null ? "" : suffix);
        text = ChatColor.translateAlternateColorCodes('&', text);
        if (text.equalsIgnoreCase("null")) {
            text = null;
        }
        return text;
    }

    protected void loadPlayers(Scoreboard sb) {
        for (Player p : Bukkit.getOnlinePlayers()) loadPlayer(sb, p);
    }

    protected void loadPlayer(Scoreboard sb, Player p) {
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
        if (!sb.getTeam(teamname).hasEntry(p.getName())) {
            sb.getTeam(teamname).addEntry(p.getName());
        }
    }

    protected CachedDataManager getCachedData(User user) {
        return user.getCachedData();
    }

    protected User getUser(UUID uuid) {
        return api.getUserManager().getUser(uuid);
    }

    protected String getPrefix(UUID uuid) {
        return PREFIX_BY_UUID.getOrDefault(uuid, "");
    }

    protected String getSuffix(UUID uuid) {
        return SUFFIX_BY_UUID.getOrDefault(uuid, "");
    }

    protected void setPrefix(Team team, String prefix) {
        team.setPrefix(shortenPrefix(prefix));
    }

    protected void setSuffix(Team team, String prefix) {
        team.setSuffix(shortenSuffix(prefix));
    }

    protected void setPrefix(UUID uuid, String prefix) {
        PREFIX_BY_UUID.put(uuid, prefix);
        ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
        for (Scoreboard sb : SCOREBOARD_BY_UUID.values()) {
            setPrefix(sb.getTeam(tag.toString()), prefix);
        }
    }

    protected String shortenPrefix(String prefix) {
        return shorten(prefix, 16);
    }

    protected String shortenSuffix(String suffix) {
        return shorten(suffix, 16);
    }

    protected String shorten(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length);
        }
        return text;
    }

    protected void setSuffix(UUID uuid, String suffix) {
        SUFFIX_BY_UUID.put(uuid, suffix);
        ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
        for (Scoreboard sb : SCOREBOARD_BY_UUID.values()) {
            setSuffix(sb.getTeam(tag.toString()), suffix);
        }
    }

    protected void reloadLuckpermsPrefix(UUID uuid) {
        if (!luckPermsPrefixEnabled()) return;

        String prefix = getMetaData(uuid).getPrefix();
        if (prefix == null) prefix = "";
        PREFIX_BY_UUID.put(uuid, translateAlternateColorCodes('&', prefix));
    }

    protected void reloadLuckpermsSuffix(UUID uuid) {
        if (!luckPermsSuffixEnabled()) return;

        String suffix = getMetaData(uuid).getSuffix();
        if (suffix == null) suffix = "";
        SUFFIX_BY_UUID.put(uuid, translateAlternateColorCodes('&', suffix));
    }

    protected CachedMetaData getMetaData(UUID uuid) {
        return getMetaData(getUser(uuid));
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

    protected CachedMetaData getMetaData(User user) {
        return getCachedData(user).getMetaData();
    }

    protected String translateAlternateColorCodesForCloudNet(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = CONSOLE_CHAR.charAt(0);
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b).replace('┃', '|');
    }

    /**
     * Translates a string using an alternate color code character into a string that uses the internal ChatColor.COLOR_CODE color code character. The alternate color code character will only be replaced if it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
     *
     * @param altColorChar    The alternate color code character to replace. Ex: {@literal &}
     * @param textToTranslate Text containing the alternate color code character.
     *
     * @return Text containing the ChatColor.COLOR_CODE color code character.
     */
    protected String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = ChatColor.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    protected String getLastColorsForCloudNet(String input) {
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
