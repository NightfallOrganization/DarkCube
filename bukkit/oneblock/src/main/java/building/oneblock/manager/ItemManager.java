package building.oneblock.manager;

import building.oneblock.items.CrookItem;
import building.oneblock.items.RodOfTheSkyItem;
import building.oneblock.items.TotemOfGravityItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemManager {

    private final JavaPlugin plugin;

    public ItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void craftingItemTemplate(Material material, String[] recipeShape, Material[] recipeIngredients) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            item.setItemMeta(meta);
        }

        ShapedRecipe recipe = new ShapedRecipe(item);
        recipe.shape(recipeShape);

        char character = 'A';
        for (Material ingredient : recipeIngredients) {
            recipe.setIngredient(character, ingredient);
            character++;
        }

        Bukkit.addRecipe(recipe);
    }

    public void createAllRecipes() {
        String[] recipeShape = {" A ", " B "};
        String[] recipeShape2 = {"AB ", "BA "};
        String[] recipeShape3 = {" A "};
        String[] recipeShape4 = {"ABA", "BCB", "ABA"};
        String[] recipeShape5 = {"ABA", " C ", " C "};


        Material customMaterial = Material.WATER_BUCKET;
        Material[] recipeIngredients = {Material.PACKED_ICE, Material.BUCKET};
        craftingItemTemplate(customMaterial, recipeShape, recipeIngredients);

        Material customMaterial2 = Material.LAVA_BUCKET;
        Material[] recipeIngredients2 = {Material.MAGMA_BLOCK, Material.BUCKET};
        craftingItemTemplate(customMaterial2, recipeShape, recipeIngredients2);

        Material customMaterial3 = Material.COARSE_DIRT;
        Material[] recipeIngredients3 = {Material.GRAVEL, Material.DIRT};
        craftingItemTemplate(customMaterial3, recipeShape2, recipeIngredients3);

        Material customMaterial4 = Material.OAK_SAPLING;
        Material[] recipeIngredients4 = {Material.OAK_PLANKS};
        craftingItemTemplate(customMaterial4, recipeShape3, recipeIngredients4);

        Material customMaterial5 = Material.ACACIA_SAPLING;
        Material[] recipeIngredients5 = {Material.ACACIA_PLANKS};
        craftingItemTemplate(customMaterial5, recipeShape3, recipeIngredients5);

        Material customMaterial6 = Material.BAMBOO_SAPLING;
        Material[] recipeIngredients6 = {Material.BAMBOO_PLANKS};
        craftingItemTemplate(customMaterial6, recipeShape3, recipeIngredients6);

        Material customMaterial7 = Material.BIRCH_SAPLING;
        Material[] recipeIngredients7 = {Material.BIRCH_PLANKS};
        craftingItemTemplate(customMaterial7, recipeShape3, recipeIngredients7);

        Material customMaterial8 = Material.CHERRY_SAPLING;
        Material[] recipeIngredients8 = {Material.CHERRY_PLANKS};
        craftingItemTemplate(customMaterial8, recipeShape3, recipeIngredients8);

        Material customMaterial9 = Material.SPRUCE_SAPLING;
        Material[] recipeIngredients9 = {Material.SPRUCE_PLANKS};
        craftingItemTemplate(customMaterial9, recipeShape3, recipeIngredients9);

        Material customMaterial10 = Material.DARK_OAK_SAPLING;
        Material[] recipeIngredients10 = {Material.DARK_OAK_PLANKS};
        craftingItemTemplate(customMaterial10, recipeShape3, recipeIngredients10);

        Material customMaterial11 = Material.POTTED_JUNGLE_SAPLING;
        Material[] recipeIngredients11 = {Material.JUNGLE_PLANKS};
        craftingItemTemplate(customMaterial11, recipeShape3, recipeIngredients11);

        Material customMaterial12 = Material.GRASS_BLOCK;
        Material[] recipeIngredients12 = {Material.OAK_SAPLING, Material.DIRT};
        craftingItemTemplate(customMaterial12, recipeShape, recipeIngredients12);

        TotemOfGravityItem.registerTotemofGravityRecipe();
        RodOfTheSkyItem.registerRodOfTheSkyRecipe();
        CrookItem.registerCrookRecipe();
    }

    public void removeAllRecipes() {
        TotemOfGravityItem.unregisterTotemofGravityRecipe();
    }

}
