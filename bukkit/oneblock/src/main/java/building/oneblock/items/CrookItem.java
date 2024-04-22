package building.oneblock.items;

import building.oneblock.OneBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class CrookItem implements Listener {
    public static ItemStack createCrookItem() {
        ItemStack totemOfGravity = new ItemStack(Material.WOODEN_HOE, 1);
        ItemMeta meta = totemOfGravity.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(1);
            meta.setDisplayName("§6Crook");
            meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            totemOfGravity.setItemMeta(meta);
        }

        return totemOfGravity;
    }

    public static void registerCrookRecipe() {
        ItemStack crook = createCrookItem();
        NamespacedKey key = new NamespacedKey(OneBlock.getInstance(), "crook");
        ShapedRecipe recipe = new ShapedRecipe(key, crook);

        recipe.shape("AA ", " B ", " B ");

        // Accept any type of wooden plank
        Material[] planks = new Material[]{
                Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
                Material.JUNGLE_PLANKS, Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS,
                Material.CRIMSON_PLANKS, Material.WARPED_PLANKS
        };
        RecipeChoice planksChoice = new RecipeChoice.MaterialChoice(planks);

        recipe.setIngredient('A', planksChoice);
        recipe.setIngredient('B', Material.DEEPSLATE_EMERALD_ORE);

        Bukkit.addRecipe(recipe);
    }


}
