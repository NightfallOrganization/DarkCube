/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.util.CustomHealthManager;
import eu.darkcube.system.aetheria.util.DamageManager;
import eu.darkcube.system.aetheria.util.DefenseManager;
import eu.darkcube.system.aetheria.util.LevelXPManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttributeCommand implements CommandExecutor, Listener {
    private final Aetheria plugin;
    private NamespacedKey speedKey;
    private NamespacedKey strengthKey;
    private NamespacedKey hitSpeedKey;
    private NamespacedKey healthKey;
    private NamespacedKey aroundDamageKey;
    private NamespacedKey defenseKey;
    private CustomHealthManager healthManager;
    private DamageManager damageManager;

    public AttributeCommand(Aetheria plugin, CustomHealthManager healthManager) {
        this.plugin = plugin;
        this.healthManager = healthManager;
        this.damageManager = damageManager;
        this.speedKey = new NamespacedKey(plugin, "SpeedKey");
        this.strengthKey = new NamespacedKey(plugin, "StrengthKey");
        this.hitSpeedKey = new NamespacedKey(plugin, "HitSpeedKey");
        this.healthKey = new NamespacedKey(plugin, "HealthKey");
        this.aroundDamageKey = new NamespacedKey(plugin, "AroundDamageKey");
        this.defenseKey = new NamespacedKey(plugin, "DefenseKey");
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            Inventory chestInventory = Bukkit.createInventory(null, 45, "§f\udaff\udfefḋ");
            int[] customModelData = new int[]{7, 8, 9, 10, 11, 12};
            int[] slots = new int[]{12, 14, 20, 24, 30, 32};
            String[] names = new String[]{"§dDefense", "§dStrength", "§dHealth", "§dSpeed", "§dAround Damage", "§dHit Speed"};

            for (int i = 0; i < customModelData.length; ++i) {
                ItemStack item = new ItemStack(Material.FIREWORK_STAR);
                ItemMeta meta = item.getItemMeta();
                meta.setCustomModelData(customModelData[i]);
                meta.setDisplayName(names[i]);
                PersistentDataContainer dataContainer = player.getPersistentDataContainer();
                int level = 0;
                switch (names[i]) {
                    case "§dDefense":
                        level = dataContainer.getOrDefault(this.defenseKey, PersistentDataType.INTEGER, 0);
                        break;
                    case "§dStrength":
                        level = dataContainer.getOrDefault(this.strengthKey, PersistentDataType.INTEGER, 0);
                        break;
                    case "§dHealth":
                        level = dataContainer.getOrDefault(this.healthKey, PersistentDataType.INTEGER, 0);
                        break;
                    case "§dSpeed":
                        level = dataContainer.getOrDefault(this.speedKey, PersistentDataType.INTEGER, 0);
                        break;
                    case "§dAround Damage":
                        level = (int) Math.round(dataContainer.getOrDefault(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0));
                        break;
                    case "§dHit Speed":
                        level = dataContainer.getOrDefault(this.hitSpeedKey, PersistentDataType.INTEGER, 0);
                }

                List<String> lore = new ArrayList();

                if (names[i].equals("§dSpeed")) {
                    lore.add("§7Level: " + level + " §7/ 39");
                } else if (names[i].equals("§dHit Speed")) {
                    lore.add("§7Level: " + level + " §7/ 37");
                } else {
                    lore.add("§7Level: " + level);
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
                chestInventory.setItem(slots[i], item);
            }

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName("§d" + player.getName());
            playerHead.setItemMeta(skullMeta);
            chestInventory.setItem(22, playerHead);
            player.openInventory(chestInventory);
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }

    @EventHandler public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§f\udaff\udfefḋ")) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) {
                return;
            }

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String displayName = clickedItem.getItemMeta().getDisplayName();
            LevelXPManager levelManager = new LevelXPManager(this.plugin, plugin.getCorManager());
            int playerAP = levelManager.getAP(player);
            if (playerAP > 0) {
                PersistentDataContainer dataContainer = player.getPersistentDataContainer();
                switch (displayName) {

                    case "§dSpeed":
                        double currentSpeed = levelManager.getAttribute(player, "speed");
                        if (currentSpeed == 0) {
                            currentSpeed = 0.22f;
                        }
                        if (currentSpeed >= 0.98f) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                            player.sendMessage("§cDu hast bereits die maximale Geschwindigkeit erreicht!");
                            return;
                        }
                        currentSpeed += 0.02f;
                        levelManager.setAttribute(player, "speed", currentSpeed);
                        player.setWalkSpeed((float) currentSpeed);
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        PersistentDataContainer dataContainerSpeed = player.getPersistentDataContainer();
                        int currentClicksSpeed = dataContainerSpeed.getOrDefault(this.speedKey, PersistentDataType.INTEGER, 0);
                        dataContainerSpeed.set(this.speedKey, PersistentDataType.INTEGER, currentClicksSpeed + 1);
                        ItemMeta metaSpeed = clickedItem.getItemMeta();
                        List<String> loreSpeed = metaSpeed.getLore();
                        loreSpeed.set(0, "§7Level: " + (currentClicksSpeed + 1) + " §7/ 39");
                        metaSpeed.setLore(loreSpeed);
                        clickedItem.setItemMeta(metaSpeed);
                        break;

                    case "§dStrength":
                        new DamageManager().addDamage(player, 5);  // Benutzerdefinierter Schaden um 3 erhöhen
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        PersistentDataContainer dataContainerStrength = player.getPersistentDataContainer();
                        int currentClicksStrength = dataContainerStrength.getOrDefault(this.strengthKey, PersistentDataType.INTEGER, 0);
                        dataContainerStrength.set(this.strengthKey, PersistentDataType.INTEGER, currentClicksStrength + 1);
                        ItemMeta metaStrength = clickedItem.getItemMeta();
                        List<String> loreStrength = metaStrength.getLore();
                        loreStrength.set(0, "§7Level: " + (currentClicksStrength + 1));
                        metaStrength.setLore(loreStrength);
                        clickedItem.setItemMeta(metaStrength);
                        break;

                    case "§dHit Speed":
                        PersistentDataContainer dataContainerHitSpeed = player.getPersistentDataContainer();
                        int currentClicksHitSpeed = dataContainerHitSpeed.getOrDefault(this.hitSpeedKey, PersistentDataType.INTEGER, 0);
                        if (currentClicksHitSpeed >= 37) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                            player.sendMessage("§cDu hast bereits die maximale Angriffsgeschwindigkeit erreicht!");
                            return;
                        }
                        AttributeModifier hitSpeedModifier = new AttributeModifier(UUID.randomUUID(), "HitSpeedModifier", 0.5, Operation.ADD_NUMBER, EquipmentSlot.HAND);
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(hitSpeedModifier);
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        dataContainerHitSpeed.set(this.hitSpeedKey, PersistentDataType.INTEGER, currentClicksHitSpeed + 1);
                        ItemMeta metaHitSpeed = clickedItem.getItemMeta();
                        List<String> loreHitSpeed = metaHitSpeed.getLore();
                        loreHitSpeed.set(0, "§7Level: " + (currentClicksHitSpeed + 1) + " §7/ 37");
                        metaHitSpeed.setLore(loreHitSpeed);
                        clickedItem.setItemMeta(metaHitSpeed);
                        break;

                    case "§dHealth":
                        this.healthManager.addMaxHealth(player, 10);
                        this.healthManager.addRegen(player, 1);
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        PersistentDataContainer dataContainerHealth = player.getPersistentDataContainer();
                        int currentClicksHealth = dataContainerHealth.getOrDefault(this.healthKey, PersistentDataType.INTEGER, 0);
                        dataContainerHealth.set(this.healthKey, PersistentDataType.INTEGER, currentClicksHealth + 1);
                        ItemMeta metaHealth = clickedItem.getItemMeta();
                        List<String> loreHealth = metaHealth.getLore();
                        loreHealth.set(0, "§7Level: " + (currentClicksHealth + 1));
                        metaHealth.setLore(loreHealth);
                        clickedItem.setItemMeta(metaHealth);
                        break;

                    case "§dAround Damage":
                        PersistentDataContainer dataContainerAroundDamage = player.getPersistentDataContainer();
                        double currentAroundDamage;
                        if (dataContainerAroundDamage.has(this.aroundDamageKey, PersistentDataType.DOUBLE)) {
                            currentAroundDamage = dataContainerAroundDamage.get(this.aroundDamageKey, PersistentDataType.DOUBLE);
                        } else {
                            currentAroundDamage = 0.0;
                            dataContainerAroundDamage.set(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);
                        }

                        dataContainerAroundDamage.set(this.aroundDamageKey, PersistentDataType.DOUBLE, currentAroundDamage + 1.0);
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        ItemMeta metaAroundDamage = clickedItem.getItemMeta();
                        List<String> loreAroundDamage = metaAroundDamage.getLore();
                        loreAroundDamage.set(0, "§7Level: " + (int) (currentAroundDamage + 1.0));
                        metaAroundDamage.setLore(loreAroundDamage);
                        clickedItem.setItemMeta(metaAroundDamage);
                        break;

                    case "§dDefense":
                        DefenseManager defenseManager = new DefenseManager(this.plugin);
                        double currentDefense = defenseManager.getDefense(player);
                        defenseManager.addDefense(player, 1);
                        levelManager.addAP(player, -1);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        PersistentDataContainer dataContainerDefense = player.getPersistentDataContainer();
                        int currentClicksDefense = dataContainerDefense.getOrDefault(this.defenseKey, PersistentDataType.INTEGER, 0);
                        dataContainerDefense.set(this.defenseKey, PersistentDataType.INTEGER, currentClicksDefense + 1);
                        ItemMeta metaAroundDefense = clickedItem.getItemMeta();
                        List<String> loreAroundDefense = metaAroundDefense.getLore();
                        loreAroundDefense.set(0, "§7Level: " + (currentClicksDefense + 1));
                        metaAroundDefense.setLore(loreAroundDefense);
                        clickedItem.setItemMeta(metaAroundDefense);
                }

            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                player.sendMessage("§cDu hast nicht genug AP!");
            }
        }

    }
}
