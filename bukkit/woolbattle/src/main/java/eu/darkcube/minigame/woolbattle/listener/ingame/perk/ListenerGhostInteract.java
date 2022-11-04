package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
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
import net.minecraft.server.v1_8_R3.EntityZombie;

public class ListenerGhostInteract extends Listener<PlayerInteractEvent> {
	public Map<User, Zombie> ghosts = new HashMap<>();

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
		if (GHOST_COOLDOWN.getItemId().equals(itemid)) {
			if (perk.getCooldown() > 0) {
				e.setCancelled(true);
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					deny(user, perk);
				} else {
					if (ghosts.containsKey(user)) {
						Main.getInstance().getIngame().listenerGhostEntityDamageByEntity.reset(user, ghosts.get(user));
					}
				}
				return;
			}
		} else if (!GHOST.getItemId().equals(itemid)) {
			return;
		}
		if (!p.getInventory().contains(Material.WOOL, PerkType.GHOST.getCost())) {
			e.setCancelled(true);
			deny(user, perk);
			return;
		}
		e.setCancelled(true);

		ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(), PerkType.GHOST.getCost());

		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 200, false, false));
		Zombie zombie = new GhostZombie(p.getWorld(), user).getBukkitEntity();
		ghosts.put(user, zombie);
		Main.getInstance().getIngame().setArmor(user);
//			zombie.teleport(p.getLocation());

		perk.setCooldown(perk.getMaxCooldown());
		p.setMaxHealth(20);

		Main.getInstance().getIngame().listenerDoubleJump.refresh(p);
		new Scheduler() {
			int cd = perk.getMaxCooldown();

			@Override
			public void run() {
				if (ghosts.get(user) == null) {
					if (cd <= 0) {
						this.cancel();
						perk.setCooldown(0);
						Main.getInstance().getIngame().listenerDoubleJump.refresh(p);
						return;
					}
					perk.setCooldown(--cd);
				}
			}
		}.runTaskTimer(20);
		new Scheduler() {

			@Override
			public void run() {
				if (ghosts.get(user) == null) {
					cancel();
					p.removePotionEffect(PotionEffectType.BLINDNESS);
					p.removePotionEffect(PotionEffectType.SPEED);
					return;
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 0, false, false), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 10, false, false), true);
			}
		}.runTaskTimer(1);
//		}
	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.getOwner().getBukkitEntity().setItemInHand(perk.calculateItem());
	}

	private static class GhostZombie {

		private EntityZombie zombie;

		public GhostZombie(World world, User user) {
			Player p = user.getBukkitEntity();
			Zombie zombie = world.spawn(p.getLocation(), Zombie.class);
			zombie.setBaby(false);
			zombie.setCanPickupItems(false);
			zombie.setCustomName(user.getTeamPlayerName());
			zombie.setVillager(false);
			zombie.getEquipment().setBoots(p.getInventory().getBoots());
			zombie.getEquipment().setLeggings(p.getInventory().getLeggings());
			zombie.getEquipment().setChestplate(p.getInventory().getChestplate());
			zombie.getEquipment().setHelmet(p.getInventory().getHelmet());
			zombie.getEquipment().setItemInHand(p.getItemInHand());
			this.zombie = ((CraftZombie) zombie).getHandle();
			this.zombie.getDataWatcher().watch(15, (byte) 1);
			this.zombie.b(true);
			zombie.setMetadata("isGhost", new FixedMetadataValue(Main.getInstance(), true));
			zombie.setMetadata("user", new FixedMetadataValue(Main.getInstance(), user.getUniqueId().toString()));

		}

		public Zombie getBukkitEntity() {
			return (Zombie) zombie.getBukkitEntity();
		}
	}
}