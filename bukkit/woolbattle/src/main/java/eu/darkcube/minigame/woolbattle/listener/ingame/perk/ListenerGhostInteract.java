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

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGhostInteract extends Listener<PlayerInteractEvent> {

	public Map<User, Location> ghosts = new HashMap<>();

	public static final Item GHOST = PerkType.GHOST.getItem();

	public static final Item GHOST_COOLDOWN = PerkType.GHOST.getCooldownItem();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
//		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (item == null)
			return;
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (Main.getInstance().getTeamManager().getSpectator().contains(user.getUniqueId())) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		Perk perk = user.getPerkByItemId(itemid);
		if (perk == null)
			return;
		if (ListenerGhostInteract.GHOST_COOLDOWN.getItemId().equals(itemid)) {
			if (perk.getCooldown() > 0) {
				e.setCancelled(true);
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					this.deny(user, perk);
				} else {
					if (this.ghosts.containsKey(user)) {
						Main.getInstance().getIngame().listenerGhostEntityDamageByEntity.reset(user,
								this.ghosts.get(user));
					}
				}
				return;
			}
		} else if (!ListenerGhostInteract.GHOST.getItemId().equals(itemid)) {
			return;
		}
		if (!p.getInventory().contains(Material.WOOL, PerkType.GHOST.getCost())) {
			e.setCancelled(true);
			this.deny(user, perk);
			return;
		}
		e.setCancelled(true);

		ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(), PerkType.GHOST.getCost());

		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 200, false, false));
		this.ghosts.put(user, p.getLocation());
		Main.getInstance().getIngame().setArmor(user);

		perk.setCooldown(perk.getMaxCooldown());
		p.setMaxHealth(20);

		Main.getInstance().getIngame().listenerDoubleJump.refresh(p);
		new Scheduler() {

			int cd = perk.getMaxCooldown();

			@Override
			public void run() {
				if (ListenerGhostInteract.this.ghosts.get(user) == null) {
					if (this.cd <= 0) {
						this.cancel();
						perk.setCooldown(0);
						Main.getInstance().getIngame().listenerDoubleJump.refresh(p);
						return;
					}
					perk.setCooldown(--this.cd);
				}
			}

		}.runTaskTimer(20);
		new Scheduler() {

			@Override
			public void run() {
				if (ListenerGhostInteract.this.ghosts.get(user) == null) {
					this.cancel();
					p.removePotionEffect(PotionEffectType.BLINDNESS);
					p.removePotionEffect(PotionEffectType.SPEED);
					return;
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 0, false, false), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 10, false, false), true);
			}

		}.runTaskTimer(1);
	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		this.setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.getOwner().getBukkitEntity().setItemInHand(perk.calculateItem());
	}

}