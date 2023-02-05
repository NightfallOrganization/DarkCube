/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.util;

import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.util.Recipe.RecipeTemplate.Type.Shaped;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.*;

public enum Recipe {

	TELEPORTER(Item.TELEPORTER,
			shaped("teleporter", "bab", "ada", "bcb").i('a', BLAZE_ROD).i('b', OBSIDIAN)
					.i('c', NETHERITE_INGOT).i('d', ENDER_EYE)),
	;

	private final Item item;
	private final RecipeTemplate[] recipes;

	Recipe(Item item, RecipeTemplate... recipes) {
		this.item = item;
		this.recipes = recipes;
	}

	public RecipeTemplate[] recipes() {
		return recipes;
	}

	public Item item() {
		return item;
	}

	public static void giveAll(VanillaAddons addons, Player player) {
		for (Recipe recipe : values()) {
			for (RecipeTemplate template : recipe.recipes) {
				player.discoverRecipe(new NamespacedKey(addons, template.key));
			}
		}
	}

	public static void unregisterAll(VanillaAddons addons) {
		for (Recipe recipe : values()) {
			for (RecipeTemplate template : recipe.recipes) {
				Bukkit.removeRecipe(new NamespacedKey(addons, template.key));
			}
		}
	}

	public static void registerAll(VanillaAddons addons) {
		for (Recipe recipe : values()) {
			for (RecipeTemplate template : recipe.recipes) {
				org.bukkit.inventory.Recipe br = null;
				if (template.type instanceof Shaped shaped) {
					ShapedRecipe s = new ShapedRecipe(new NamespacedKey(addons, template.key()),
							recipe.item().item()).shape(shaped.shape);
					template.materialIngredients.forEach(s::setIngredient);
					s.setCategory(template.category);
					br = s;
				}
				if (br != null) {
					Bukkit.addRecipe(br);
				}
			}
		}
	}

	public static RecipeTemplate shaped(String key, String... shape) {
		return new RecipeTemplate(key, new RecipeTemplate.Type.Shaped().shape(shape));
	}

	public static class RecipeTemplate {
		private final String key;
		private final CraftingBookCategory category;
		private final Type type;
		private final Map<Character, Material> materialIngredients = new HashMap<>();

		public RecipeTemplate(String key, CraftingBookCategory category, Type type) {
			this.key = key;
			this.category = category;
			this.type = type;
		}

		public RecipeTemplate(String key, Type type) {
			this(key, CraftingBookCategory.MISC, type);
		}

		public RecipeTemplate i(char c, Material m) {
			materialIngredients.put(c, m);
			return this;
		}

		public CraftingBookCategory category() {
			return category;
		}

		public String key() {
			return key;
		}

		public Type type() {
			return type;
		}

		public interface Type {
			class Shaped implements Type {
				private final String[] shape = new String[3];

				public String[] shape() {
					return shape;
				}

				public Shaped shape(String... shape) {
					this.shape[0] = shape[0];
					this.shape[1] = shape[1];
					this.shape[2] = shape[2];
					return this;
				}
			}
		}
	}
}
