package building.oneblock.items.gui.buttons;
import building.oneblock.OneBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Button1 {

    public static ItemStack createButton1() {
        ItemStack button1 = new ItemStack(Material.FIREWORK_STAR, 1);
        ItemMeta meta = button1.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(3);
            meta.setDisplayName("§e+ §7Welt erstellen");
            button1.setItemMeta(meta);
        }

        return button1;
    }

}
