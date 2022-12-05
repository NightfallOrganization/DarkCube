package eu.darkcube.system.lobbysystem.user;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.EventGadgetSelect;
import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.inventory.InventoryPlayer;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.ParticleEffect;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.data.Key;
import eu.darkcube.system.userapi.data.PersistentDataType;
import eu.darkcube.system.userapi.data.PersistentDataTypes;

public class LobbyUser {
	private static final PersistentDataType<Set<Integer>> INTEGERS =
			PersistentDataTypes.set(PersistentDataTypes.INTEGER);
	private static final PersistentDataType<Gadget> TGADGET =
			PersistentDataTypes.enumType(Gadget.class);
	private static final PersistentDataType<PServerUserSlots> PSERVER_SLOTS =
			PersistentDataTypes.map(PersistentDataTypes.JSONDOCUMENT, slots -> {
				return slots.createDocument();
			}, doc -> {
				return new PServerUserSlots(doc);
			});
	private static final Key ANIMATIONS = new Key(Lobby.getInstance(), "animations");
	private static final Key SOUNDS = new Key(Lobby.getInstance(), "sounds");
	private static final Key LASTDAILYREWARD = new Key(Lobby.getInstance(), "lastDailyReward");
	private static final Key REWARDSLOTSUSED = new Key(Lobby.getInstance(), "rewardSlotsUsed");
	private static final Key GADGET = new Key(Lobby.getInstance(), "gadget");
	private static final Key POSITION = new Key(Lobby.getInstance(), "position");
	private static final Key SELECTEDSLOT = new Key(Lobby.getInstance(), "selectedSlot");
	volatile IInventory openInventory;
	boolean buildMode = false;
	PServerUserSlots slots;
	final User user;

	public LobbyUser(User user) {
		this.user = user;
		long time1 = System.currentTimeMillis();
		this.user.getPersistentDataStorage().setIfNotPresent(ANIMATIONS,
				PersistentDataTypes.BOOLEAN, true);
		this.user.getPersistentDataStorage().setIfNotPresent(SOUNDS, PersistentDataTypes.BOOLEAN,
				true);
		this.user.getPersistentDataStorage().setIfNotPresent(LASTDAILYREWARD,
				PersistentDataTypes.LONG, 0L);
		this.user.getPersistentDataStorage().setIfNotPresent(REWARDSLOTSUSED, INTEGERS,
				new HashSet<>());
		this.user.getPersistentDataStorage().setIfNotPresent(GADGET, TGADGET,
				Gadget.GRAPPLING_HOOK);
		this.user.getPersistentDataStorage().setIfNotPresent(SELECTEDSLOT,
				PersistentDataTypes.INTEGER, 0);
		long time2 = System.currentTimeMillis();
		this.openInventory = new InventoryPlayer();
		this.slots = new PServerUserSlots(user);
		if (System.currentTimeMillis() - time1 > 1000) {
			Lobby.getInstance().getLogger()
					.info("Loading LobbyUser took very long: "
							+ (System.currentTimeMillis() - time1) + " | "
							+ (System.currentTimeMillis() - time2));
		}
	}

	void unload() {
		this.slots.save();
		this.slots = null;
		this.openInventory = null;
		this.buildMode = false;
	}

	public User getUser() {
		return this.user;
	}

	public PServerUserSlots getSlots() {
		return this.slots;
	}

	public IInventory getOpenInventory() {
		return this.openInventory;
	}

	public LobbyUser setOpenInventory(IInventory openInventory) {
		boolean oldAnimations = this.isAnimations();
		if (this.openInventory != null
				&& openInventory.getClass().equals(this.openInventory.getClass())) {
			this.setAnimations(false);
		}

		Player p = user.asPlayer();
		if (p != null) {
			Runnable r = new Runnable() {

				@Override
				public void run() {
					if (openInventory.getHandle() != null) {
						openInventory.open(p);
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 100,
								false, false), true);
						LobbyUser.this.openInventory = openInventory;
					}
				}

			};
			if (!Bukkit.isPrimaryThread()) {
				Bukkit.getScheduler().runTask(Lobby.getInstance(), r);
			} else {
				r.run();
			}
		} else {
			this.openInventory = openInventory;
		}
		this.setAnimations(oldAnimations);
		return this;
	}

	public int getSelectedSlot() {
		return user.getPersistentDataStorage().get(SELECTEDSLOT, PersistentDataTypes.INTEGER);
	}

	public void setSelectedSlot(int slot) {
		user.getPersistentDataStorage().set(SELECTEDSLOT, PersistentDataTypes.INTEGER, slot);
	}

	public Set<Integer> getRewardSlotsUsed() {
		return user.getPersistentDataStorage().get(REWARDSLOTSUSED, INTEGERS);
	}

	public void setRewardSlotsUsed(Set<Integer> slots) {
		user.getPersistentDataStorage().set(REWARDSLOTSUSED, INTEGERS, slots);
	}

	public void setBuildMode(boolean buildMode) {
		this.buildMode = buildMode;
	}

	public boolean isBuildMode() {
		return this.buildMode;
	}

	public void setLastPosition(Location position) {
		user.getPersistentDataStorage().set(POSITION, PersistentDataTypes.LOCATION, position);
	}

	public Location getLastPosition() {
		return user.getPersistentDataStorage().get(POSITION, PersistentDataTypes.LOCATION,
				() -> Lobby.getInstance().getDataManager().getSpawn());
	}

	public boolean isSounds() {
		return user.getPersistentDataStorage().get(SOUNDS, PersistentDataTypes.BOOLEAN);
	}

	public boolean isAnimations() {
		return user.getPersistentDataStorage().get(ANIMATIONS, PersistentDataTypes.BOOLEAN);
	}

	public long getLastDailyReward() {
		return user.getPersistentDataStorage().get(LASTDAILYREWARD, PersistentDataTypes.LONG);
	}

	public void setLastDailyReward(long lastDailyReward) {
		user.getPersistentDataStorage().set(LASTDAILYREWARD, PersistentDataTypes.LONG,
				lastDailyReward);
	}

	public void playSound(Sound sound, float volume, float pitch) {
		if (!this.isSounds())
			return;
		Player p = user.asPlayer();
		if (p != null) {
			this.playSound(p.getLocation(), sound, volume, pitch);
		}
	}

	public void playSound(Location loc, Sound sound, float volume, float pitch) {
		if (!this.isSounds())
			return;
		Player p = user.asPlayer();
		if (p != null) {
			p.playSound(loc, sound, volume, pitch);
		}
	}

	public void teleport(Location loc) {
		Player p = user.asPlayer();
		if (p != null) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 100, false, false),
					true);
			this.playSound(Sound.NOTE_PLING, 10, 1);
			p.teleport(loc);
			p.setGameMode(GameMode.SURVIVAL);
			p.setAllowFlight(true);
			new BukkitRunnable() {

				double t = 0;

				double r = .8;

				double x, y, z;

				@Override
				public void run() {
					for (int i = 0; i < 4; i++) {
						this.t = this.t + Math.PI / 16;
						this.x = this.r * Math.cos(this.t);
						this.y = 0.1 * this.t;
						this.z = this.r * Math.sin(this.t);
						loc.add(this.x, this.y, this.z);
						ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 1, loc, 20);
						loc.subtract(this.x, this.y, this.z);
					}
					if (this.t > Math.PI * 8) {
						this.cancel();
					}
				}

			}.runTaskTimer(Lobby.getInstance(), 1, 1);
		}
	}

	public void setSounds(boolean sounds) {
		user.getPersistentDataStorage().set(SOUNDS, PersistentDataTypes.BOOLEAN, sounds);
	}

	public void setAnimations(boolean animations) {
		user.getPersistentDataStorage().set(ANIMATIONS, PersistentDataTypes.BOOLEAN, animations);
	}

	// public UserData newUserData() {
	// return new UserData(this.language, this.gadget, this.sounds, this.animations,
	// this.lastDailyReward, this.rewardSlotsUsed);
	// }

	public LobbyUser setGadget(Gadget gadget) {
		EventGadgetSelect e = new EventGadgetSelect(this, gadget);
		Bukkit.getPluginManager().callEvent(e);
		if (!e.isCancelled()) {
			Player p = user.asPlayer();
			if (p != null) {
				p.getInventory().setItem(4, Item.byGadget(gadget).getItem(user));
			}
			user.getPersistentDataStorage().set(GADGET, TGADGET, gadget);
		}
		return this;
	}

	public Gadget getGadget() {
		return user.getPersistentDataStorage().get(GADGET, TGADGET);
	}

}
