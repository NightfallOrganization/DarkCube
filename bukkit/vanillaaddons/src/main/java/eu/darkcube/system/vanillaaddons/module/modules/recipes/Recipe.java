/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.recipes;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.com.google.gson.JsonPrimitive;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.modules.recipes.Recipe.RecipeTemplate.Type.Shaped;
import eu.darkcube.system.vanillaaddons.util.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class Recipe {

	private final Item item;
	private final RecipeTemplate[] recipes;

	public Recipe(Item item, RecipeTemplate... recipes) {
		this.item = item;
		this.recipes = recipes;
	}

	private static final Collection<Recipe> values = new HashSet<>();
	private static final NamespacedKey recipeVersions;
	private static final Set<NamespacedKey> validKeys;
	private static final Path dataFile;
	private static final Gson dataGson;
	private static final JsonObject dataVersions;
	static boolean save = false;

	public static void registerRecipe(Recipe recipe) {
		if (values.contains(recipe))
			return;
		values.add(recipe);
		VanillaAddons addons = VanillaAddons.instance();
		for (RecipeTemplate template : recipe.recipes) {
			validKeys.add(template.key(addons, recipe));
		}
		reg(addons, recipe);
	}

	public static void unregisterRecipe(Recipe recipe) {
		if (!values.contains(recipe))
			return;
		values.remove(recipe);
		VanillaAddons addons = VanillaAddons.instance();
		for (RecipeTemplate template : recipe.recipes) {
			validKeys.remove(template.key(addons, recipe));
		}
	}

	static {
		validKeys = new HashSet<>();
		VanillaAddons addons = VanillaAddons.instance();
		recipeVersions = new NamespacedKey(addons, "recipeVersions");
		dataGson = new GsonBuilder().setPrettyPrinting().create();
		dataFile = addons.getDataFolder().toPath().resolve("recipeVersions" + ".json");
		JsonObject o;
		try {
			if (Files.exists(dataFile)) {
				BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8);
				o = dataGson.fromJson(reader, JsonObject.class);
				reader.close();
			} else {
				o = new JsonObject();
				save = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				Files.copy(dataFile,
						dataFile.getParent().resolve(dataFile.getFileName() + ".backup"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			o = new JsonObject();
		}
		dataVersions = o;
	}

	static void saveDataVersions0() {
		try {
			if (!Files.exists(dataFile.getParent())) {
				Files.createDirectories(dataFile.getParent());
			}
			BufferedWriter writer = Files.newBufferedWriter(dataFile);
			JsonObject versions = new JsonObject();
			VanillaAddons addons = VanillaAddons.instance();
			for (Recipe recipe : values()) {
				for (RecipeTemplate template : recipe.recipes) {
					NamespacedKey key = template.key(addons, recipe);
					versions.add(key.toString(), new JsonPrimitive(template.dataVersion()));
				}
			}
			dataGson.toJson(versions, writer);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public RecipeTemplate[] recipes() {
		return recipes;
	}

	public Item item() {
		return item;
	}

	private static void give(VanillaAddons addons, Recipe recipe, Player player) {
		PersistentDataContainer recipeVersionsContainer;
		if (player.getPersistentDataContainer().has(recipeVersions)) {
			recipeVersionsContainer = player.getPersistentDataContainer()
					.get(recipeVersions, PersistentDataType.TAG_CONTAINER);
		} else {
			recipeVersionsContainer = player.getPersistentDataContainer().getAdapterContext()
					.newPersistentDataContainer();
			player.getPersistentDataContainer()
					.set(recipeVersions, PersistentDataType.TAG_CONTAINER,
							recipeVersionsContainer);
		}
		for (RecipeTemplate template : recipe.recipes) {
			Integer version = null;
			NamespacedKey key = template.key(addons, recipe);
			//noinspection DataFlowIssue
			if (recipeVersionsContainer.has(key)) {
				version = recipeVersionsContainer.get(key, PersistentDataType.INTEGER);
			}
			if (version == null || !version.equals(template.dataVersion())) {
				player.undiscoverRecipe(key);
				recipeVersionsContainer.set(key, PersistentDataType.INTEGER,
						template.dataVersion());
				player.getPersistentDataContainer()
						.set(recipeVersions, PersistentDataType.TAG_CONTAINER,
								recipeVersionsContainer);
			}
			if (!player.hasDiscoveredRecipe(key))
				player.discoverRecipe(key);
		}
	}

	public static void giveAll(VanillaAddons addons, Player player) {
		PersistentDataContainer recipeVersionsContainer;
		if (player.getPersistentDataContainer().has(recipeVersions)) {
			recipeVersionsContainer = player.getPersistentDataContainer()
					.get(recipeVersions, PersistentDataType.TAG_CONTAINER);
		} else {
			recipeVersionsContainer = player.getPersistentDataContainer().getAdapterContext()
					.newPersistentDataContainer();
			player.getPersistentDataContainer()
					.set(recipeVersions, PersistentDataType.TAG_CONTAINER,
							recipeVersionsContainer);
		}
		Set<NamespacedKey> toRemove = new HashSet<>();
		//noinspection DataFlowIssue
		for (NamespacedKey key : recipeVersionsContainer.getKeys()) {
			if (!validKeys.contains(key)) {
				recipeVersionsContainer.remove(key);
				toRemove.add(key);
			}
		}
		player.undiscoverRecipes(toRemove);
		for (Recipe recipe : values()) {
			give(addons, recipe, player);
		}
	}

	private static void reg(VanillaAddons addons, Recipe recipe) {
		for (RecipeTemplate template : recipe.recipes) {
			NamespacedKey key = template.key(addons, recipe);
			org.bukkit.inventory.Recipe br = null;
			if (template.type instanceof Shaped shaped) {
				ShapedRecipe s = new ShapedRecipe(key, recipe.item().item()).shape(shaped.shape);
				template.materialIngredients.forEach(s::setIngredient);
				s.setCategory(template.category);
				br = s;
			}
			if (br != null) {
				if (dataVersions.has(key.toString())) {
					int version = dataVersions.get(key.toString()).getAsInt();
					if (version != template.dataVersion()) {
						Bukkit.removeRecipe(key);
						dataVersions.add(key.toString(),
								new JsonPrimitive(template.dataVersion()));
					}
				}
				if (Bukkit.getRecipe(key) == null)
					Bukkit.addRecipe(br);
			}
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			give(addons, recipe, player);
		}
	}

	public static RecipeTemplate shaped(String key, int dataVersion, String... shape) {
		return new RecipeTemplate(key, dataVersion, new RecipeTemplate.Type.Shaped().shape(shape));
	}

	public static class RecipeTemplate {
		private final String key;
		private final CraftingBookCategory category;
		private final Type type;
		private final int dataVersion;
		private final Map<Character, Material> materialIngredients = new HashMap<>();

		public RecipeTemplate(String key, int dataVersion, CraftingBookCategory category,
				Type type) {
			this.key = key;
			this.dataVersion = dataVersion;
			this.category = category;
			this.type = type;
		}

		public RecipeTemplate(String key, int dataVersion, Type type) {
			this(key, dataVersion, CraftingBookCategory.MISC, type);
		}

		private NamespacedKey key(VanillaAddons addons, Recipe recipe) {
			return new NamespacedKey(addons, recipe.item().name().toLowerCase() + "_" + key);
		}

		public int dataVersion() {
			return dataVersion;
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

	public static Collection<Recipe> values() {
		return values;
	}
}
