package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

public class MonsterLevelHandler implements Listener {

	private static final String WORLD_NAME = "farmworld";
	private static final double HEALTH_UPDATE_THRESHOLD = 10.0;
	private static final UUID SPEED_MODIFIER_UUID = UUID.randomUUID();

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
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
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player || event.getEntity() instanceof Player) {
			return; // Allow players to damage entities and vice versa
		}

		if (!(event.getEntity() instanceof Monster)) {
			return;
		}

		Monster monster = (Monster) event.getEntity();
		World world = monster.getWorld();
		if (!world.getName().equals(WORLD_NAME)) {
			return;
		}

		if (event.getDamager() instanceof LivingEntity && event.getEntity() instanceof LivingEntity) {
			event.setCancelled(true); // Prevent non-player entities from damaging each other
		}

		Bukkit.getScheduler().runTaskLater(Citybuild.getInstance(), () -> {
			updateMonsterName(monster);
		}, 1L);
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
		double x = location.getX();
		double z = location.getZ();
		int distance = (int) Math.sqrt(x * x + z * z);
		return Math.max(1, distance / 100);
	}

	private void applyLevelToMonster(Monster monster, int level) {
		monster.setMaxHealth(level * 20);
		monster.setHealth(level * 20);
		monster.setCustomName("§6Level§7:§e" + level + " §7- §c100%");
		monster.setCustomNameVisible(true);

		// Modifikator für Geschwindigkeitserhöhung setzen
		AttributeInstance attributeSpeed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		if (attributeSpeed != null) {
			attributeSpeed.setBaseValue(0.35); // Setze Geschwindigkeit auf den gewünschten Wert.
		}

		if (monster instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) monster;
			if (level >= 10) {
				equipRandomArmor(livingEntity, level);
			}
		}
	}

	private void equipRandomArmor(LivingEntity entity, int level) {
		// Die Methode zum Ausrüsten der Monster mit zufälliger Rüstung und Waffen
	}

	private void updateMonsterName(Monster monster) {
		double healthPercentage =
				(monster.getHealth() / monster.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) * 100;
		String levelString = monster.getCustomName().split(" ")[1];
		monster.setCustomName(
				"§6Level§7:§e" + levelString + " §7- §c" + String.format("%.2f", healthPercentage) + "%");
	}

	private void equipMonster(Monster monster, int level) {
		if (level < 10)
			return; // Only equip monsters of level 10 and above

		EntityEquipment equipment = monster.getEquipment();

		Material[] armors = level < 50
				? new Material[] {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}
				: level < 100
						? new Material[] {Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS,
						Material.GOLDEN_BOOTS}
						: level < 150
								? new Material[] {Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
								Material.IRON_BOOTS}
								: new Material[] {Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
										Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS};

		Material[] weapons = {Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.IRON_SWORD,
				Material.IRON_AXE, Material.GOLDEN_SWORD, Material.GOLDEN_AXE, Material.STONE_SWORD,
				Material.STONE_AXE, Material.WOODEN_SWORD, Material.WOODEN_AXE};

		Random random = new Random();

		// Pick a random armor
		Material randomArmorMaterial = armors[random.nextInt(armors.length)];

		// Check if selected materials are valid armor pieces
		if (randomArmorMaterial.toString().contains("HELMET")) {
			equipment.setHelmet(addProtection(new ItemStack(randomArmorMaterial, 1)));
		} else if (randomArmorMaterial.toString().contains("CHESTPLATE")) {
			equipment.setChestplate(addProtection(new ItemStack(randomArmorMaterial, 1)));
		} else if (randomArmorMaterial.toString().contains("LEGGINGS")) {
			equipment.setLeggings(addProtection(new ItemStack(randomArmorMaterial, 1)));
		} else if (randomArmorMaterial.toString().contains("BOOTS")) {
			equipment.setBoots(addProtection(new ItemStack(randomArmorMaterial, 1)));
		}

		// Equip the skeleton with a bow or melee weapon
		if (monster instanceof Skeleton) {
			if (random.nextInt(100) < 70) { // 70% chance to have a bow
				equipment.setItemInMainHand(new ItemStack(Material.BOW, 1));
			} else { // 30% chance to have a sword or axe
				Material randomWeaponMaterial = weapons[random.nextInt(weapons.length)];
				if (randomWeaponMaterial.toString().contains("SWORD") || randomWeaponMaterial.toString().contains("AXE")) {
					// Equip the weapon
					ItemStack weapon = new ItemStack(randomWeaponMaterial, 1);
					weapon.addEnchantment(Enchantment.DAMAGE_ALL, 1); // Adds a random damage enchantment to the weapon
					equipment.setItemInMainHand(weapon);
				}
			}
		} else { // Original code for non-skeleton monsters
			// Pick a random weapon
			Material randomWeaponMaterial = weapons[random.nextInt(weapons.length)];

			if (randomWeaponMaterial.toString().contains("SWORD") || randomWeaponMaterial.toString()
					.contains("AXE")) {
				// Equip the weapon
				ItemStack weapon = new ItemStack(randomWeaponMaterial, 1);
				weapon.addEnchantment(Enchantment.DAMAGE_ALL, 1); // Adds a random damage enchantment to the weapon
				equipment.setItemInMainHand(weapon);
			}
		}
	}

		private ItemStack addProtection(ItemStack itemStack) {
			return itemStack;
		}

}
