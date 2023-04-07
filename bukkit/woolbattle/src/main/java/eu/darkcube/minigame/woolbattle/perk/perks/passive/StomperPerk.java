/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.other.DoubleJumpEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class StomperPerk extends Perk {
	public static final PerkName STOMPER = new PerkName("STOMPER");
	private static final Key active = new Key(WoolBattle.instance(), "perk_stomper_active");

	public StomperPerk() {
		super(ActivationType.PASSIVE, STOMPER, 0, 10, Item.PERK_STOMPER, DefaultUserPerk::new);
		addListener(new StomperListener());
		addScheduler(new StomperScheduler());
	}

	public class StomperScheduler extends Scheduler implements ConfiguredScheduler {

		@Override
		public void run() {
			for (WBUser user : WBUser.onlineUsers()) {
				if (!user.getTeam().canPlay())
					continue;
				if (!user.user().getMetaDataStorage().has(active))
					continue;
				int size = user.user().getMetaDataStorage().remove(active);
				double rad = size * 3;
				
			}
		}

		@Override
		public void start() {
			runTaskTimer(1);
		}

		@Override
		public void stop() {
			cancel();
		}
	}

	public class StomperListener implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void handle(DoubleJumpEvent event) {
			int size = event.user().perks().perks(perkName()).size();
			if (size > 0) {
				event.user().user().getMetaDataStorage().set(active, size);
			}
		}
	}
}
