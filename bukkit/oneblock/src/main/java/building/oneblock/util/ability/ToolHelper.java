package building.oneblock.util.ability;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ToolHelper implements Listener {

//    @EventHandler
//    public void onBlockBreak(BlockBreakEvent event) {
//        Player player = event.getPlayer();
//        Block block = event.getBlock();
//        ItemStack bestTool = findBestTool(player, block);
//
//        if (bestTool != null) {
//            player.getInventory().setItemInMainHand(bestTool);
//        }
//    }
//
//    private ItemStack findBestTool(Player player, Block block) {
//        ItemStack bestTool = null;
//        double bestScore = 0.0;
//
//        for (ItemStack item : player.getInventory().getContents()) {
//            if (item != null && isTool(item) && canHarvest(block, item)) {
//                double score = calculateEfficiency(item, block);
//                if (score > bestScore) {
//                    bestScore = score;
//                    bestTool = item;
//                }
//            }
//        }
//        return bestTool;
//    }
//
//    private boolean isTool(ItemStack item) {
//        if (item == null) {
//            return false;
//        }
//
//        switch (item.getType()) {
//            case WOODEN_PICKAXE:
//            case STONE_PICKAXE:
//            case IRON_PICKAXE:
//            case GOLDEN_PICKAXE:
//            case DIAMOND_PICKAXE:
//            case NETHERITE_PICKAXE:
//            case WOODEN_AXE:
//            case STONE_AXE:
//            case IRON_AXE:
//            case GOLDEN_AXE:
//            case DIAMOND_AXE:
//            case NETHERITE_AXE:
//            case WOODEN_SHOVEL:
//            case STONE_SHOVEL:
//            case IRON_SHOVEL:
//            case GOLDEN_SHOVEL:
//            case DIAMOND_SHOVEL:
//            case NETHERITE_SHOVEL:
//            case WOODEN_HOE:
//            case STONE_HOE:
//            case IRON_HOE:
//            case GOLDEN_HOE:
//            case DIAMOND_HOE:
//            case NETHERITE_HOE:
//            case SHEARS:
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    public boolean canHarvest(Block block, ItemStack tool) {
//        Material blockType = block.getType();
//        Material toolType = tool.getType();
//
//        // Beispiellogik für die Zuordnung von Werkzeugen zu Blocktypen
//        switch (blockType) {
//            case STONE:
//            case COBBLESTONE:
//            case IRON_ORE:
//            case DIAMOND_ORE:
//                // Pickaxes sind effektiv für Stein und Erze
//                return toolType == Material.WOODEN_PICKAXE ||
//                        toolType == Material.STONE_PICKAXE ||
//                        toolType == Material.IRON_PICKAXE ||
//                        toolType == Material.GOLDEN_PICKAXE ||
//                        toolType == Material.DIAMOND_PICKAXE ||
//                        toolType == Material.NETHERITE_PICKAXE;
//            case DIRT:
//            case GRASS_BLOCK:
//            case SAND:
//                // Shovels sind effektiv für Erde, Gras und Sand
//                return toolType == Material.WOODEN_SHOVEL ||
//                        toolType == Material.STONE_SHOVEL ||
//                        toolType == Material.IRON_SHOVEL ||
//                        toolType == Material.GOLDEN_SHOVEL ||
//                        toolType == Material.DIAMOND_SHOVEL ||
//                        toolType == Material.NETHERITE_SHOVEL;
//            case OAK_LOG:
//            case ACACIA_LOG:
//            case BIRCH_LOG:
//            case CHERRY_LOG:
//            case DARK_OAK_LOG:
//            case JUNGLE_LOG:
//            case MANGROVE_LOG:
//            case SPRUCE_LOG:
//                return toolType == Material.WOODEN_AXE ||
//                        toolType == Material.STONE_AXE ||
//                        toolType == Material.IRON_AXE ||
//                        toolType == Material.GOLDEN_AXE ||
//                        toolType == Material.DIAMOND_AXE ||
//                        toolType == Material.NETHERITE_AXE;
//            case ACACIA_LEAVES:
//            case AZALEA_LEAVES:
//            case BIRCH_LEAVES:
//            case CHERRY_LEAVES:
//            case DARK_OAK_LEAVES:
//            case FLOWERING_AZALEA_LEAVES:
//            case JUNGLE_LEAVES:
//            case MANGROVE_LEAVES:
//            case OAK_LEAVES:
//            case SPRUCE_LEAVES:
//                return toolType == Material.WOODEN_HOE;
//            default:
//                // Für alle anderen Blöcke nehmen wir an, dass kein spezielles Werkzeug benötigt wird
//                return true;
//        }
//    }
//    private double calculateEfficiency(ItemStack tool, Block block) {
//        Material toolType = tool.getType();
//        Material blockType = block.getType();
//        double efficiency = 0.0;
//
//        if (isAppropriateTool(toolType, blockType)) {
//            efficiency += 10.0;
//            if (tool.containsEnchantment(Enchantment.DIG_SPEED)) {
//                efficiency += 5.0 * tool.getEnchantmentLevel(Enchantment.DIG_SPEED);
//            }
//        }
//        return efficiency;
//    }
//
//    private boolean isAppropriateTool(Material tool, Material block) {
//        // Beispiellogik für die Überprüfung, ob das Werkzeug für den Block geeignet ist
//        switch (block) {
//            case STONE:
//            case COBBLESTONE:
//                return tool == Material.DIAMOND_PICKAXE || tool == Material.IRON_PICKAXE;
//            case DIRT:
//            case GRASS_BLOCK:
//                return tool == Material.DIAMOND_SHOVEL || tool == Material.IRON_SHOVEL;
//            // Fügen Sie zusätzliche Fälle für andere Block- und Werkzeugtypen hinzu
//            default:
//                return false;
//        }
//    }
}
