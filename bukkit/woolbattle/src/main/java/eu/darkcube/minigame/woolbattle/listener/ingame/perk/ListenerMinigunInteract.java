package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerMinigunInteract extends Listener<PlayerInteractEvent> {

	public static final Item MINIGUN = PerkType.MINIGUN.getItem();
	public static final Item MINIGUN_COOLDOWN = PerkType.MINIGUN.getCooldownItem();

	public List<User> users = new ArrayList<>();
	
	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			ItemStack item = e.getItem();
			if (item == null)
				return;
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			final Perk perk = user.getPerkByItemId(ItemManager.getItemId(item));
			if (MINIGUN_COOLDOWN.getItemId().equals(ItemManager.getItemId(item))) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				e.setCancelled(true);
				return;
			} else if (!MINIGUN.getItemId().equals(ItemManager.getItemId(item))) {
				return;
			} else if (perk == null) {
				e.setCancelled(true);
				return;
			}
			if(users.contains(user)) {
				return;
			}
			e.setCancelled(true);
			if (!p.getInventory().contains(Material.WOOL, PerkType.MINIGUN.getCost()) || perk.getCooldown() > 0) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			}
			
			users.add(user);

			new Scheduler() {
				private int count = 0;

				@Override
				public void run() {
					ItemStack item = p.getItemInHand();
					if (count >= 20 || item == null || !ItemManager.getItemId(item).equals(MINIGUN.getItemId())
							|| !p.getInventory().contains(Material.WOOL, PerkType.MINIGUN.getCost())
							|| perk.getCooldown() > 0) {
						this.cancel();
						return;
					}

					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3, false, false));

					count++;
					Snowball s = p.getWorld().spawn(p.getEyeLocation(), Snowball.class);
					s.setShooter(p);
					s.setVelocity(p.getLocation().getDirection().multiply(2.5));
					s.setMetadata("type", new FixedMetadataValue(WoolBattle.getInstance(), "minigun"));

					ItemManager.removeItems(user, p.getInventory(),
							new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColorByte()),
							PerkType.MINIGUN.getCost());
				}

				@Override
				public void cancel() {
					super.cancel();
					new Scheduler() {
						int cd = PerkType.MINIGUN.getCooldown() + 1;

						@Override
						public void run() {
							if (cd <= 1) {
								this.cancel();
								perk.setCooldown(0);
								users.remove(user);
								return;
							}
							perk.setCooldown(--cd);
						}
					}.runTaskTimer(20);
				};
			}.runTaskTimer(3);
		}
	}
}
