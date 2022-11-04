package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.Line;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerLineBuilderInteract extends Listener<PlayerInteractEvent> {

	public static final Item LINE_BUILDER = PerkType.LINE_BUILDER.getItem();
	public static final Item LINE_BUILDER_COOLDOWN = PerkType.LINE_BUILDER.getCooldownItem();

	public Map<User, Line> lines = new HashMap<>();
	public Map<User, LineBuilderScheduler> cooldownTasks = new HashMap<>();
//	private Map<User, ResetLineScheduler> resetTasks = new HashMap<>();
	public Map<User, LineBuilderBlockPlacerScheduler> placeTasks = new HashMap<>();
	public Map<User, Long> lastLine = new HashMap<>();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			if (item == null)
				return;
			String itemid = ItemManager.getItemId(item);
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (LINE_BUILDER_COOLDOWN.getItemId().equals(itemid)) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				e.setCancelled(true);
				return;
			} else if (!LINE_BUILDER.getItemId().equals(itemid)) {
				return;
			}
			e.setCancelled(true);
			if (!p.getInventory().contains(Material.WOOL, PerkType.LINE_BUILDER.getCost())
					|| (perk.getCooldown() == perk.getMaxCooldown() && perk.getCooldown() != 0)) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			}
			lastLine.put(user, System.currentTimeMillis());

			if (placeTasks.get(user) == null) {
				placeTasks.put(user, new LineBuilderBlockPlacerScheduler(perk));
				placeTasks.get(user).runTaskTimer(1);
			}

		} else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			lines.remove(user);
		}
	}

	private class LineBuilderBlockPlacerScheduler extends Scheduler {

		private Perk perk;
		private Scheduler runner = new Scheduler() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				User user = perk.getOwner();
				ItemManager.removeItems(user, user.getBukkitEntity().getInventory(),
						new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()),
						PerkType.LINE_BUILDER.getCost());

				perk.setCooldown(perk.getCooldown() + 1);

				if (cooldownTasks.get(user) == null) {
					cooldownTasks.put(user, new LineBuilderScheduler(perk));
					cooldownTasks.get(user).runTaskTimer(20, 20);
				}
				Line line = lines.get(user);
				if (line == null) {
					Vector direction = getNiceLocation(user.getBukkitEntity().getLocation()).getDirection();
					line = new Line(direction);
					lines.put(user, line);
				}
				Location next = line.getNextBlock(user.getBukkitEntity().getLocation());
				line.addBlock(next);
				if (next.getBlock().getType() == Material.AIR) {
					next.getBlock().setType(Material.WOOL);
					next.getBlock().setData(user.getTeam().getType().getWoolColor());
					Main.getInstance().getIngame().placedBlocks.add(next.getBlock());
				}
				user.getBukkitEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 10, false, false),
						true);
			}
		};

		public LineBuilderBlockPlacerScheduler(Perk linebuilder) {
			perk = linebuilder;
			runner.runTaskTimer(3);
		}

		@Override
		public void run() {
			if (System.currentTimeMillis() - lastLine.get(perk.getOwner()) > 250) {
				runner.cancel();
				this.cancel();
				placeTasks.remove(perk.getOwner());
				lines.remove(perk.getOwner());
				return;
			}
		}

	}

	private class LineBuilderScheduler extends Scheduler {
		private final Perk perk;

		public LineBuilderScheduler(Perk linebuilder) {
			this.perk = linebuilder;
		}

		@Override
		public void run() {
			if (perk.getCooldown() <= 0) {
				this.cancel();
				return;
			}
			perk.setCooldown(perk.getCooldown() - 1);
		}

		@Override
		public void cancel() {
			super.cancel();
			cooldownTasks.remove(perk.getOwner());
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
