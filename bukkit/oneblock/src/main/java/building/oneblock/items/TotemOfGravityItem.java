package building.oneblock.items;
import building.oneblock.OneBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class TotemOfGravityItem {

    public static ItemStack createTotemofGravityItem() {
        ItemStack totemOfGravity = new ItemStack(Material.FIREWORK_STAR, 1);
        ItemMeta meta = totemOfGravity.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(1);
            meta.setDisplayName("§7Totem of §aGravity");
            totemOfGravity.setItemMeta(meta);
        }

        return totemOfGravity;
    }

    public static void registerTotemofGravityRecipe() {
        ItemStack totemOfGravity = createTotemofGravityItem();
        NamespacedKey key = new NamespacedKey(OneBlock.getInstance(), "totem_of_gravity");
        ShapedRecipe recipe = new ShapedRecipe(key, totemOfGravity);

        recipe.shape("ABA", "BCB", "ABA");
        recipe.setIngredient('A', Material.DEEPSLATE_TILES);
        recipe.setIngredient('B', Material.DEEPSLATE_EMERALD_ORE);
        recipe.setIngredient('C', Material.TOTEM_OF_UNDYING);

        Bukkit.addRecipe(recipe);
    }
}
