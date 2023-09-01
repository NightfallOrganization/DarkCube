/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.user;

import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.util.data.BukkitPersistentDataTypes;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.inventory.InventoryPlayer;
import eu.darkcube.system.lobbysystem.jumpandrun.JaR;
import eu.darkcube.system.lobbysystem.util.ParticleEffect;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LobbyUser {
    private static final PersistentDataType<Set<Integer>> INTEGERS = PersistentDataTypes.set(PersistentDataTypes.INTEGER);
    private static final PersistentDataType<Gadget> TYPE_GADGET = PersistentDataTypes.enumType(Gadget.class);
    private static final Key ANIMATIONS = new Key(Lobby.getInstance(), "animations");
    private static final Key SOUNDS = new Key(Lobby.getInstance(), "sounds");
    private static final Key LAST_DAILY_REWARD = new Key(Lobby.getInstance(), "lastDailyReward");
    private static final Key REWARD_SLOTS_USED = new Key(Lobby.getInstance(), "rewardSlotsUsed");
    private static final Key GADGET = new Key(Lobby.getInstance(), "gadget");
    private static final Key POSITION = new Key(Lobby.getInstance(), "position");
    private static final Key SELECTED_SLOT = new Key(Lobby.getInstance(), "selectedSlot");
    private final User user;
    private boolean buildMode = false;
    private volatile IInventory openInventory;
    private JaR currentJaR;
    private boolean disableAnimations = false;
    private boolean disableSounds = false;

    public LobbyUser(User user) {
        this.user = user;
        long time1 = System.currentTimeMillis();
        this.user.persistentData().setIfNotPresent(ANIMATIONS, PersistentDataTypes.BOOLEAN, true);
        this.user.persistentData().setIfNotPresent(SOUNDS, PersistentDataTypes.BOOLEAN, true);
        this.user.persistentData().setIfNotPresent(LAST_DAILY_REWARD, PersistentDataTypes.LONG, 0L);
        this.user.persistentData().setIfNotPresent(REWARD_SLOTS_USED, INTEGERS, new HashSet<>());
        this.user.persistentData().setIfNotPresent(GADGET, TYPE_GADGET, Gadget.GRAPPLING_HOOK);
        this.user.persistentData().setIfNotPresent(SELECTED_SLOT, PersistentDataTypes.INTEGER, 0);
        long time2 = System.currentTimeMillis();
        this.openInventory = new InventoryPlayer();
        if (System.currentTimeMillis() - time1 > 1000) {
            Lobby
                    .getInstance()
                    .getLogger()
                    .info("Loading LobbyUser took very long: " + (System.currentTimeMillis() - time1) + " | " + (System.currentTimeMillis() - time2));
        }
    }

    public @NotNull Optional<@NotNull Player> player() {
        return Optional.ofNullable(asPlayer());
    }

    public @Nullable Player asPlayer() {
        return Bukkit.getPlayer(user.uniqueId());
    }

    public JaR getCurrentJaR() {
        return currentJaR;
    }

    public void startJaR() {
        stopJaR();
        this.currentJaR = Lobby.getInstance().getJaRManager().startJaR(this);
        player().ifPresent(p -> p.setAllowFlight(false));
        Lobby.getInstance().setItems(this);
    }

    public void stopJaR() {
        if (this.currentJaR != null) {
            this.currentJaR.stop();
            this.currentJaR = null;
            Lobby.getInstance().setItems(this);
            player().ifPresent(player -> {
                player.setAllowFlight(true);
                player.teleport(Lobby.getInstance().getDataManager().getJumpAndRunSpawn().clone().add(0, 0.1, 0));
            });

        }
    }

    public User user() {
        return this.user;
    }

    public IInventory getOpenInventory() {
        return this.openInventory;
    }

    public LobbyUser setOpenInventory(@NotNull IInventory openInventory) {
        if (this.openInventory != null && openInventory.getClass().equals(this.openInventory.getClass())) {
            this.disableAnimations(true);
        }

        Player p = asPlayer();
        if (p != null) {
            Runnable r = () -> {
                if (openInventory.getHandle() != null) {
                    openInventory.open(p);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 100, false, false), true);
                    LobbyUser.this.openInventory = openInventory;
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
        this.disableAnimations(false);
        return this;
    }

    public int getSelectedSlot() {
        return user.persistentData().get(SELECTED_SLOT, PersistentDataTypes.INTEGER);
    }

    public void setSelectedSlot(int slot) {
        user.persistentData().set(SELECTED_SLOT, PersistentDataTypes.INTEGER, slot);
    }

    public Set<Integer> getRewardSlotsUsed() {
        return user.persistentData().get(REWARD_SLOTS_USED, INTEGERS);
    }

    public void setRewardSlotsUsed(Set<Integer> slots) {
        user.persistentData().set(REWARD_SLOTS_USED, INTEGERS, slots);
    }

    public boolean disableAnimations() {
        return disableAnimations;
    }

    public void disableAnimations(boolean disableAnimations) {
        this.disableAnimations = disableAnimations;
    }

    public boolean isBuildMode() {
        return this.buildMode;
    }

    public void setBuildMode(boolean buildMode) {
        this.buildMode = buildMode;
    }

    public Location getLastPosition() {
        return user
                .persistentData()
                .get(POSITION, BukkitPersistentDataTypes.LOCATION, () -> Lobby.getInstance().getDataManager().getSpawn());
    }

    public void setLastPosition(Location position) {
        user.persistentData().set(POSITION, BukkitPersistentDataTypes.LOCATION, position);
    }

    public boolean isSounds() {
        if (disableSounds()) return false;
        return user.persistentData().get(SOUNDS, PersistentDataTypes.BOOLEAN);
    }

    public void setSounds(boolean sounds) {
        user.persistentData().set(SOUNDS, PersistentDataTypes.BOOLEAN, sounds);
    }

    public boolean disableSounds() {
        return disableSounds;
    }

    public void disableSounds(boolean disableSounds) {
        this.disableSounds = disableSounds;
    }

    public boolean isAnimations() {
        if (disableAnimations()) return false;
        return user.persistentData().get(ANIMATIONS, PersistentDataTypes.BOOLEAN);
    }

    public void setAnimations(boolean animations) {
        user.persistentData().set(ANIMATIONS, PersistentDataTypes.BOOLEAN, animations);
    }

    public long getLastDailyReward() {
        return user.persistentData().get(LAST_DAILY_REWARD, PersistentDataTypes.LONG);
    }

    public void setLastDailyReward(long lastDailyReward) {
        user.persistentData().set(LAST_DAILY_REWARD, PersistentDataTypes.LONG, lastDailyReward);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        if (!this.isSounds()) return;
        Player p = asPlayer();
        if (p != null) {
            this.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        if (!this.isSounds()) return;
        Player p = asPlayer();
        if (p != null) {
            p.playSound(loc, sound, volume, pitch);
        }
    }

    public void teleport(Location loc) {
        Player p = asPlayer();
        if (p != null) {
            this.playSound(Sound.NOTE_PLING, 10, 1);
            p.teleport(loc.clone().add(0, 0.1, 0));
            p.setGameMode(GameMode.SURVIVAL);
            p.setAllowFlight(true);
            new BukkitRunnable() {

                final double r = .8;
                double t = 0;
                double x, y, z;

                @Override public void run() {
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

    public Gadget getGadget() {
        return user.persistentData().get(GADGET, TYPE_GADGET);
    }

    public void setGadget(Gadget gadget) {
        Gadget current = getGadget();
        if (current == gadget) {
            return;
        }
        user.persistentData().set(GADGET, TYPE_GADGET, gadget);
        Lobby.getInstance().setItems(this);
    }

}
