package building.oneblock.items.gui;

import building.oneblock.manager.player.CoreManager;
import building.oneblock.manager.player.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class PlayerHeadItem {

    public static ItemStack getPlayerHeadItem(Player player, PlayerManager playerManager) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if (skullMeta != null) {

//            double damage = playerManager.getDamageManager().getDamage(player);
            int level = playerManager.getLevelManager().getLevel(player);
//            double maxhealth = playerManager.getMaxHealthManager().getMaxHealth(player);
//            double regeneration = playerManager.getRegenerationManager().getRegenerationRate(player);
//            int attrpValue = playerManager.getAttributePointsManager().getAttributePoints(player);
            int core = playerManager.getCoreManager().getCoreValue(player);

            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName("§e" + player.getName());
            skullMeta.setLore(Arrays.asList(
                    " ",
//                    "§7Gesundheit: §c" + Math.round(maxhealth),
//                    "§7Regeneration: §c" + Math.round(regeneration),
//                    "§7Schaden: §b" + Math.round(damage),
                    "§7Level: §e" + level,
                    "§7Core: §e" + core,
//                    "§7Defense: §e",
                    " "
            ));

            playerHead.setItemMeta(skullMeta);
        }
        return playerHead;
    }

}
