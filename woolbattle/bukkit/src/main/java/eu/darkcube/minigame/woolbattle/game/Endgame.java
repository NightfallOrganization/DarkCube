/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerBlockBreak;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerEntityDamage;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerPlayerJoin;
import eu.darkcube.minigame.woolbattle.listener.endgame.ListenerPlayerQuit;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class Endgame extends GamePhase {

    public final ListenerPlayerJoin listenerPlayerJoin;
    public final ListenerPlayerQuit listenerPlayerQuit;
    public final ListenerBlockBreak listenerBlockBreak;
    public final ListenerEntityDamage listenerEntityDamage;

    public Endgame() {
        this.listenerPlayerJoin = new ListenerPlayerJoin();
        this.listenerPlayerQuit = new ListenerPlayerQuit();
        this.listenerBlockBreak = new ListenerBlockBreak();
        this.listenerEntityDamage = new ListenerEntityDamage();
    }

    @Override
    public void onEnable() {
        CloudNetLink.update();
        final WoolBattleBukkit main = WoolBattleBukkit.instance();
        //		main.getSchedulers().clear();

        WoolBattleBukkit.registerListeners(this.listenerPlayerJoin);
        WoolBattleBukkit.registerListeners(this.listenerPlayerQuit);
        WoolBattleBukkit.registerListeners(this.listenerBlockBreak);
        WoolBattleBukkit.registerListeners(this.listenerEntityDamage);

        Team winner = main.ingame().winner;
        WBUser.onlineUsers().forEach(u -> {
            u.getBukkitEntity().teleport(main.lobby().getSpawn());
            u.getBukkitEntity().setAllowFlight(false);
            this.setPlayerItems(u);
        });
        main.sendMessage(Message.TEAM_HAS_WON, user -> new Object[]{winner.getName(user)});
        WBUser mostKills = this.getPlayerWithMostKills();
        main.sendMessage(Message.PLAYER_HAS_MOST_KILLS,
                user -> new Object[]{mostKills.getTeamPlayerName(),
                        Integer.toString(mostKills.getKills())});
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Player t : Bukkit.getOnlinePlayers()) {
                if (!p.canSee(t))
                    p.showPlayer(t);
            }
        }

        List<WBUser> users = WBUser.onlineUsers().stream().filter(u -> u.getKills() != 0)
                .sorted((o1, o2) -> Double.compare(o2.getKD(), o1.getKD()))
                .collect(Collectors.toList());

        int players = users.size();
        if (players >= 1) {
            main.sendMessage(Message.STATS_PLACE_1, "1", this.val(users.get(0).getKD()),
                    users.get(0).getTeamPlayerName());
            if (players >= 2) {
                main.sendMessage(Message.STATS_PLACE_2, "2", this.val(users.get(1).getKD()),
                        users.get(1).getTeamPlayerName());
                if (players >= 3) {
                    main.sendMessage(Message.STATS_PLACE_3, "3", this.val(users.get(2).getKD()),
                            users.get(2).getTeamPlayerName());
                    if (players >= 4) {
                        for (int i = 3; i < 4; i++) {
                            main.sendMessage(Message.STATS_PLACE_TH, Integer.toString(i + 1),
                                    this.val(users.get(i).getKD()),
                                    users.get(i).getTeamPlayerName());
                        }
                    }
                }
            }
        }

        new Scheduler() {
            private int countdownTicks = 400;

            @Override
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (this.countdownTicks % 20 == 0)
                        all.setLevel(this.countdownTicks / 20);
                    all.setExp(this.countdownTicks / 400F);
                }
                this.countdownTicks--;
                if (this.countdownTicks <= 0) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        all.kickPlayer(Bukkit.getShutdownMessage());
                    }
                    Endgame.this.disable();
                    this.cancel();
                    Bukkit.shutdown();
                    //					Main.getInstance().getLobby().enable();
                }
            }
        }.runTaskTimer(1);
    }

    @Override
    public void onDisable() {
        WoolBattleBukkit.unregisterListeners(this.listenerPlayerJoin);
        WoolBattleBukkit.unregisterListeners(this.listenerPlayerQuit);
        WoolBattleBukkit.unregisterListeners(this.listenerBlockBreak);
        WoolBattleBukkit.unregisterListeners(this.listenerEntityDamage);
    }

    private String val(double d) {
        return String.format("%1.2f", d);
    }

    public WBUser getPlayerWithMostKills() {
        WBUser result = null;
        int kills = -1;
        for (WBUser player : WBUser.onlineUsers()) {
            if (player.getKills() > kills) {
                kills = player.getKills();
                result = player;
            }
        }
        return result;
    }

    public WBUser getPlayerWithBestKD() {
        WBUser result = null;
        double kd = -1;
        for (WBUser player : WBUser.onlineUsers()) {
            if (player.getKD() > kd) {
                kd = player.getKD();
                result = player;
            }
        }
        return result;
    }

    public void setPlayerItems(WBUser user) {
        user.getBukkitEntity().getInventory().clear();
        user.getBukkitEntity().getInventory().setArmorContents(new ItemStack[4]);
    }

}
