package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Sound;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGrandpasClock extends BasicPerkListener {

	public ListenerGrandpasClock() {
		super(PerkType.GRANDPAS_CLOCK);
	}

	private static final String METADATA_OLDPOS = "grandpasClockOldPos";
	private static final String METADATA_TICKER = "grandpasClockTicker";

	@Override
	protected boolean activate(User user, Perk perk) {
		if (user.getTemporaryDataStorage().has(METADATA_OLDPOS)) {
			user.getBukkitEntity()
					.teleport(user.getTemporaryDataStorage().<Location>remove(METADATA_OLDPOS));
			user.getTemporaryDataStorage().<Scheduler>remove(METADATA_TICKER).cancel();
			user.getBukkitEntity().playSound(user.getBukkitEntity().getBedSpawnLocation(),
					Sound.ENDERMAN_TELEPORT, 100, 1);
			return true;
		}
		user.getTemporaryDataStorage().set(METADATA_OLDPOS, user.getBukkitEntity().getLocation());
		user.getTemporaryDataStorage().set(METADATA_TICKER, new Scheduler() {
			{
				runTaskTimer(10);
			}

			private int count = 0;

			@Override
			public void run() {
				if (count++ == 6) {
					user.getBukkitEntity().teleport(
							user.getTemporaryDataStorage().<Location>remove(METADATA_OLDPOS));
					user.getTemporaryDataStorage().remove(METADATA_TICKER);
					user.getBukkitEntity().playSound(user.getBukkitEntity().getBedSpawnLocation(),
							Sound.ENDERMAN_TELEPORT, 100, 1);
					cancel();
					activated(user, perk);
					return;
				}
				user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(), Sound.CLICK,
						100, 1);
			}
		});
		return false;
	}
}
