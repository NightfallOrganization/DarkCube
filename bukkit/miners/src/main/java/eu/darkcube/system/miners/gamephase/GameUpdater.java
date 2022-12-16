package eu.darkcube.system.miners.gamephase;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.miners.Miners;

public class GameUpdater {

	private BukkitRunnable runnable;

	public GameUpdater() {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				List<Integer> teamsAlive = new ArrayList<>();
				for (int i = 1; i <= Miners.getMinersConfig().TEAM_COUNT; i++)
					if (Miners.getTeamManager().getPlayersInTeam(i).size() != 0)
						teamsAlive.add(i);
				if (teamsAlive.size() < 2)
					Miners.endGame();
				if (Miners.getGamephase() == 1)
					Bukkit.getOnlinePlayers().forEach(p -> {
						p.setSaturation(20);
						p.setFoodLevel(20);
					});
			}
		};
	}

	public void start() {
		runnable.runTaskTimer(Miners.getInstance(), 20, 20);
	}

	public void stop() {
		if (runnable != null)
			runnable.cancel();
		runnable = null;
	}

}
