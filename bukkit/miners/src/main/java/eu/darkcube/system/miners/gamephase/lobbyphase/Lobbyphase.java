/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.lobbyphase;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.util.Timer;

public class Lobbyphase {

	private BukkitRunnable lobbyUpdater;
	private Timer lobbyTimer;

	public Lobbyphase() {
		if (Miners.getGamephase() != 0)
			return;

		lobbyTimer = new Timer() {

			@Override
			public void onIncrement() {
				updateXpBar();
			}

			@Override
			public void onEnd() {
				Miners.getTeamManager().distributeRemainingPlayers();
				Miners.getTeamManager().fixTeams();
				Miners.nextGamephase();
			}
		};

		lobbyUpdater = new BukkitRunnable() {

			@Override
			public void run() {
				int playerCount = Bukkit.getOnlinePlayers().size();
				if (lobbyTimer.isRunning()) { // if timer is running check if it should stop, continue or quick start
					if (playerCount < Miners.getMinersConfig().MIN_PLAYERS)
						lobbyTimer.cancel(false);
					else if (playerCount == Miners.getMinersConfig().MAX_PLAYERS
							&& lobbyTimer.getTimeRemainingMillis() > Miners.getMinersConfig().LOBBY_TIMER_QUICK * 1000)
						lobbyTimer.setEndTime(
								System.currentTimeMillis() + Miners.getMinersConfig().LOBBY_TIMER_QUICK * 1000);
				} else { // if timer is not running check if it should start or quick start
					if (playerCount >= Miners.getMinersConfig().MIN_PLAYERS)
						lobbyTimer.start(Miners.getMinersConfig().LOBBY_TIMER_DEFAULT * 1000);
					else
						resetXpBar();
					if (playerCount == Miners.getMinersConfig().MAX_PLAYERS)
						lobbyTimer.setEndTime(
								System.currentTimeMillis() + Miners.getMinersConfig().LOBBY_TIMER_QUICK * 1000);
				}
			}
		};
		lobbyUpdater.runTaskTimer(Miners.getInstance(), 20, 20);
	}

	public void disable() {
		lobbyTimer.cancel(false);
		if (lobbyUpdater != null) {
			lobbyUpdater.cancel();
			lobbyUpdater = null;
		}
	}

	public Timer getTimer() {
		return lobbyTimer;
	}

	public void updateXpBar() {
		float totalTime = lobbyTimer.getOriginalEndTime() - lobbyTimer.getStartTime();
		float div = lobbyTimer.getTimeRemainingMillis() / totalTime;

		int remainingSeconds = (int) Math.ceil(lobbyTimer.getTimeRemainingMillis() / 1000);

		Bukkit.getOnlinePlayers().forEach(p -> {
			p.setExp(div);
			p.setLevel(remainingSeconds);
		});
	}

	public void resetXpBar() {
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.setExp(1);
			p.setLevel(Miners.getMinersConfig().LOBBY_TIMER_DEFAULT);
		});
	}

}
