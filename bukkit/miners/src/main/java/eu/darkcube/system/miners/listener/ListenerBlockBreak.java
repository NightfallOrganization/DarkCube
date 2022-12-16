package eu.darkcube.system.miners.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.effect.MinersEffect;
import eu.darkcube.system.miners.items.Item;
import eu.darkcube.system.miners.player.TNTManager;

public class ListenerBlockBreak implements Listener {

    public static final List<Material> DONT_BREAK = Arrays.asList(Material.GLOWSTONE, Material.LOG);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (Miners.getGamephase() != 1) {
            e.setCancelled(true);
            return;
        }
        if (DONT_BREAK.contains(e.getBlock().getType()))
            e.setCancelled(true);
        e.setExpToDrop(0);
        handleDrops(e.getBlock(), e.getPlayer());
    }

    @EventHandler
    public void onBlockBreakBad(BlockBreakEvent e) {
        if (Miners.getGamephase() != 1) {
            e.setCancelled(true);
        } else {
            if (DONT_BREAK.contains(e.getBlock().getType()))
                e.setCancelled(true);
            e.setExpToDrop(0);
            handleDrops(e.getBlock(), e.getPlayer());
        }
    }

    public static boolean shouldDouble(Player p) {
        return false;
    }

    public static void handleDrops(Block b, Player p) {
        switch (b.getType()) {
            case AIR:
                return;
            case LOG:
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                p.getInventory().addItem(Item.STICK.getItem(p, 8));
                if (shouldDouble(p))
                    p.getInventory().addItem(Item.STICK.getItem(p, 8));
                break;
            case IRON_ORE:
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                p.getInventory().addItem(Item.INGOT_IRON.getItem(p));
                if (shouldDouble(p))
                    p.getInventory().addItem(Item.INGOT_IRON.getItem(p));
                break;
            case IRON_BLOCK:
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                p.getInventory().addItem(Item.INGOT_IRON.getItem(p, 9));
                if (shouldDouble(p))
                    p.getInventory().addItem(Item.INGOT_IRON.getItem(p, 9));
                break;
            case GOLD_ORE:
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                p.getInventory().addItem(Item.INGOT_GOLD.getItem(p));
                if (shouldDouble(p))
                    p.getInventory().addItem(Item.INGOT_GOLD.getItem(p));
                break;
            case GOLD_BLOCK:
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                p.getInventory().addItem(Item.INGOT_GOLD.getItem(p, 9));
                if (shouldDouble(p))
                    p.getInventory().addItem(Item.INGOT_GOLD.getItem(p, 9));
                break;
            case DIAMOND_ORE:
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                p.getInventory().addItem(Item.DIAMOND.getItem(p));
                if (shouldDouble(p))
                    p.getInventory().addItem(Item.DIAMOND.getItem(p));
                break;
            case DIAMOND_BLOCK:
                p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                p.getInventory().addItem(Item.DIAMOND.getItem(p, 9));
                if (shouldDouble(p))
                    p.getInventory().addItem(Item.DIAMOND.getItem(p, 9));
                break;
            case EMERALD_ORE:
                // xp
                break;
            case EMERALD_BLOCK:
                // xp
                break;
            case TNT:
                if (p.getInventory().getItemInHand().getType().equals(Material.SHEARS)) {
                    p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                    p.getInventory().addItem(Item.TNT.getItem(p));
                } else
                    TNTManager.explodeTNT(b.getLocation(), null, 10);
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                p.addPotionEffect(MinersEffect.getRandomEffect().getEffect());
                break;
            default:
                break;
        }
    }

}
