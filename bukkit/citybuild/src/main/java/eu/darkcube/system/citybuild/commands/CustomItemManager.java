package eu.darkcube.system.citybuild.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomItemManager {
	private final JavaPlugin plugin;

	private final List<NamespacedKey>recipes = new ArrayList<>();

	public CustomItemManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	public void registerItems() {

		// Add the recipe for the ender bag
		ShapedRecipe enderBagRecipe = new ShapedRecipe(new NamespacedKey(plugin, "ender_bag"), getEnderBag());
		enderBagRecipe.shape("ABA", "CDC", "ABA");
		enderBagRecipe.setIngredient('A', Material.OBSIDIAN);
		enderBagRecipe.setIngredient('B', Material.DIAMOND);
		enderBagRecipe.setIngredient('C', Material.LEATHER);
		enderBagRecipe.setIngredient('D', Material.ENDER_CHEST);
		addRecipe(enderBagRecipe);

	}

	private void addRecipe(ShapedRecipe recipe){
		plugin.getServer().addRecipe(recipe);
		recipes.add(recipe.getKey());
	}

	public void unloadRecipes(){
		for (NamespacedKey recipe : recipes) {
			plugin.getServer().removeRecipe(recipe);
		}
	}

	public static ItemStack getOrdinaryBag() {
		ItemStack ordinaryBag = new ItemStack(Material.FIREWORK_STAR, 1);
		ItemMeta meta = ordinaryBag.getItemMeta();
		if (meta != null) {
			meta.setCustomModelData(1);  // Eine andere CustomModelData als die anderen Items
			meta.setDisplayName("§6Ordinary Bag");
			ordinaryBag.setItemMeta(meta);
		}
		return ordinaryBag;
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
					new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 2000.0,
							AttributeModifier.Operation.ADD_NUMBER));
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			swiftSword.setItemMeta(meta);
		}
		return swiftSword;
	}
}
