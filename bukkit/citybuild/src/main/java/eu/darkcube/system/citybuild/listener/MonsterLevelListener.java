/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import eu.darkcube.system.citybuild.Citybuild;
import eu.darkcube.system.citybuild.util.CustomHealthManager;
import eu.darkcube.system.citybuild.util.DamageManager;
import eu.darkcube.system.citybuild.util.LevelXPManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import org.bukkit.entity.Monster;

import java.util.Random;

public class MonsterLevelListener implements Listener {

    private LevelXPManager levelXPManager;
    private CustomHealthManager healthManager;
    private final Map<LivingEntity, Map<Player, Double>> damageMap = new HashMap<>();
    private DamageManager damageManager = new DamageManager();

    public MonsterLevelListener(LevelXPManager levelXPManager, CustomHealthManager healthManager) {
        this.levelXPManager = levelXPManager;
        this.healthManager = healthManager;
    }

    private static final String WORLD_NAME = "Beastrealm";
    private static final double HEALTH_UPDATE_THRESHOLD = 10.0;
    private static final UUID SPEED_MODIFIER_UUID = UUID.randomUUID();

    @EventHandler public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        // Storniert das Spawnen, wenn die Kreatur friedlich ist
        if (entity instanceof Animals || entity instanceof Villager || entity instanceof Golem || entity instanceof Bat || entity instanceof Squid) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.isBaby()) {
                event.setCancelled(true);
                return;
            }
        }

        if (!(event.getEntity() instanceof Monster)) {
            return;
        }

        Monster monster = (Monster) event.getEntity();
        World world = monster.getWorld();
        if (!world.getName().equals(WORLD_NAME)) {
            return;
        }

        Location spawnLocation = monster.getLocation();
        int level = calculateMonsterLevel(spawnLocation);

        applyLevelToMonster(monster, level);
        equipMonster(monster, level);

    }

    @EventHandler public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }

        Monster monster = (Monster) event.getEntity();
        World world = monster.getWorld();
        if (!world.getName().equals(WORLD_NAME)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(Citybuild.getInstance(), () -> {
            updateMonsterName(monster);
        }, 1L);
    }

    @EventHandler public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Monster)) {
            return;
        }

        World world = entity.getWorld();
        if (!world.getName().equals("Beastrealm")) {
            return;
        }

        // Holt die Schadensmap für das aktuelle Monster
        Map<Player, Double> playerDamages = damageMap.get(entity);
        if (playerDamages != null) {
            // Ermittelt den Spieler, der dem Monster den meisten Schaden zugefügt hat
            Player topDamager = playerDamages.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

            if (topDamager != null) {
                int level = calculateMonsterLevel(entity.getLocation());
                double xp = levelXPManager.getXPForLevel(level);
                double xpMultiplier = 0.005;
                xp *= xpMultiplier;

                levelXPManager.addXP(topDamager, xp);

                // XP-Wert anzeigen
                SchadensAnzeigeListener schadensAnzeigeListener = new SchadensAnzeigeListener();
                schadensAnzeigeListener.zeigeXPWert(entity.getLocation(), xp);
            }

            damageMap.remove(entity);  // Entfernt das Monster aus der Schadensverfolgung, da es jetzt tot ist
        }
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            LivingEntity target = (LivingEntity) event.getEntity();

            // Erhalte den benutzerdefinierten Schadenswert des Spielers
            double playerDamage = damageManager.getDamage(player);
            if (playerDamage > 0) {
                event.setDamage(playerDamage);
            }

            damageMap.computeIfAbsent(target, k -> new HashMap<>()).merge(player, event.getFinalDamage(), Double::sum);
            int currentHealth = healthManager.getMonsterHealth(target);
            int newHealth = currentHealth - (int) event.getFinalDamage();

            if (newHealth <= 0) {
                target.setHealth(0);
            } else {
                healthManager.setMonsterHealth(target, newHealth);
                if (target instanceof Monster) {
                    updateMonsterName((Monster) target);
                }
                event.setDamage(0.1);  // Setzen Sie den Schaden temporär auf einen geringen Wert
            }
        }
    }


    @EventHandler public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Skeleton) {
                Skeleton skeleton = (Skeleton) arrow.getShooter();

                // Abrufen des Levels des Skeletts
                String customName = skeleton.getCustomName();
                int level = 1; // Standardwert, falls kein Level gefunden wird
                if (customName != null) {
                    int levelStartIndex = customName.indexOf("§e") + 2;  // +2 to skip "§e"
                    int levelEndIndex = customName.indexOf(" ", levelStartIndex);
                    String levelString = customName.substring(levelStartIndex, levelEndIndex);
                    try {
                        level = Integer.parseInt(levelString); // Erstes Gruppenmatch ist das Level
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                double damage = event.getDamage() + level; // oder andere Anpassungen
                event.setDamage(damage);
            }
        } else if (event.getDamager() instanceof Creeper) {
            Creeper creeper = (Creeper) event.getDamager();

            // Abrufen des Levels des Creepers
            String customName = creeper.getCustomName();
            int level = 1; // Standardwert, falls kein Level gefunden wird
            if (customName != null) {
                int levelStartIndex = customName.indexOf("§e") + 2;  // +2 to skip "§e"
                int levelEndIndex = customName.indexOf(" ", levelStartIndex);
                String levelString = customName.substring(levelStartIndex, levelEndIndex);
                try {
                    level = Integer.parseInt(levelString); // Erstes Gruppenmatch ist das Level
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            double damage = event.getDamage() + level; // oder andere Anpassungen
            event.setDamage(damage);
        }
    }

    @EventHandler public void onMonsterDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            int currentHealth = healthManager.getMonsterHealth(monster);
            int newHealth = currentHealth - (int) event.getFinalDamage();
            if (newHealth <= 0) {
                monster.setHealth(0);
            } else {
                healthManager.setMonsterHealth(monster, newHealth);
                updateMonsterName(monster);
                event.setDamage(0.1);  // Setzen Sie den Schaden temporär auf einen geringen Wert
            }
        }
    }

    @EventHandler public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

    private int calculateMonsterLevel(Location location) {
        double x = Math.abs(location.getX());
        double z = Math.abs(location.getZ());
        int distance = (int) Math.max(x, z); // Verwenden Sie den größeren der beiden Werte
        return Math.max(1, distance / 300);
    }

    private void applyLevelToMonster(Monster monster, int level) {
        double maxHealth = level * 20;
        healthManager.setMonsterHealth(monster, (int) maxHealth);
        healthManager.setMonsterMaxHealth(monster, (int) maxHealth);
        updateMonsterName(monster);
        monster.setCustomName("§6Level §e" + level + " §7- §c100%");
        monster.setCustomNameVisible(true);

        // Modifikator für Geschwindigkeitserhöhung setzen
        AttributeInstance attributeSpeed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attributeSpeed != null) {
            attributeSpeed.setBaseValue(0.35); // Setze Geschwindigkeit auf den gewünschten Wert.
        }

        // Set the attack damage based on level
        AttributeInstance attributeAttack = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attributeAttack != null) {
            double attackDamage = level; // You can adjust this to fit your game's balance
            attributeAttack.setBaseValue(attackDamage);
        }

        // Equip skeleton with bow if monster is a skeleton
        if (monster instanceof Skeleton) {
            Skeleton skeleton = (Skeleton) monster;
            ItemStack bow = new ItemStack(Material.BOW);
            modifyBowDamage(bow, level); // or whatever value you want
            skeleton.getEquipment().setItemInMainHand(bow);
        }

        if (monster instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) monster;
            if (level >= 10) {
                equipRandomArmor(livingEntity, level);
            }
        }
    }

    private void modifyBowDamage(ItemStack bow, int level) {
        ItemMeta meta = bow.getItemMeta();

        if (meta != null) {
            int enchantmentLevel = level / 20; // Anpassen nach Bedarf
            enchantmentLevel = Math.min(enchantmentLevel, 5); // Power-Enchantment kann maximal Level 5 sein
            meta.addEnchant(Enchantment.ARROW_DAMAGE, enchantmentLevel, true);
            bow.setItemMeta(meta);
        }
    }

    private void updateMonsterName(Monster monster) {
        double maxHealth = healthManager.getMonsterMaxHealth(monster);
        double currentHealth = healthManager.getMonsterHealth(monster);
        double healthPercentage = (currentHealth / maxHealth) * 100;

        String customName = monster.getCustomName();
        if (customName != null && !customName.isEmpty()) {
            int levelStartIndex = customName.indexOf("§e") + 2;  // +2 um "§e" zu überspringen
            int levelEndIndex = customName.indexOf(" ", levelStartIndex);
            String levelString = customName.substring(levelStartIndex, levelEndIndex);

            monster.setCustomName("§6Level §e" + levelString + " §7- §c" + String.format("%.0f", healthPercentage) + "%");
        } else {
            monster.setCustomName("§c" + String.format("%.0f", healthPercentage) + "%");
        }
        monster.setCustomNameVisible(true);
    }

    private void equipRandomArmor(LivingEntity entity, int level) {
        // Die Methode zum Ausrüsten der Monster mit zufälliger Rüstung und Waffen
    }

    public void equipMonster(Monster monster, int level) {
        Random random = new Random();
        EntityEquipment equipment = monster.getEquipment();
        Material[] armors = getArmorType(level);
        boolean shouldEnchant = level % 80 >= 40 && level % 80 < 120;
        boolean completeSet = level % 80 >= 120 && level % 80 < 160;
        boolean protectionOne = level % 80 >= 160;

        // Rüstung
        if (shouldEnchant) {
            int armorParts = level % 40 + 1;
            for (int i = 0; i < armorParts; i++) {
                Material randomArmorMaterial = armors[random.nextInt(armors.length)];
                equipArmorPiece(equipment, randomArmorMaterial, true, Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(4) + 1);
            }
        } else if (completeSet) {
            for (Material armorMaterial : armors) {
                equipArmorPiece(equipment, armorMaterial, true, Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(4) + 1);
            }
        } else if (protectionOne) {
            for (Material armorMaterial : armors) {
                equipArmorPiece(equipment, armorMaterial, true, Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            }
        } else {
            Material randomArmorMaterial = armors[random.nextInt(armors.length)];
            equipArmorPiece(equipment, randomArmorMaterial, false);
        }

        // Waffen
        Material[] swords = {Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD};
        Material[] axes = {Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.STONE_AXE, Material.WOODEN_AXE};

        if (level >= 10) { // 100% Chance für eine Waffe ab Level 10
            if (monster instanceof Skeleton) {
                ItemStack bow = new ItemStack(Material.BOW, 1);
                bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                equipment.setItemInMainHand(bow);
            } else if (monster instanceof Zombie) {
                int chance = random.nextInt(100);
                if (chance < 20) { // 20% chance for an Axe
                    Material randomAxeMaterial = axes[random.nextInt(axes.length)];
                    equipWeapon(equipment, randomAxeMaterial);
                } else { // 80% chance for a Sword
                    Material randomSwordMaterial = swords[random.nextInt(swords.length)];
                    equipWeapon(equipment, randomSwordMaterial);
                }
            } else {
                Material randomWeaponMaterial = swords[random.nextInt(swords.length)];
                equipWeapon(equipment, randomWeaponMaterial);
            }
        }
    }

    private void equipWeapon(EntityEquipment equipment, Material weaponMaterial) {
        Random random = new Random();

        if (weaponMaterial.toString().contains("SWORD") || weaponMaterial.toString().contains("AXE")) {
            ItemStack weapon = new ItemStack(weaponMaterial, 1);

            // 50% Chance auf Verzauberung
            if (random.nextFloat() < 0.5) {
                weapon.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            }

            equipment.setItemInMainHand(weapon);
        }
    }

    private Material[] getArmorType(int level) {
        if (level >= 840) {
            return new Material[]{Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS};
        } else if (level >= 680) {
            return new Material[]{Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS};
        } else if (level >= 520) {
            return new Material[]{Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS};
        } else if (level >= 360) {
            return new Material[]{Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS};
        } else if (level >= 200) {
            return new Material[]{Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS};
        } else {
            return new Material[]{Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};
        }
    }

    private void equipArmorPiece(EntityEquipment equipment, Material armorMaterial, boolean enchanted) {
        equipArmorPiece(equipment, armorMaterial, enchanted, Enchantment.PROTECTION_ENVIRONMENTAL, 1);
    }

    private void equipArmorPiece(EntityEquipment equipment, Material armorMaterial, boolean enchanted, Enchantment enchantment, int level) {
        level = Math.min(level, enchantment.getMaxLevel());
        level = Math.max(level, 1);

        ItemStack armorPiece = new ItemStack(armorMaterial);
        if (enchanted) {
            armorPiece.addEnchantment(enchantment, level);
        }

        switch (armorMaterial) {
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case DIAMOND_HELMET:
            case GOLDEN_HELMET:
            case NETHERITE_HELMET:
                equipment.setHelmet(armorPiece);
                break;
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
                equipment.setChestplate(armorPiece);
                break;
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case NETHERITE_LEGGINGS:
                equipment.setLeggings(armorPiece);
                break;
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
            case GOLDEN_BOOTS:
            case NETHERITE_BOOTS:
                equipment.setBoots(armorPiece);
                break;
        }
    }

    private ItemStack addProtection(ItemStack itemStack) {
        return itemStack;
    }

}
