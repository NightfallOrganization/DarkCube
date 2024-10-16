/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.module.modules.teleporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.inventory.InventoryRegistry;
import eu.darkcube.system.vanillaaddons.module.Module;
import eu.darkcube.system.vanillaaddons.module.modules.recipes.Recipe;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.Teleporter.TeleportAccess;
import eu.darkcube.system.vanillaaddons.util.BlockLocation;
import eu.darkcube.system.vanillaaddons.util.Item;
import eu.darkcube.system.vanillaaddons.util.Item.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleporterModule implements Module {
    private final VanillaAddons addons;
    private final TeleporterListener listener = new TeleporterListener();
    private final Recipe recipe = new Recipe(Item.TELEPORTER, Recipe
            .shaped("teleporter", 3, "bab", "ada", "bcb")
            .i('a', Material.BLAZE_ROD)
            .i('b', Material.OBSIDIAN)
            .i('c', Material.NETHERITE_INGOT)
            .i('d', Material.ENDER_EYE));
    private Map<World, Teleporters> teleporters = new HashMap<>();

    public TeleporterModule(VanillaAddons addons) {
        this.addons = addons;
    }

    public List<Teleporter> teleporters(World world) {
        return teleporters.computeIfAbsent(world, k -> new Teleporters()).teleporters;
    }

    public Map<World, Teleporters> teleporters() {
        return teleporters;
    }

    @Override public void onEnable() {
        Bukkit.getPluginManager().registerEvents(listener, addons);
        InventoryRegistry ir = addons.inventoryRegistry();
        ir.register(TeleporterInventory.TYPE, TeleporterInventory::new);
        ir.register(TeleportersInventory.TYPE, TeleportersInventory::new);
        ir.register(TeleporterRenameInventory.TYPE, TeleporterRenameInventory::new);
        ir.register(TeleporterTrustedListInventory.TYPE, TeleporterTrustedListInventory::new);
        ir.register(TeleporterTrustedListAddInventory.TYPE, TeleporterTrustedListAddInventory::new);
        for (World world : Bukkit.getWorlds()) {
            TeleporterListener.loadTeleporters(addons, world);
        }
        Recipe.registerRecipe(recipe);
    }

    @Override public void onDisable() {
        Recipe.unregisterRecipe(recipe);
        InventoryRegistry ir = addons.inventoryRegistry();
        ir.unregister(TeleporterInventory.TYPE);
        ir.unregister(TeleportersInventory.TYPE);
        ir.unregister(TeleporterRenameInventory.TYPE);
        ir.unregister(TeleporterTrustedListInventory.TYPE);
        ir.unregister(TeleporterTrustedListAddInventory.TYPE);
        HandlerList.unregisterAll(listener);
        for (World world : Bukkit.getWorlds()) {
            TeleporterListener.saveTeleporters(addons, world);
        }
    }

    public class TeleporterListener implements Listener {

        public static boolean remove(VanillaAddons addons, Block block) {
            BlockLocation loc = new BlockLocation(block.getWorld().getKey(), block.getX(), block.getY(), block.getZ());
            boolean changed = false;
            for (Teleporter teleporter : new ArrayList<>(addons
                    .moduleManager()
                    .module(TeleporterModule.class)
                    .teleporters(block.getWorld()))) {
                if (!teleporter.block().equals(loc)) {
                    continue;
                }
                addons.moduleManager().module(TeleporterModule.class).teleporters(block.getWorld()).remove(teleporter);
                Location drop = block.getLocation().add(0.5, 0.5, 0.5);
                block.getWorld().dropItemNaturally(drop, Item.TELEPORTER.item());
                block.setType(Material.AIR);
                changed = true;
            }
            return changed;
        }

        public static void loadTeleporters(VanillaAddons addons, World world) {
            PersistentDataContainer container = world.getPersistentDataContainer();
            if (container.has(Teleporter.teleporters)) {
                int dataVersion = 0;
                if (container.has(Teleporter.teleportersDataVersion)) {
                    dataVersion = container.get(Teleporter.teleportersDataVersion, PersistentDataType.INTEGER);
                }
                if (dataVersion == 0) {
                    dataVersion = 1;
                    container.set(Teleporter.teleportersDataVersion, PersistentDataType.INTEGER, dataVersion);
                    TeleportersOld teleporters = container.get(Teleporter.teleporters, OldTeleporter.TELEPORTERS);
                    Teleporters newTeleporters = new Teleporters();
                    teleporters.teleporters
                            .stream()
                            .map(old -> new Teleporter(new ItemStack(old.icon), old.name, old.block, old.access, old.owner))
                            .forEach(newTeleporters.teleporters::add);
                    container.set(Teleporter.teleporters, Teleporter.TELEPORTERS, newTeleporters);
                }

                if (dataVersion == 1) {
                    addons
                            .moduleManager()
                            .module(TeleporterModule.class)
                            .teleporters()
                            .put(world, container.get(Teleporter.teleporters, Teleporter.TELEPORTERS));
                    for (Teleporter teleporter : addons
                            .moduleManager()
                            .module(TeleporterModule.class)
                            .teleporters()
                            .get(world).teleporters) {
                        teleporter.spawn();
                    }
                }
            }
        }

        public static void saveTeleporters(VanillaAddons addons, World world) {
            PersistentDataContainer container = world.getPersistentDataContainer();
            Teleporters teleporters = addons.moduleManager().module(TeleporterModule.class).teleporters().get(world);
            if (teleporters != null) {
                container.set(Teleporter.teleporters, Teleporter.TELEPORTERS, teleporters);
            }
        }

        @EventHandler public void handle(PlayerToggleSneakEvent event) {
            if (event.isSneaking()) {
                for (Teleporter teleporter : teleporters(event.getPlayer().getWorld())) {
                    if (this.getPlayers(teleporter.block()).contains(event.getPlayer())) {
                        AUser user = AUser.user(UserAPI.instance().user(event.getPlayer().getUniqueId()));
                        user.openInventory(TeleportersInventory.TYPE, teleporter);
                        break;
                    }
                }
            }
        }

        @EventHandler public void handle(PlayerInteractEvent event) {
            if (!event.hasBlock() || event.getClickedBlock() == null) return;
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                BlockLocation o = new BlockLocation(block.getWorld().getKey(), block.getX(), block.getY(), block.getZ());
                Teleporter teleporter = teleporters(block.getWorld()).stream().filter(t -> t.block().equals(o)).findFirst().orElse(null);
                if (teleporter == null) return;
                new BukkitRunnable() {
                    @Override public void run() {
                        AUser
                                .user(UserAPI.instance().user(event.getPlayer().getUniqueId()))
                                .openInventory(TeleporterInventory.TYPE, teleporter);
                    }
                }.runTask(VanillaAddons.instance());
                event.setCancelled(true);
            }
        }

        @EventHandler public void handle(InventoryClickEvent event) {
            AUser user = AUser.user(UserAPI.instance().user(event.getWhoClicked().getUniqueId()));

            if (TeleportersInventory.TYPE.equals(user.openInventory())) {
                if (event.getCurrentItem() == null) return;
                if (!event.getCurrentItem().hasItemMeta()) return;
                if (event.getClickedInventory() == null) return;
                ItemBuilder item = ItemBuilder.item(event.getCurrentItem());
                event.setCancelled(true);
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta.getPersistentDataContainer().has(new NamespacedKey("vanillaaddons", "teleporter"))) {
                    Teleporter teleporter = meta
                            .getPersistentDataContainer()
                            .get(new NamespacedKey("vanillaaddons", "teleporter"), Teleporter.TELEPORTER);
                    Player player = Bukkit.getPlayer(user.user().uniqueId());
                    if (player != null && teleporter != null) {
                        player.closeInventory(Reason.TELEPORT);
                        player.teleportAsync(teleporter
                                .block()
                                .block()
                                .getLocation()
                                .add(0.5, 1, 0.5)
                                .setDirection(player.getLocation().getDirection()));
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) public void handle(BlockDestroyEvent event) {
            Block block = event.getBlock();
            if (remove(addons, block)) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) public void handle(BlockBreakEvent event) {
            Block block = event.getBlock();
            if (remove(addons, block)) {
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) public void handle(BlockPlaceEvent event) {
            if (!event.canBuild()) return;
            Player p = event.getPlayer();
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HAND, EquipmentSlot.OFF_HAND}) {
                ItemStack item = p.getInventory().getItem(slot);
                if (!item.hasItemMeta()) continue;
                ItemBuilder builder = ItemBuilder.item(item);
                if (!builder.persistentDataStorage().has(Data.TYPE_KEY)) continue;
                if (builder.persistentDataStorage().get(Data.TYPE_KEY, Data.TYPE) != Item.TELEPORTER) continue;
                Block block = event.getBlockPlaced();
                Teleporter teleporter = new Teleporter(new ItemStack(Material.ENDER_EYE), "Teleporter", new BlockLocation(block
                        .getWorld()
                        .getKey(), block.getX(), block.getY(), block.getZ()), TeleportAccess.PUBLIC, p.getUniqueId());
                teleporters(block.getWorld()).add(teleporter);
                new BukkitRunnable() {
                    @Override public void run() {
                        teleporter.spawn();
                    }
                }.runTask(addons);
                event.setCancelled(true);
                p.getInventory().setItem(slot, item.subtract());
                return;
            }
        }

        private Collection<Player> getPlayers(BlockLocation block) {
            return block.block().getLocation().add(0.5, 1.0, 0.5).getNearbyPlayers(1.2, 0.2, (p) -> {
                Material mat = p.getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType();
                return mat == Material.RESPAWN_ANCHOR || mat == Material.OBSIDIAN || mat == Material.CRYING_OBSIDIAN;
            });
        }

        @EventHandler public void handle(WorldLoadEvent event) {
            loadTeleporters(addons, event.getWorld());
        }

        @EventHandler public void handle(WorldUnloadEvent event) {
            saveTeleporters(addons, event.getWorld());
        }

        @EventHandler public void handle(WorldSaveEvent event) {
            saveTeleporters(addons, event.getWorld());
        }
    }

}
