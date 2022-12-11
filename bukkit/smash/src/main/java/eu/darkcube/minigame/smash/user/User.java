/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.user;

import static java.lang.Math.*;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.smash.character.Character;
import eu.darkcube.minigame.smash.listener.BaseListener;
import eu.darkcube.minigame.smash.util.InventoryId;
import eu.darkcube.minigame.smash.util.Item;
import eu.darkcube.minigame.smash.util.ParticleEffect;
import eu.darkcube.minigame.smash.util.scheduler.Scheduler;

public class User extends BaseListener implements eu.darkcube.minigame.smash.api.user.User {

	private OfflinePlayer player;
	private int percent = 0;
	private Language language;
	private Character character = Character.KIRBY;
	private int kills = 0;
	private int deaths = 0;
	private InventoryId inv = null;
	private String prefix = "§7";
	private String suffix = "";
	private Random random = new Random();
	private int jumpUses = 0;
	private long shieldTicksUpdated = 0;
	private int shieldTicksUsed = 0;
	private boolean shieldActive = false;
	private int stunTicks;
	private int spawnProtectionTicks = 0;
	private Scheduler shieldTicksRunnable = new Scheduler() {
		private int iteration = 0;

		@Override
		public void run() {
			if (shieldTicksUpdated + 1000 < System.currentTimeMillis()) {
				shieldTicksUsed--;
			}
			if (isShieldActive()) {
				iteration++;
				iteration %= 4;
				if (iteration == 1) {
					double max = 0.5;
					double rad = 0;
					double min = 0.3;
					double y = -0.3;
					while (true) {
						rad += 0.1;
						if (rad > 1) {
							break;
						}
						displayRing(min, max * rad, y + 0.3);
						displayRing(min, max * rad, getPlayer().getEyeHeight(true) - y + 0.3);
						y += (getPlayer().getEyeHeight() + 0.6) / 20;
					}
				}
			}
		}

		private double theta = 0;

		private void displayRing(double min, double radius, double y) {
			Player p = getPlayer();
			for (theta = 0; theta < PI;) {
				theta += PI / 6D;
				Location loc = p.getLocation();
				double x = radius * cos(theta);
				double z = radius * sin(theta);
				Vector v = new Vector(cos(theta) * min, 0, sin(theta) * min);
				try {
					ParticleEffect.REDSTONE.display(
							new ParticleEffect.OrdinaryColor(getCharacter().getShieldSmash().getShieldColor()),
							loc.clone().add(v).add(x, y, z), 100);
					ParticleEffect.REDSTONE.display(
							new ParticleEffect.OrdinaryColor(getCharacter().getShieldSmash().getShieldColor()),
							loc.clone().subtract(v).subtract(x, -y, z), 100);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	};
	private boolean stunned = false;

	public User(UUID uniqueId) {
		this(uniqueId, Language.GERMAN);
	}

	public User(UUID uniqueId, Language language) {
		this(Bukkit.getOfflinePlayer(uniqueId), language);
	}

	public User(OfflinePlayer player, Language language) {
		this.player = player;
		this.language = language;
		register();
		shieldTicksRunnable.runTaskTimer(1, 1);
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler
	public void handle(InventoryCloseEvent e) {
		if (e.getPlayer().equals(player)) {
			setInv(null);
		}
	}

	@Override
	public boolean isShieldActive() {
		return shieldActive;
	}

	@Override
	public void setShieldActive(boolean shieldActive) {
		Player p = getPlayer();
		if (shieldActive) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000000, 255, false, false), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000000, 128, false, false), true);
		} else {
			p.removePotionEffect(PotionEffectType.SLOW);
			p.removePotionEffect(PotionEffectType.JUMP);
		}
		this.shieldActive = shieldActive;
	}

	@Override
	public int getShieldTimeUsed() {
		return shieldTicksUsed;
	}

	@Override
	public void setShieldTimeUsed(int shieldTimeUsed) {
		this.shieldTicksUsed = shieldTimeUsed;
		shieldTicksUpdated = System.currentTimeMillis();
	}

	@Override
	public int getJumpUses() {
		return jumpUses;
	}

	@Override
	public void setJumpUses(int jumpUses) {
		this.jumpUses = jumpUses;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public Language getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setInv(InventoryId inv) {
		this.inv = inv;
		if (isOnline() && inv != null) {
			getPlayer().openInventory(inv.build(this));
		}
	}

	public InventoryId getInv() {
		return inv;
	}

	@Override
	public int getKills() {
		return kills;
	}

	@Override
	public int getDeaths() {
		return deaths;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}

	@Override
	public Character getCharacter() {
		return character;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	@Override
	public OfflinePlayer getOfflinePlayer() {
		return player;
	}

	@Override
	public CraftPlayer getPlayer() {
		return (CraftPlayer) getOfflinePlayer().getPlayer();
	}

	@Override
	public boolean isOnline() {
		return player.isOnline();
	}

	@Override
	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void handle(PlayerMoveEvent e) {
		if (e.getPlayer().equals(getPlayer())) {
			if (stunned || isShieldActive()) {
				if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY()
						|| e.getFrom().getZ() != e.getTo().getZ()) {
					e.setTo(e.getFrom());
				}
			}
			new Scheduler() {
				@Override
				public void run() {
					if (getJumpUses() != 0) {
						if (e.getPlayer().isOnGround()) {
							setJumpUses(0);
						}
					}
				}
			}.runTask();
		}
	}

	@Override
	public void stun(int ticks) {
		if (stunned) {
			return;
		}
		stunned = true;
		Player p = getPlayer();
		p.removePotionEffect(PotionEffectType.JUMP);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, false, false));
		setSpeedMultiplier(0);
		stunTicks = ticks;
		new Scheduler() {
			@Override
			public void run() {
				stunTicks--;
				if (stunTicks <= 0) {
					stunTicks = 0;
					stunned = false;
					p.removePotionEffect(PotionEffectType.JUMP);
					resetSpeedMultiplier();
					cancel();
				}
			}
		}.runTaskTimer(0, 1);
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				p.removePotionEffect(PotionEffectType.JUMP);
//				resetSpeedMultiplier();
//				stunned = false;
//			}
//		}.runTaskLater(Main.getInstance(), ticks);
	}

	@Override
	public int getStunTicks() {
		return stunTicks;
	}

	public void resetSpeedMultiplier() {
		character.resetSpeedMultiplier(this);
	}

	public void setSpeedMultiplier(float speed) {
		Player p = getPlayer();
		if(speed > 1) {
			speed /= 2;
		}
		speed *= .2;
		if (speed > 1)
			speed = 1;
		if (speed < 0)
			speed = 0;
		p.setWalkSpeed(speed);
	}

	public void kill() {
		System.out.println("killed");
	}

	@Override
	public void damage(int percent) {
		setPercent(getPercent() + percent);
		getPlayer().setNoDamageTicks(0);
		getPlayer().damage(0);
		if (percent > 2) {
			if (getPercent() > 80) {
				if (getPercent() >= 180) {
					kill();
				} else if (random.nextInt(180 - getPercent()) <= 2) {
					kill();
				}
			}
		}
	}

	public void damage() {
		this.damage(0);
	}

	@Override
	public boolean hasAddon() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setItems() {
		if (isOnline()) {
			Player p = getPlayer();
			PlayerInventory inv = p.getInventory();
			inv.clear();
			inv.setArmorContents(new ItemStack[4]);
			inv.setItem(0, Item.DOWN_SMASH.getItem(this));
			inv.setItem(1, Item.FRONT_SMASH.getItem(this));
			inv.setItem(2, Item.BASE_SMASH.getItem(this));
			inv.setItem(3, Item.JUMP_SMASH.getItem(this));
			inv.setItem(4, Item.SHIELD_SMASH.getItem(this));
			inv.setItem(5, Item.UP_SMASH.getItem(this));
		}
	}

	@Override
	public void setSpawnProtectionTicks(int ticks) {
		spawnProtectionTicks = ticks;
	}

	@Override
	public int getSpawnProtectionTicks() {
		return spawnProtectionTicks;
	}
}
