/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;
import java.util.UUID;

public class MonsterStatsManager implements Listener {

    private static final String WORLD_NAME = "Beastrealm";
    private static final double HEALTH_UPDATE_THRESHOLD = 10.0;
    private static final UUID SPEED_MODIFIER_UUID = UUID.randomUUID();

    private final NamespacedKey levelKey;
    private CustomHealthManager healthManager;

    public MonsterStatsManager(Aetheria aetheria, CustomHealthManager healthManager) {
        this.healthManager = healthManager;
        this.levelKey = new NamespacedKey(aetheria, "level");
    }

    /**
     * Gets the level of the entity
     *
     * @param entity the entity to get the level from
     * @return the level of the entity, -1 if the entity has no level
     */
    public int level(Entity entity) {
        return entity.getPersistentDataContainer().getOrDefault(levelKey, PersistentDataType.INTEGER, -1);
    }

    /**
     * Sets the level for an entity
     */
    public void level(Entity entity, int level) {
        entity.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
        double maxHealth = level * 20;
        healthManager.setHealth(entity, (int) maxHealth * 2);
        healthManager.setMaxHealth(entity, (int) maxHealth * 2);
        updateMonsterName(entity);
        entity.setCustomName("§6Level §e" + level + " §7- §c100%");
        entity.setCustomNameVisible(true);

        // Equip skeleton with bow if monster is a skeleton
        if (entity instanceof Skeleton skeleton) {
            ItemStack bow = new ItemStack(Material.BOW);
            skeleton.getEquipment().setItemInMainHand(bow);
        }

        if (entity instanceof LivingEntity livingEntity) {
            if (level >= 10) {
                equipRandomArmor(livingEntity, level);
            }

            // Modifikator für Geschwindigkeitserhöhung setzen
            AttributeInstance attributeSpeed = livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (attributeSpeed != null) {
                attributeSpeed.setBaseValue(0.35); // Setze Geschwindigkeit auf den gewünschten Wert.
            }

            // Set the attack damage based on level
            AttributeInstance attributeAttack = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (attributeAttack != null) {
                attributeAttack.setBaseValue(level);
            }
        }
    }

    @EventHandler public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Zombie zombie) {
            if (!zombie.isAdult()) {
                event.setCancelled(true);
                return;
            }
        }

        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        World world = monster.getWorld();
        if (!world.getName().equals(WORLD_NAME)) {
            return;
        }

        Location spawnLocation = monster.getLocation();
        int level = calculateMonsterLevel(spawnLocation);

        level(monster, level);
        equipMonster(monster, level);

    }

    private int calculateMonsterLevel(Location location) {
        double x = Math.abs(location.getX());
        double z = Math.abs(location.getZ());
        int distance = (int) Math.max(x, z); // Verwenden Sie den größeren der beiden Werte
        return Math.max(1, distance / 300);
    }

    public void updateMonsterName(Entity monster) {
        double maxHealth = healthManager.getMaxHealth(monster);
        double currentHealth = healthManager.getHealth(monster);
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
