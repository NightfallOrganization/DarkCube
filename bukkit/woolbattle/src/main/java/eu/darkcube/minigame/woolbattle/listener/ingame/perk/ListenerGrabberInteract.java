package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGrabberInteract extends Listener<PlayerInteractEvent> {
	public static final Item GRABBER = PerkType.GRABBER.getItem();
	public static final Item GRABBER_GRABBED = Item.PERK_GRABBER_GRABBED;
	public static final Item GRABBER_COOLDOWN = PerkType.GRABBER.getCooldownItem();

	public Map<User, User> grabbed = new HashMap<>();
	public Map<User, Integer> delay = new HashMap<>();
	public Map<User, Perk> perks = new HashMap<>();

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Egg && e.getEntity() instanceof Player
				&& ((Egg) e.getDamager()).getShooter() instanceof Player) {
			Main main = Main.getInstance();
			Player target = (Player) e.getEntity();
			User tuser = main.getUserWrapper().getUser(target.getUniqueId());
			Egg egg = (Egg) e.getDamager();
			Player p = (Player) egg.getShooter();
			User user = main.getUserWrapper().getUser(p.getUniqueId());
			if (egg.hasMetadata("type") && egg.getMetadata("type").get(0).asString().equals("grabber")) {
				if (!main.getIngame().listenerGrabberInteract.perks.containsKey(user)) {
					e.setCancelled(true);
					return;
				}
				if (main.getIngame().attack(user, tuser) || (user.getTeam() == tuser.getTeam() && user != tuser)) {
					if (user.getTeam() == tuser.getTeam() && user != tuser) {
						e.setCancelled(true);
					}
					main.getIngame().listenerGrabberInteract.grabbed.put(user, tuser);
					main.getIngame().listenerGrabberInteract.perks.remove(user).setItem();
				} else {
					e.setCancelled(true);
				}
			}
		}
	}

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			Player p = e.getPlayer();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (item == null)
				return;
			String itemid = ItemManager.getItemId(item);
			if (GRABBER_GRABBED.getItemId().equals(itemid)) {
				if (grabbed.containsKey(user)) {
					Location loc = user.getBukkitEntity().getLocation();
					if (loc.getY() < Main.getInstance().getMap().getDeathHeight() + 10) {
						loc.setY(Main.getInstance().getMap().getDeathHeight() + 10);
					}
					grabbed.remove(user).getBukkitEntity().teleport(loc);
				}
				delay.put(user, 1000);
				return;
			}
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (GRABBER_COOLDOWN.getItemId().equals(itemid)) {
				Ingame.playSoundNotEnoughWool(user);
				e.setCancelled(true);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			} else if (!GRABBER.getItemId().equals(itemid)) {
				return;
			}
			if (perks.containsKey(user)) {
				return;
			}
			if (!p.getInventory().contains(Material.WOOL, PerkType.GRABBER.getCost())) {
				Ingame.playSoundNotEnoughWool(user);
				e.setCancelled(true);
				new Scheduler() {

					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			}
			ItemManager.removeItems(user, p.getInventory(),
					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()),
					PerkType.GRABBER.getCost());

			perks.put(user, perk);

			Egg egg = p.getWorld().spawn(p.getEyeLocation(), Egg.class);
			egg.setShooter(p);
			egg.setVelocity(p.getLocation().getDirection().multiply(2.5));
			egg.setMetadata("type", new FixedMetadataValue(Main.getInstance(), "grabber"));
			new Scheduler() {
				int count = 0;

				@Override
				public void run() {
					count++;
					if (count > 100 || (delay.containsKey(user) && delay.get(user) >= 100)) {
						new Scheduler() {
							int cd = PerkType.GRABBER.getCooldown() + 1;
							boolean b = false;

							@Override
							public void run() {
								if (!b) {
									b = true;
									perks.remove(user);
									grabbed.remove(user);
									delay.remove(user);
								}
								if (cd <= 1) {
									this.cancel();
									perk.setCooldown(0);
									return;
								}
								perk.setCooldown(--cd);
							}
						}.runTaskTimer(20);
						this.cancel();
					}
				}
			}.runTaskTimer(1);
		}
	}
}
