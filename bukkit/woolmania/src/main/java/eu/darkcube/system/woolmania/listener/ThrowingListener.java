/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.enums.Sounds.NO;
import static eu.darkcube.system.woolmania.enums.Sounds.THROW;
import static eu.darkcube.system.woolmania.enums.TeleportLocations.SPAWN;
import static eu.darkcube.system.woolmania.util.message.Message.*;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Hall;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.items.WoolItem;
import eu.darkcube.system.woolmania.items.gadgets.WoolGrenadeItem;
import eu.darkcube.system.woolmania.registry.WoolRegistry;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ThrowingListener implements Listener {

    private static final NamespacedKey key = new NamespacedKey(WoolMania.getInstance(), "woolgrenade");

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());
        var item = event.getItem();
        if (item == null) return;
        CustomItem customItem = new CustomItem(ItemBuilder.item(item));
        String itemId = customItem.getItemID();
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        int itemLevel = customItem.getLevel();
        int itemTier = customItem.getTierID();
        int playerLevel = woolManiaPlayer.getLevel();
        Hall hall = woolManiaPlayer.getHall();
        String name = hall.name();
        int hallValue = hall.getHallValue().getValue();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (event.getHand() == EquipmentSlot.OFF_HAND && WoolGrenadeItem.ITEM_ID.equals(itemId)) {
            NO.playSound(player);
            user.sendMessage(NOT_OFFHAND);
            event.setCancelled(true);
            return;
        }

        if (hallValue > itemTier) {
            user.sendMessage(TIER_TO_LOW);
            NO.playSound(player);
            event.setCancelled(true);
            return;
        }

        if (itemLevel > playerLevel) {
            user.sendMessage(LEVEL_TO_LOW);
            NO.playSound(player);
            event.setCancelled(true);
            return;
        }

        if (player.getWorld().equals(SPAWN.getWorld()) && WoolGrenadeItem.ITEM_ID.equals(itemId)) {
            NO.playSound(player);
            user.sendMessage(NOT_IN_HALL);
            event.setCancelled(true);
            return;
        }

        if (!WoolGrenadeItem.ITEM_ID.equals(itemId)) return;
        event.setCancelled(true);
        var grenade = event.getPlayer().launchProjectile(Snowball.class);
        grenade.getPersistentDataContainer().set(key, PersistentDataType.STRING, name);

        ItemStack itemToRemove = item.clone();
        itemToRemove.setAmount(1);
        player.getInventory().removeItem(itemToRemove);
        THROW.playSound(player);
    }

    @EventHandler
    public void handle(ProjectileHitEvent event) {
        Entity entity = event.getEntity();

        if (entity.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            Snowball snowball = (Snowball) event.getEntity();
            if (snowball.getShooter() instanceof Player player) {
                User user = UserAPI.instance().user(player.getUniqueId());
                Hall hall = Hall.valueOf(entity.getPersistentDataContainer().get(key, PersistentDataType.STRING));

                Location location = entity.getLocation();
                WoolRegistry registry = WoolMania.getInstance().getWoolRegistry();
                int radius = 5;
                int radiusSquared = radius * radius;
                int totalCount = 0;
                World world = event.getEntity().getWorld();
                Map<WoolRegistry.Entry, Integer> drops = new HashMap<>();

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            Location loc = location.clone().add(x, y, z);
                            var block = loc.getBlock();
                            var type = block.getType();
                            if (loc.distanceSquared(location) <= radiusSquared && type != Material.LIGHT && hall.getPool().isWithinBounds(loc)) {
                                if (registry.contains(type)) {
                                    WoolRegistry.Entry entry = registry.get(type);
                                    totalCount++;

                                    drops.compute(entry, (_, oldCount) -> {
                                        if (oldCount == null) return 1;
                                        return oldCount + 1;
                                    });

                                    Light l = (Light) Material.LIGHT.createBlockData();
                                    l.setLevel(12);
                                    block.setBlockData(l, false);
                                }
                            }
                        }
                    }
                }
                if (totalCount > 0) {
                    world.spawnParticle(Particle.CLOUD, location, totalCount, radius / 2.5F, radius / 2.5F, radius / 2.5F, 0);
                }
                for (var entry : drops.entrySet()) {
                    var e = entry.getKey();
                    var count = entry.getValue();
                    var woolItem = new WoolItem(user, e.material(), e.tier(), e.name());
                    var itemStack = woolItem.getItemStack();

                    int remaining = count;
                    while (remaining > 0) {
                        int subtract = Math.min(remaining, 64);
                        remaining -= subtract;
                        itemStack.setAmount(subtract);

                        dropBlocks(player, location, itemStack.clone());
                    }
                }

                WoolMania.getStaticPlayer(player).getFarmingSound().playSound(player);
            }
        }
    }

    public void dropBlocks(Player player, Location dropLocation, ItemStack customItem) {
        var failedToAdd = player.getInventory().addItem(customItem);
        if (!failedToAdd.isEmpty()) {
            for (var value : failedToAdd.values()) {
                player.getWorld().dropItemNaturally(dropLocation, value);
            }
        }
    }
}
