/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.deathchests;

import eu.darkcube.system.bukkit.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.bukkit.inventoryapi.item.meta.SkullBuilderMeta;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class DeathChestsModule implements Module, Listener {
    private static final List<Vector> RELATIVE_POSTIONS = Arrays.asList(new Vector(1, 0, 0), new Vector(1, 0, 1), new Vector(0, 0, 1), new Vector(-1, 0, 0), new Vector(-1, 0, -1), new Vector(0, 0, -1), new Vector(-1, 0, 1), new Vector(1, 0, -1), new Vector(1, 1, 0), new Vector(1, 1, 1), new Vector(0, 1, 1), new Vector(-1, 1, 0), new Vector(-1, 1, -1), new Vector(0, 1, -1), new Vector(-1, 1, 1), new Vector(1, 1, -1), new Vector(0, 1, 0), new Vector(1, -1, 0), new Vector(1, -1, 1), new Vector(0, -1, 1), new Vector(-1, -1, 0), new Vector(-1, -1, -1), new Vector(0, -1, -1), new Vector(-1, -1, 1), new Vector(1, -1, -1), new Vector(0, -1, 0));
    private final VanillaAddons addons;
    private Random rand = new Random();

    public DeathChestsModule(VanillaAddons addons) {
        this.addons = addons;
    }

    @EventHandler public void onDeath(PlayerDeathEvent e) {
        if (isPlayerKill(e.getPlayer())) {
            e.setKeepInventory(true);
            e.setKeepLevel(true);
            e.getDrops().clear();
            e.setDroppedExp(0);
            if (Math.random() < .5) e.getDrops().add(getPlayerHead(e.getEntity().getName(), e.getEntity().getUniqueId()));
            return;
        }

        if (Math.random() < .5) e.getDrops().add(getPlayerHead(e.getEntity().getName(), e.getEntity().getUniqueId()));

        ItemStack expb = new ItemStack(Material.EXPERIENCE_BOTTLE, (int) ((e.getEntity().getTotalExperience() / 7.0) * .66));
        if (expb.getAmount() > 64) expb.setAmount(64);
        e.setDroppedExp(expb.getAmount() * 7);

        ItemStack i = containsChest(e.getDrops());
        Location loc = getSafeLocation(e.getEntity().getLocation());
        if (i == null || loc == null) return;
        if (i.getAmount() > 1) i.setAmount(i.getAmount() - 1);
        else e.getDrops().remove(i);

        e.getDrops().add(expb);
        e.setDroppedExp(0);

        Block b = loc.getBlock();
        b.setType(i.getType());
        Chest c = (Chest) b.getState();
        getItems(e.getDrops()).forEach(it -> c.getInventory().addItem(it));

        if (!Arrays.asList(c.getInventory().getContents()).contains(expb)) {
            e.getDrops().remove(expb);
            e.setDroppedExp(expb.getAmount() * 7);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (e.getCause().equals(DamageCause.VOID)) return;
        if (e.getFinalDamage() < p.getHealth() + p.getAbsorptionAmount()) return;
        PlayerInventory inv = p.getInventory();
        if (inv.getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) || inv
                .getItemInOffHand()
                .getType()
                .equals(Material.TOTEM_OF_UNDYING)) return;

        int slot = containsTotem(inv);
        if (slot == -1) return;
        ItemStack old = inv.getItemInOffHand();
        inv.setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
        inv.setItem(slot, null);

        new BukkitRunnable() {
            @Override public void run() {
                inv.setItemInOffHand(old);
            }
        }.runTaskLater(addons, 1);
    }

    private ItemStack containsChest(List<ItemStack> items) {
        for (ItemStack i : items) {
            if (i.getType().equals(Material.CHEST)) return i;
        }
        for (ItemStack i : items) {
            if (i.getType().equals(Material.TRAPPED_CHEST)) return i;
        }
        return null;
    }

    private List<ItemStack> getItems(List<ItemStack> items) {
        List<ItemStack> result = new ArrayList<>();
        int i = 0;
        while (i < 27 && !items.isEmpty()) {
            int j = rand.nextInt(items.size());
            result.add(items.get(j));
            items.remove(j);
            i++;
        }
        return result;
    }

    private ItemStack getPlayerHead(String name, UUID uuid) {
        ItemBuilder item = ItemBuilder
                .item(Material.PLAYER_HEAD)
                .meta(new SkullBuilderMeta(new SkullBuilderMeta.UserProfile(name, uuid)))
                .displayname(Component.text(name, NamedTextColor.YELLOW));
        return item.build();
    }

    private Location getSafeLocation(Location start) {
        if (start.getBlock().getType().equals(Material.AIR)) return start;
        for (Vector v : RELATIVE_POSTIONS) {
            if (start.clone().add(v).getBlock().getType().equals(Material.AIR)) return start.clone().add(v);
        }
        return null;
    }

    private int containsTotem(PlayerInventory inv) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType().equals(Material.TOTEM_OF_UNDYING)) return i;
        }
        return -1;
    }

    private boolean isPlayerKill(Player p) {
        if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent event) {
            Entity damager = event.getDamager();
            if (damager instanceof Projectile pr && pr.getShooter() instanceof Player s) {
                damager = s;
            }
            return damager instanceof Player;
        }
        return false;
    }

    @Override public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, addons);
    }

    @Override public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
