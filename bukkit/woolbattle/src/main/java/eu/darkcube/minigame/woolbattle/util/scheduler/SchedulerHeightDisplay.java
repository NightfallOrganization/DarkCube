/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.util.ChatUtils.ChatEntry;

public class SchedulerHeightDisplay extends Scheduler {

	public void start() {
		this.runTaskTimer(1 * 20);
	}

	public void stop() {
		cancel();
	}

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(this::display);
	}

	public final void display(Player p) {
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		HeightDisplay display = user.getData().getHeightDisplay();
		if (display.isEnabled()) {
			int deathHeight = WoolBattle.getInstance().getMap().getDeathHeight();
			int currentHeight = p.getLocation().getBlockY();
			int diff = (diff = currentHeight - deathHeight) < 0 ? 0 : diff;

			if (display.maxDistance == -1 || display.maxDistance < diff) {
				Team team = user.getTeam();
				if (team != null) {
					if (team.getType() != TeamType.SPECTATOR) {
						ChatEntry.buildActionbar(new ChatEntry.Builder().text(
										"§8» " + display.getColor().toString() + diff + " §8«").build())
								.send(p);
					}
				}
			}
		}
	}
}
