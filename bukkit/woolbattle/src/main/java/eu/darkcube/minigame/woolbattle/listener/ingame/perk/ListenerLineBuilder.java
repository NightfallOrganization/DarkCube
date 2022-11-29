package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Line;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerLineBuilder extends BasicPerkListener {

	private static final String DATA_SCHEDULER = "linebuilderScheduler";

	public ListenerLineBuilder() {
		super(PerkType.LINE_BUILDER);
	}

	@Override
	protected boolean activateRight(User user, Perk perk) {
		if (!user.getTemporaryDataStorage().has(DATA_SCHEDULER)) {
			user.getTemporaryDataStorage().set(DATA_SCHEDULER, new TheScheduler(perk));
		}
		TheScheduler s = user.getTemporaryDataStorage().<TheScheduler>get(DATA_SCHEDULER);
		s.lastLine = s.tick;
		return false;
	}

	@Override
	protected boolean activateLeft(User user, Perk perk) {
		user.getTemporaryDataStorage().remove(DATA_SCHEDULER);
		return false;
	}

	private class TheScheduler extends Scheduler {
		private int lastLine = 0;
		private Line line = null;
		private int cooldownTicks = 0;
		private Perk perk;
		private User user;
		private int tick = 0;

		public TheScheduler(Perk perk) {
			this.perk = perk;
			this.user = perk.getOwner();
			runTaskTimer(1);
		}

		@Override
		public void run() {
			tick++;
			if (cooldownTicks > 1) {
				cooldownTicks--;
			} else {
				if (tick - lastLine > 5) {
					user.getTemporaryDataStorage().remove(DATA_SCHEDULER);
					cancel();
					return;
				}
			}
			if (tick - lastLine > 5) {
				line = null;
				return;
			}
			if (cooldownTicks > TimeUnit.SECOND.toUnit(TimeUnit.TICKS) * perk.getMaxCooldown()) {
				startCooldown(perk);
				user.getTemporaryDataStorage().remove(DATA_SCHEDULER);
				cancel();
				return;
			}
			if (tick % 3 == 1) {
				payForThePerk(user, getPerkType());
				cooldownTicks += TimeUnit.SECOND.toUnit(TimeUnit.TICKS);
				if (line == null) {
					line = new Line(
							getNiceLocation(user.getBukkitEntity().getLocation()).getDirection());
				}
				Location next = line.getNextBlock(user.getBukkitEntity().getLocation());
				line.addBlock(next);
				if (next.getBlock().getType() == Material.AIR) {
					next.getBlock().setType(Material.WOOL);
					BlockState state = next.getBlock().getState();
					Wool wool = (Wool) state.getData();
					wool.setColor(user.getTeam().getType().getWoolColor());
					state.setData(wool);
					state.update(true);
					WoolBattle.getInstance().getIngame().placedBlocks.add(next.getBlock());
				}
				user.getBukkitEntity().addPotionEffect(
						new PotionEffect(PotionEffectType.SLOW, 20, 10, false, false), true);
			}
		}
	}

	public static Location getNiceLocation(Location loc) {
		Location l = loc.clone();
		l.setYaw(getNiceYaw(l.getYaw()));
		l.setPitch(0);
		l.setX(l.getBlockX());
		l.setY(l.getBlockY());
		l.setZ(l.getBlockZ());
		return l;
	}

	public static float getNiceYaw(float y) {
		float interval = 45f;
		float half = interval / 2f;
		y %= 360F;

		if (y < 0)
			y = 360F + y;

		for (float i = 360; i >= 0; i -= interval) {
			float bound1 = i - half;
			float bound2 = i + half;
			if (y >= bound1 && y <= bound2) {
				y = i;
				break;
			}
		}
		return y;
	}
}
