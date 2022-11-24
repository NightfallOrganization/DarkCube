package eu.darkcube.system.miners.lobbyphase;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.util.Timer;

public class Lobbyphase {

	private BukkitRunnable lobbyUpdater;
	private Timer lobbyTimer;

	private int teams;
	private int teamSize;
	private int maxPlayers;
	private int minPlayers;
	private int timerDefault;
	private int timerQuick;

	public Lobbyphase() {
		teams = Miners.getConfigFile().getInt("teams");
		teamSize = Miners.getConfigFile().getInt("teamSize");
		maxPlayers = teams * teamSize;
		minPlayers = Miners.getConfigFile().getInt("minPlayers");
		timerDefault = Miners.getConfigFile().getInt("timerDefault");
		timerQuick = Miners.getConfigFile().getInt("timerQuick");

		if (Miners.getGamephase() != 0)
			return;

		lobbyTimer = new Timer() {

			@Override
			public void onIncrement() {
				updateXpBar();
			}

			@Override
			public void onEnd() {
				// startNextPhase();
				System.out.println("init gamephase 1");
			}
		};

		lobbyUpdater = new BukkitRunnable() {

			@Override
			public void run() {
				int playerCount = Bukkit.getOnlinePlayers().size();
				if (lobbyTimer.isRunning()) { // if timer is running check if it should stop, continue or quick start
					if (playerCount < minPlayers)
						lobbyTimer.cancel(false);
					else if (playerCount == maxPlayers && lobbyTimer.getTimeRemaining() > timerQuick * 1000)
						lobbyTimer.setEndTime(System.currentTimeMillis() + timerQuick * 1000);
				} else { // if timer is not running check if it should start or quick start
					if (playerCount >= minPlayers)
						lobbyTimer.start(timerDefault * 1000);
					else
						resetXpBar();
					if (playerCount == maxPlayers)
						lobbyTimer.setEndTime(System.currentTimeMillis() + timerQuick * 1000);
				}
			}
		};
		lobbyUpdater.runTaskTimer(Miners.getInstance(), 20, 20);
	}

	public Timer getTimer() {
		return lobbyTimer;
	}

	public void startNextPhase() {
		if (Miners.getGamephase() != 0)
			return;
		lobbyUpdater.cancel();
	}

	public void updateXpBar() {
		float totalTime = lobbyTimer.getOriginalEndTime() - lobbyTimer.getStartTime();
		float div = lobbyTimer.getTimeRemaining() / totalTime;

		int remainingSeconds = (int) Math.ceil(lobbyTimer.getTimeRemaining() / 1000);

		Bukkit.getOnlinePlayers().forEach(p -> {
			p.setExp(div);
			p.setLevel(remainingSeconds);
		});
	}

	public void resetXpBar() {
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.setExp(1);
			p.setLevel(timerDefault);
		});
	}

}
