package eu.darkcube.system.citybuild.commands;

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
import java.util.Random;
import org.bukkit.entity.Monster;

import java.util.Random;
import java.util.UUID;

public class MonsterLevelHandler implements Listener {

	private LevelXPManager levelXPManager;

	public MonsterLevelHandler(LevelXPManager levelXPManager) {
		this.levelXPManager = levelXPManager;
	}

	private static final String WORLD_NAME = "Beastrealm";
	private static final double HEALTH_UPDATE_THRESHOLD = 10.0;
	private static final UUID SPEED_MODIFIER_UUID = UUID.randomUUID();

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
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

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
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

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player killer = entity.getKiller();

		if (!(entity instanceof Monster)) {
			return;
		}

		World world = entity.getWorld();
		if (!world.getName().equals("Beastrealm")) {
			return;
		}

		if (killer == null) {
			return;
		}

		int level = calculateMonsterLevel(entity.getLocation());

		double xp = levelXPManager.getXPForLevel(level);
		double xpMultiplier = 0.005;  // Monster geben nur 50% der benötigten XP pro Level
		xp *= xpMultiplier;

		levelXPManager.addXP(killer, xp);
	}




	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
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


	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		if (!(event.getEntity() instanceof Monster)) {
			return;
		}

		Monster monster = (Monster) event.getEntity();
		World world = monster.getWorld();
		if (!world.getName().equals(WORLD_NAME)) {
			return;
		}

		// Verhindert jegliches Verbrennen des Monsters.
		event.setCancelled(true);
	}

	private int calculateMonsterLevel(Location location) {
		double x = Math.abs(location.getX());
		double z = Math.abs(location.getZ());
		int distance = (int) Math.max(x, z); // Verwenden Sie den größeren der beiden Werte
		return Math.max(1, distance / 100);
	}


	private void applyLevelToMonster(Monster monster, int level) {
		double maxHealth = Math.min(level * 20, 2048);
		monster.setMaxHealth(maxHealth);
		monster.setHealth(maxHealth);
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


	private void equipRandomArmor(LivingEntity entity, int level) {
		// Die Methode zum Ausrüsten der Monster mit zufälliger Rüstung und Waffen
	}

	private void updateMonsterName(Monster monster) {
		double healthPercentage = (monster.getHealth() / monster.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) * 100;
		String customName = monster.getCustomName();
		if (customName != null && !customName.isEmpty()) {
			int levelStartIndex = customName.indexOf("§e") + 2;  // +2 to skip "§e"
			int levelEndIndex = customName.indexOf(" ", levelStartIndex);
			String levelString = customName.substring(levelStartIndex, levelEndIndex);
			monster.setCustomName("§6Level§7:§e" + levelString + " §7- §c" + String.format("%.2f", healthPercentage) + "%");
		}
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
				equipArmorPiece(equipment, randomArmorMaterial, true, Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(5));
			}
		} else if (completeSet) {
			for (Material armorMaterial : armors) {
				equipArmorPiece(equipment, armorMaterial, true, Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(5));
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

		if(level >= 10) { // 100% Chance für eine Waffe ab Level 10
			if(monster instanceof Skeleton) {
				ItemStack bow = new ItemStack(Material.BOW, 1);
				bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				equipment.setItemInMainHand(bow);
			} else if(monster instanceof Zombie) {
				int chance = random.nextInt(100);
				if(chance < 20) { // 20% chance for an Axe
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
