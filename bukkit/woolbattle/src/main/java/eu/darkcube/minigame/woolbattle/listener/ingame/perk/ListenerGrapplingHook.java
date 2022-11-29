package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.PerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGrapplingHook extends PerkListener {

	public static final Item HOOK = PerkType.GRAPPLING_HOOK.getItem();
	public static final Item HOOK_COOLDOWN = PerkType.GRAPPLING_HOOK.getCooldownItem();

	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		Projectile proj = e.getEntity();
		if (!proj.getType().equals(EntityType.FISHING_HOOK)) {
			return;
		}
		if (!(proj.getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) proj.getShooter();
		ItemStack item = p.getItemInHand();
		if (item == null) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		if (itemid == null) {
			return;
		}
		if (itemid.equals(HOOK.getItemId())) {
			proj.setVelocity(proj.getVelocity().multiply(1.5));
		}
	}

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (item == null) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		if (itemid == null) {
			return;
		}
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (itemid.equals(HOOK_COOLDOWN.getItemId())) {
			Ingame.playSoundNotEnoughWool(user);
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void handle(PlayerFishEvent e) {
		PlayerFishEvent.State state = e.getState();
		if (state == PlayerFishEvent.State.IN_GROUND || state == PlayerFishEvent.State.CAUGHT_ENTITY
				|| e.getHook().getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
			Player p = e.getPlayer();
			FishHook hook = e.getHook();
			ItemStack item = p.getItemInHand();
			if (item == null) {
				return;
			}
			String itemid = ItemManager.getItemId(item);
			if (itemid == null) {
				return;
			}
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (!itemid.equals(HOOK.getItemId())) {
				return;
			}
			if (!p.getInventory().contains(Material.WOOL, PerkType.GRAPPLING_HOOK.getCost())
					|| perk.getCooldown() > 0) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				hook.remove();
				e.setCancelled(true);
				return;
			}
			ItemManager.removeItems(user, p.getInventory(),
					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColorByte()),
					PerkType.GRAPPLING_HOOK.getCost());

			Location from = p.getLocation();
			Location to = hook.getLocation();

			double x = to.getX() - from.getX();
			double y = to.getY() - from.getY() + 3;
			double z = to.getZ() - from.getZ();
			Vector v = new Vector(x, y, z);
			double mult = Math.pow(v.length(), 0.35);
			v = v.normalize().multiply(new Vector(mult, mult * 1.1, mult));

			p.setVelocity(v);

			startCooldown(perk);
		}
	}
}
