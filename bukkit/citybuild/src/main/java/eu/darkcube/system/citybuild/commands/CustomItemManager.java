package eu.darkcube.system.citybuild.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CustomItemManager {
	private final JavaPlugin plugin;

	public CustomItemManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void registerItems() {
		// Add the recipe for the custom firework star
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "custom_firework_star"), getCustomFireworkStar());
		recipe.shape("ABA", "ACA", "AAA");
		recipe.setIngredient('A', Material.LEATHER);
		recipe.setIngredient('B', Material.GOLD_BLOCK);
		recipe.setIngredient('C', Material.CHEST);
		plugin.getServer().addRecipe(recipe);

		// Add the recipe for the ender bag
		ShapedRecipe enderBagRecipe = new ShapedRecipe(new NamespacedKey(plugin, "ender_bag"), getEnderBag());
		enderBagRecipe.shape("ABA", "CDC", "ABA");
		enderBagRecipe.setIngredient('A', Material.OBSIDIAN);
		enderBagRecipe.setIngredient('B', Material.DIAMOND);
		enderBagRecipe.setIngredient('C', Material.LEATHER);
		enderBagRecipe.setIngredient('D', Material.ENDER_CHEST);
		plugin.getServer().addRecipe(enderBagRecipe);

		// Add the recipe for the "Ring of Healing"
		ShapedRecipe ringOfHealingRecipe = new ShapedRecipe(new NamespacedKey(plugin, "ring_of_healing"), getRingOfHealing());
		ringOfHealingRecipe.shape("ABA", "ACA", "AAA");
		ringOfHealingRecipe.setIngredient('A', Material.GOLD_BLOCK);
		ringOfHealingRecipe.setIngredient('B', Material.NETHERITE_INGOT);
		ringOfHealingRecipe.setIngredient('C', Material.GLISTERING_MELON_SLICE);
		plugin.getServer().addRecipe(ringOfHealingRecipe);

		// Add the recipe for the "Ring of Speed"
		ShapedRecipe ringOfSpeedRecipe = new ShapedRecipe(new NamespacedKey(plugin, "ring_of_speed"), getRingOfSpeed());
		ringOfSpeedRecipe.shape("ABA", "ACA", "AAA");
		ringOfSpeedRecipe.setIngredient('A', Material.IRON_BLOCK);
		ringOfSpeedRecipe.setIngredient('B', Material.NETHERITE_INGOT);
		ringOfSpeedRecipe.setIngredient('C', Material.FEATHER);
		plugin.getServer().addRecipe(ringOfSpeedRecipe);

		// Add the recipe for the "Swift Sword"
		ShapedRecipe swiftSwordRecipe = new ShapedRecipe(new NamespacedKey(plugin, "swift_sword"), getSwiftSword());
		swiftSwordRecipe.shape("ABA", "DCD", "ABA");
		swiftSwordRecipe.setIngredient('A', Material.OBSIDIAN);
		swiftSwordRecipe.setIngredient('B', Material.AMETHYST_CLUSTER);
		swiftSwordRecipe.setIngredient('C', Material.NETHERITE_SWORD);
		swiftSwordRecipe.setIngredient('D', Material.ENCHANTED_GOLDEN_APPLE);
		plugin.getServer().addRecipe(swiftSwordRecipe);
	}

	public static ItemStack getCustomFireworkStar() {
		ItemStack customFireworkStar = new ItemStack(Material.FIREWORK_STAR, 1);
		ItemMeta meta = customFireworkStar.getItemMeta();
		if (meta != null) {
			meta.setCustomModelData(1);
			meta.setDisplayName("§aOrdinary Bag");
			customFireworkStar.setItemMeta(meta);
		}
		return customFireworkStar;
	}

	public static ItemStack getEnderBag() {
		ItemStack enderBag = new ItemStack(Material.FIREWORK_STAR, 1);
		ItemMeta meta = enderBag.getItemMeta();
		if (meta != null) {
			meta.setCustomModelData(4);
			meta.setDisplayName("§3Ender Bag");
			enderBag.setItemMeta(meta);
		}
		return enderBag;
	}

	public static ItemStack getRingOfHealing() {
		ItemStack ringOfHealing = new ItemStack(Material.FIREWORK_STAR, 1);
		ItemMeta meta = ringOfHealing.getItemMeta();
		if (meta != null) {
			meta.setCustomModelData(5);
			meta.setDisplayName("§cRing of Healing");
			ringOfHealing.setItemMeta(meta);

		}
		return ringOfHealing;
	}

	public static ItemStack getRingOfSpeed() {
		ItemStack ringOfSpeed = new ItemStack(Material.FIREWORK_STAR, 1);
		ItemMeta meta = ringOfSpeed.getItemMeta();
		if (meta != null) {
			meta.setCustomModelData(6);
			meta.setDisplayName("§bRing of Speed");
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
					new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", 1,
							AttributeModifier.Operation.MULTIPLY_SCALAR_1));
			ringOfSpeed.setItemMeta(meta);
		}
		return ringOfSpeed;
	}

	public static ItemStack getSwiftSword() {
		ItemStack swiftSword = new ItemStack(Material.NETHERITE_SWORD, 1);
		ItemMeta meta = swiftSword.getItemMeta();
		if (meta != null) {
			meta.setCustomModelData(2);
			meta.setDisplayName("§dSwift Sword");
			meta.setUnbreakable(true);
			meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
					new AttributeModifier("generic.attackspeed", 50.0,
							AttributeModifier.Operation.MULTIPLY_SCALAR_1));
			meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
					new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 2.0,
							AttributeModifier.Operation.ADD_NUMBER));
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			swiftSword.setItemMeta(meta);
		}
		return swiftSword;
	}
}
