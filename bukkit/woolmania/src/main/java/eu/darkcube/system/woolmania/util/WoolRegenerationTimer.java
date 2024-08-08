/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;

import static eu.darkcube.system.woolmania.enums.Names.SYSTEM;

import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.area.RegenerateWoolAreas;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WoolRegenerationTimer extends BukkitRunnable {
    private static boolean isRunning = false;

    @Override
    public void run() {
        new LastMinuteWarningTask().runTaskTimer(WoolMania.getInstance(), 20L, 20L);
        isRunning = false;
        startTimerIfNotRunning();
    }

    private static void startTimerIfNotRunning() {
        if (!isRunning) {
            isRunning = true;
            new WoolRegenerationTimer().runTaskLater(WoolMania.getInstance(), 30 * 60 * 20L); // 30 Minuten in Ticks
        }
    }

    public static void startFirstTimer() {
        new WoolRegenerationTimer().runTaskLater(WoolMania.getInstance(), 29 * 60 * 20L); // 29 Minuten in Ticks
    }

    private void playSoundToAllPlayers(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, (float) 1.0, (float) 2.0);
    }

    private class LastMinuteWarningTask extends BukkitRunnable {
        private int timer = 60;
        @Override
        public void run() {

            if (timer == 60) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    User user = UserAPI.instance().user(onlinePlayer.getUniqueId());
                    playSoundToAllPlayers(onlinePlayer);
                    user.sendMessage(Message.ONE_MINUTE_LEFT);
                }
            }

            if (timer <= 3) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    User user = UserAPI.instance().user(onlinePlayer.getUniqueId());
                    playSoundToAllPlayers(onlinePlayer);
                    user.sendMessage(Message.SECOUND_LEFT, timer);
                }
            }

            timer--;

            if (timer <= 0) {
                cancel();
                isRunning = false;
                timer = 60;
                RegenerateWoolAreas.regenerateWoolAreas();

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    User user = UserAPI.instance().user(onlinePlayer.getUniqueId());
                    onlinePlayer.sendMessage(" ");
                    onlinePlayer.sendMessage("§7--------- " + SYSTEM.getName() + " ---------");
                    onlinePlayer.sendMessage(" ");
                    user.sendMessage(Message.TIMER_IS_OVER);
                    user.sendMessage(Message.TIMER_IS_OVER_SECOUND);
                    onlinePlayer.sendMessage(" ");
                    onlinePlayer.sendMessage("§7----------------------------");
                }
            }
        }
    }
}
