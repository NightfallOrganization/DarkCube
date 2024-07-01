/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.module.modules;

import java.util.Iterator;
import java.util.List;

import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RandoShit implements Module, Listener {
    private final VanillaAddons addons;

    public RandoShit(VanillaAddons addons) {
        this.addons = addons;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, addons);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onItemBreak(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (this.isValidMaterial(item.getType())) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable damageable) {
                if (damageable.getDamage() + event.getDamage() >= item.getType().getMaxDurability()) {
                    event.setCancelled(true);
                    damageable.setDamage(item.getType().getMaxDurability() - 1);
                    item.setItemMeta(damageable);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                }
            }

        }
    }

    @EventHandler
    public void onRightClickStairs(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                if (clickedBlock.getRelative(BlockFace.UP).isPassable()) {
                    BlockData var5 = clickedBlock.getBlockData();
                    if (var5 instanceof Stairs stairs) {
                        Location location = new Location(clickedBlock.getWorld(), clickedBlock.getLocation().getX() + 0.5, clickedBlock.getLocation().getY() - 1.2, clickedBlock.getLocation().getZ() + 0.5, this.getYaw(stairs.getFacing().getOppositeFace()), 0.0F);
                        ArmorStand armorStand = player.getWorld().spawn(location, ArmorStand.class);
                        armorStand.setCustomName("SIT_" + player.getUniqueId());
                        armorStand.setCustomNameVisible(false);
                        armorStand.setGravity(false);
                        armorStand.setVisible(false);
                        armorStand.addPassenger(player);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        Entity var4 = event.getEntity();
        if (var4 instanceof Player player) {
            var4 = event.getDismounted();
            if (var4 instanceof ArmorStand stand) {
                var customName = stand.customName();
                if (customName == null) {
                    return;
                }

                if (PlainTextComponentSerializer.plainText().serialize(customName).equals("SIT_" + player.getUniqueId())) {
                    stand.remove();
                }

                player.teleport(player.getLocation().add(0.0, 1.0, 0.0));
            }
        }

    }

    private float getYaw(BlockFace face) {
        return switch (face) {
            case NORTH -> 180.0F;
            case WEST -> 90.0F;
            case EAST -> -90.0F;
            default -> 0.0F;
        };
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (this.hasBrokenPiece(player)) {
                player.setHealth(Math.max(0.0, player.getHealth() - event.getDamage()));
                event.setDamage(0.0);
            }
        }

    }

    private boolean hasBrokenPiece(Player player) {
        ItemStack[] contents = player.getInventory().getArmorContents();

        for (ItemStack stack : contents) {
            if (stack != null && this.isValidMaterial(stack.getType())) {
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof Damageable damageable) {
                    if (damageable.getDamage() + 1 >= stack.getType().getMaxDurability()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isValidMaterial(Material material) {
        List<String> mats = List.of("DIAMOND_", "NETHERITE_");
        Iterator<String> var3 = mats.iterator();

        String s;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            s = var3.next();
        } while (!material.name().startsWith(s));

        return true;
    }
}
