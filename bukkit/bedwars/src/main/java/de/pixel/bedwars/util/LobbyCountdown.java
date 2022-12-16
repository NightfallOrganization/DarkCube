/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.pixel.bedwars.Main;

public class LobbyCountdown implements Listener {

	private final int maxTimerTicks = 60 * 20;
	private int timerTicks = maxTimerTicks;
	private boolean started = false;
	private boolean inited = false;

	private BukkitRunnable runnable;

	public void cancel() {
		if (inited)
			runnable.cancel();
		inited = false;
		stop();
	}

	public void stop() {
		started = false;
		setTimerTicks(maxTimerTicks);
	}

	public void start() {
		checkInit();
		if (!started) {
			started = true;
		}
	}
	
	private void checkInit() {
		if (!inited) {
			runnable = new BukkitRunnable() {
				@Override
				public void run() {
					if (started) {
						setTimerTicks(timerTicks - 1);
					}
				}
			};
			inited = true;
			runnable.runTaskTimer(Main.getInstance(), 0, 1);
		}
	}

	public void setTimerSeconds(int timer) {
		setTimerTicks(timer * 20);
	}

	public void setTimerTicks(int timerTicks) {
		this.timerTicks = timerTicks;
		if (timerTicks == 0) {
			Main.getInstance().getIngame().activate();
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.setLevel(0);
				p.setExp(0);
			}
		} else if (inited) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.setLevel((int) Math.ceil(timerTicks / 20F));
				if (timerTicks >= maxTimerTicks) {
					p.setExp(0.99999F);
				} else {
					p.setExp((float) timerTicks / (float) maxTimerTicks);
				}
				setScoreboardValue(p);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void handle(PlayerJoinEvent e) {
		checkInit();
		setTimerTicks(timerTicks);
		if (Main.getInstance().getMaxPlayers() == Bukkit.getOnlinePlayers().size()) {
			setTimerSeconds(10);
		}
		if (Bukkit.getOnlinePlayers().size() == 2) {
			start();
		}
	}

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		if (Bukkit.getOnlinePlayers().size() - 1 < 2) {
			stop();
			setTimerTicks(maxTimerTicks);
		}
	}

	public void setScoreboardValue() {
		Bukkit.getOnlinePlayers().forEach(this::setScoreboardValue);
	}

	public void setScoreboardValue(Player p) {
		Scoreboard sb = p.getScoreboard();
		Team team = sb.getTeam(ScoreboardTeam.LOBBY_TIMER.getTag());
		team.setSuffix(Integer.toString((int) Math.ceil(timerTicks / 20F)));
	}
}
