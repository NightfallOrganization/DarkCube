package building.oneblock.items.gui.buttons;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static building.oneblock.manager.WorldManager.SPAWN;

public class Button2 {

    public static ItemStack createButton2(String world) {
        ItemStack button2 = new ItemStack(Material.FIREWORK_STAR, 1);
        ItemMeta meta = button2.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(4);
            meta.setDisplayName("§7OneBlock: " + ChatColor.of("#e66dc5") + world);
            meta.setLore(Arrays.asList(
                    " ",
//                    "§7Gesundheit: §c" + Math.round(maxhealth),
//                    "§7Regeneration: §c" + Math.round(regeneration),
//                    "§7Schaden: §b" + Math.round(damage),
//                    "§7Level: §e" + level,
//                    "§7Core: §e" + core,
//                    "§7Defense: §e",
                    " "
            ));
            button2.setItemMeta(meta);
        }

        return button2;
    }

}
