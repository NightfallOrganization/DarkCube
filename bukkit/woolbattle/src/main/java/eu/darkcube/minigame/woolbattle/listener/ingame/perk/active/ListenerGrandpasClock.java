/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.Location;
import org.bukkit.Sound;

public class ListenerGrandpasClock extends BasicPerkListener {

	private static final Key DATA_OLD_POS =
			new Key(WoolBattle.getInstance(), "grandpasClockOldPos");
	private static final Key DATA_TICKER = new Key(WoolBattle.getInstance(), "grandpasClockTicker");

	public ListenerGrandpasClock(Perk perk) {
		super(perk);
	}

	@Override
	protected boolean activate(UserPerk perk) {
		WBUser user = perk.owner();
		if (user.user().getMetaDataStorage().has(DATA_OLD_POS)) {
			user.getBukkitEntity()
					.teleport(user.user().getMetaDataStorage().<Location>remove(DATA_OLD_POS));
			user.user().getMetaDataStorage().<Scheduler>remove(DATA_TICKER).cancel();
			user.getBukkitEntity().playSound(user.getBukkitEntity().getBedSpawnLocation(),
					Sound.ENDERMAN_TELEPORT, 100, 1);
			return true;
		}
		user.user().getMetaDataStorage().set(DATA_OLD_POS, user.getBukkitEntity().getLocation());
		user.user().getMetaDataStorage().set(DATA_TICKER, new Scheduler() {
			private int count = 0;

			{
				runTaskTimer(10);
			}

			@Override
			public void run() {
				if (count++ == 6) {
					user.getBukkitEntity().teleport(
							user.user().getMetaDataStorage().<Location>remove(DATA_OLD_POS));
					user.user().getMetaDataStorage().remove(DATA_TICKER);
					user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(),
							Sound.ENDERMAN_TELEPORT, 1, 1);
					cancel();
					activated(perk);
					return;
				}
				user.getBukkitEntity()
						.playSound(user.getBukkitEntity().getLocation(), Sound.CLICK, 100, 1);
			}
		});
		return false;
	}
}
