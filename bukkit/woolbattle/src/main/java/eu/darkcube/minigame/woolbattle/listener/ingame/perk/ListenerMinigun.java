/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerMinigun extends BasicPerkListener {

	public ListenerMinigun() {
		super(PerkType.MINIGUN);
	}

	private static final String DATA_SCHEDULER = "minigunScheduler";

	@Override
	protected boolean activateRight(User user, Perk perk) {
		if (user.getTemporaryDataStorage().has(DATA_SCHEDULER)) {
			return false;
		}
		user.getTemporaryDataStorage().set(DATA_SCHEDULER, new Scheduler() {
			private int count = 0;
			{
				runTaskTimer(3);
			}

			@Override
			public void run() {
				Player p = user.getBukkitEntity();
				ItemStack item = p.getItemInHand();
				if (count >= 20 || item == null || !checkUsable(user, getPerkType(), item)) {
					stop(user);
					return;
				}
				count++;
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3, false, false),
						true);
				Snowball s = p.getWorld().spawn(p.getEyeLocation(), Snowball.class);
				s.setShooter(p);
				s.setVelocity(p.getLocation().getDirection().multiply(2.5));
				s.setMetadata("type", new FixedMetadataValue(WoolBattle.getInstance(), "minigun"));
				payForThePerk(perk);
			}

			@Override
			public void cancel() {
				startCooldown(perk);
				super.cancel();
			}
		});

		return false;
	}


	@EventHandler
	public void handle(PlayerItemHeldEvent event) {
		User user =
				WoolBattle.getInstance().getUserWrapper().getUser(event.getPlayer().getUniqueId());
		stop(user);
	}

	private void stop(User user) {
		if (user.getTemporaryDataStorage().has(DATA_SCHEDULER)) {
			user.getTemporaryDataStorage().<Scheduler>remove(DATA_SCHEDULER).cancel();
		}
	}
}
