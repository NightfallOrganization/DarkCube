/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ListenerScoreboard extends BaseListener {

    private static final String TEAM_RANK_TEXT = "§1§1§d";
    private static final String TEAM_RANK = "§1§2§d";
    private static final String TEAM_ACCOUNT_TEXT = "§2§1§d";
    private static final String TEAM_ACCOUNT = "§2§2§d";
    private static final String TEAM_COINS_TEXT = "§3§1§d";
    private static final String TEAM_COINS = "§3§2§d";
    private static final String TEAM_SOCIALS_TEXT = "§4§1§d";
    private static final String TEAM_SOCIALS = "§4§2§d";
    private static final String PREFIX = "§8» §d";
    private static final String[] ANIMATION = new String[]{"§8« §5Dark§dCube§8.§5eu §8»    ", "§8« §5Dark§dCube§8.§5eu §8»    ", "§8« §5Dark§dCube§8.§5eu §8»    ", "§8« §5Dark§dCube§8.§5eu §8»    ", "§8« §5Dark§dCube§8.§5eu §8»    ", "§8« §5Dark§dCube§8.§5eu §8»    ", "§8« §5Dark§dCube§8.§5eu §8»    ", "§8 « §5Dark§dCube§8.§5eu §8»   ", "§8  « §5Dark§dCube§8.§5eu §8»  ", "§8   « §5Dark§dCube§8.§5eu §8» ", "§8    « §5Dark§dCube§8.§5eu §8»", "§8    « §5Dark§dCube§8.§5eu §8»", "§8    « §5Dark§dCube§8.§5eu §8»", "§8    « §5Dark§dCube§8.§5eu §8»", "§8    « §5Dark§dCube§8.§5eu §8»", "§8   « §5D ark§dCube§8.§5eu §8»", "§8   « §5Da rk§dCube§8.§5eu §8»", "§8   « §5Dar k§dCube§8.§5eu §8»", "§8   « §5Dark §dCube§8.§5eu §8»", "§8   « §5Dark§dC ube§8.§5eu §8»", "§8   « §5Dark§dCu be§8.§5eu §8»", "§8   « §5Dark§dCub e§8.§5eu §8»", "§8   « §5Dark§dCube §8.§5eu §8»", "§8   « §5Dark§dCube§8. §5eu §8»", "§8   « §5Dark§dCube§8.§5e u §8»", "§8   « §5Dark§dCube§8.§5eu  §8»", "§8  « §5Dark§dCube§8.§5eu §8»  ", "§8  « §5D ark§dCube§8.§5eu §8» ", "§8  « §5Da rk§dCube§8.§5eu §8» ", "§8  « §5Dar k§dCube§8.§5eu §8» ", "§8  « §5Dark §dCube§8.§5eu §8» ", "§8  « §5Dark§dC ube§8.§5eu §8» ", "§8  « §5Dark§dCu be§8.§5eu §8» ", "§8  « §5Dark§dCub e§8.§5eu §8» ", "§8  « §5Dark§dCube §8.§5eu §8» ", "§8  « §5Dark§dCube§8. §5eu §8» ", "§8  « §5Dark§dCube§8.§5e u §8» ", "§8  « §5Dark§dCube§8.§5eu  §8» ", "§8  « §5Dark§dCube§8.§5eu §8»  ", "§8 « §5Dark§dCube§8.§5eu §8»   ", "§8 « §5D ark§dCube§8.§5eu §8»  ", "§8 « §5Da rk§dCube§8.§5eu §8»  ", "§8 « §5Dar k§dCube§8.§5eu §8»  ", "§8 « §5Dark §dCube§8.§5eu §8»  ", "§8 « §5Dark§dC ube§8.§5eu §8»  ", "§8 « §5Dark§dCu be§8.§5eu §8»  ", "§8 « §5Dark§dCub e§8.§5eu §8»  ", "§8 « §5Dark§dCube §8.§5eu §8»  ", "§8 « §5Dark§dCube§8. §5eu §8»  ", "§8 « §5Dark§dCube§8.§5e u §8»  ", "§8 « §5Dark§dCube§8.§5eu  §8»  ", "§8 « §5Dark§dCube§8.§5eu §8»   ", "§8« §5Dark§dCube§8.§5eu §8»    ", "§8« §5D ark§dCube§8.§5eu §8»   ", "§8« §5Da rk§dCube§8.§5eu §8»   ", "§8« §5Dar k§dCube§8.§5eu §8»   ", "§8« §5Dark §dCube§8.§5eu §8»   ", "§8« §5Dark§dC ube§8.§5eu §8»   ", "§8« §5Dark§dCu be§8.§5eu §8»   ", "§8« §5Dark§dCub e§8.§5eu §8»   ", "§8« §5Dark§dCube §8.§5eu §8»   ", "§8« §5Dark§dCube§8. §5eu §8»   ", "§8« §5Dark§dCube§8.§5e u §8»   ", "§8« §5Dark§dCube§8.§5eu  §8»   ", "§8« §5Dark§dCube§8.§5eu §8»    ",};
    public static ListenerScoreboard instance;

    public ListenerScoreboard() {
        instance = this;
    }

    @EventHandler public void handle(PlayerJoinEvent e) {
        String ssl = "«";
        String ssr = "»";
        String arr = "➥";

        Player p = e.getPlayer();
        Scoreboard sb = p.getScoreboard();
        if (sb == Bukkit.getScoreboardManager().getMainScoreboard()) {
            sb = Bukkit.getScoreboardManager().getNewScoreboard();
            p.setScoreboard(sb);
        }

        Objective obj = sb.getObjective("side");
        if (obj == null) {
            obj = sb.registerNewObjective("side", "dummy");
        }
        obj.setDisplayName("§8" + ssl + " §5Dark§dCube§8.§5eu §8" + ssr);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team rankText = getTeam(sb, TEAM_RANK_TEXT);
        rankText.setPrefix("§8" + arr + " §5Rang§8:");
        rankText.setSuffix("");

        Team rank = getTeam(sb, TEAM_RANK);
        rank.setPrefix(PREFIX);
        rank.setSuffix("");

        Team accountText = getTeam(sb, TEAM_ACCOUNT_TEXT);
        accountText.setPrefix("§8" + arr + " §5Profil§8:");
        accountText.setSuffix("");
        Team account = getTeam(sb, TEAM_ACCOUNT);
        account.setPrefix(PREFIX);
        account.setSuffix("");

        Team coinsText = getTeam(sb, TEAM_COINS_TEXT);
        coinsText.setPrefix("§8" + arr + " §5Cubes§8:");
        coinsText.setSuffix("");
        Team coins = getTeam(sb, TEAM_COINS);
        coins.setPrefix(PREFIX);
        coins.setSuffix("");

        Team socialsText = getTeam(sb, TEAM_SOCIALS_TEXT);
        socialsText.setPrefix("§8" + arr + " §5Website§8:");
        socialsText.setSuffix("");
        Team socials = getTeam(sb, TEAM_SOCIALS);
        socials.setPrefix(PREFIX + "DarkCube§8");
        socials.setSuffix(".§deu");

        Team space = sb.getTeam("                ");
        if (space == null) space = sb.registerNewTeam("                ");
        space.setPrefix("§1            §2");
        space.setSuffix("");
        if (!space.hasEntry("                ")) space.addEntry("                ");
        obj.getScore("                ").setScore(12);
        obj.getScore(TEAM_RANK_TEXT).setScore(11);
        obj.getScore(TEAM_RANK).setScore(10);
        obj.getScore("§1§d").setScore(9);
        obj.getScore(TEAM_ACCOUNT_TEXT).setScore(8);
        obj.getScore(TEAM_ACCOUNT).setScore(7);
        obj.getScore("§2§d").setScore(6);
        obj.getScore(TEAM_COINS_TEXT).setScore(5);
        obj.getScore(TEAM_COINS).setScore(4);
        obj.getScore("§3§d").setScore(3);
        obj.getScore(TEAM_SOCIALS_TEXT).setScore(2);
        obj.getScore(TEAM_SOCIALS).setScore(1);
        obj.getScore("§4§d").setScore(0);

        eu.darkcube.system.userapi.User u = UserAPI.instance().user(p.getUniqueId());
        eu.darkcube.system.lobbysystem.user.LobbyUser user = UserWrapper.fromUser(u);
        new BukkitRunnable() {

            @Override public void run() {
                if (!p.isOnline()) {
                    cancel();
                    return;
                }
                setSuffix(coins, u.cubes().toString());
                setSuffix(account, p.getName());

                LuckPerms lp = LuckPermsProvider.get();

                User user = lp.getUserManager().getUser(p.getUniqueId());
                if (user != null) {
                    String prefix = user.getCachedData().getMetaData().getPrefix();
                    if (prefix == null) prefix = "";
                    String suffix = prefix.split(" ")[0];

                    setSuffix(rank, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', suffix)));
                }
            }
        }.runTaskTimer(Lobby.getInstance(), 1, 1);
        final Objective obj2 = obj;

        new BukkitRunnable() {
            int count = 0;

            @Override public void run() {
                if (!p.isOnline()) {
                    cancel();
                    return;
                }
                if (!user.isAnimations()) {
                    obj2.setDisplayName("§8" + ssl + " §5Dark§dCube§8.§5eu §8" + ssr);
                    return;
                }
                obj2.setDisplayName(ANIMATION[count++]);
                count %= ANIMATION.length;
            }
        }.runTaskTimer(Lobby.getInstance(), 2, 2);
    }

    private void setSuffix(Team team, String suffix) {
        if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
        }
        team.setSuffix(suffix);
    }

    private Team getTeam(Scoreboard sb, String name) {
        Team t = sb.getTeam(name);
        if (t == null) {
            t = sb.registerNewTeam(name);
        }
        if (!t.hasEntry(name)) {
            t.addEntry(name);
        }
        return t;
    }
}
