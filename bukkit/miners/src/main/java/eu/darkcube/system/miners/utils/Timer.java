/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.utils;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.utils.message.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable {
    public static int time = 30;
    public static boolean isTimerRunning = false;

    @Override
    public void run() {
        time--;
        isTimerRunning = true;

        if (time <= 3 && time != 0) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                User user = UserAPI.instance().user(onlinePlayer.getUniqueId());
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, (float) 1.0, (float) 2.0);
                user.sendMessage(Message.SECOUND_LEFT, time);
            }
        }

        if (time <= 0 || Bukkit.getOnlinePlayers().size() <= 1) {
            cancel();
            isTimerRunning = false;
            time = 30;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Miners.getInstance().getGameScoreboard().updateTime(onlinePlayer);
        }
    }

}
