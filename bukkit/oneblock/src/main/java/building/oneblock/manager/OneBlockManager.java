package building.oneblock.manager;

import building.oneblock.OneBlock;
import building.oneblock.manager.player.DataStorageManager;
import building.oneblock.util.StageBlocks;
import building.oneblock.util.WorldRepository;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OneBlockManager implements Listener {
    private final Map<Integer, List<Material>> stageBlocks = StageBlocks.createStageBlocksMap();
    private final Map<Integer, Integer> stageThresholds = Map.of(
            1, 1,
            2, 200,
            3, 1000,
            4, 3000,
            5, 6000,
            6, 12000,
            7, 24000,
            8, 48000
    );
    private final DataStorageManager storageManager;

    public OneBlockManager(DataStorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        World world = event.getBlock().getWorld();
        if (!world.getName().equals("world") && !world.getName().equals("world_nether")) {
            Block block = event.getBlock();
            if (block.getX() == 0 && block.getY() == 99 && block.getZ() == 0) {
                int progress = storageManager.getBlocksMined(world) + 1;
                storageManager.setBlocksMined(world, progress);

                int currentStage = getCurrentStage(progress);
                int previousStage = getCurrentStage(progress - 1);

                if (currentStage > previousStage) {
                    for (Player player : world.getPlayers()) {
                        player.sendMessage("§eOneBlock§7: Stage §e" + currentStage + " §7wurde erreicht");
                    }
                }

                if (stageBlocks.containsKey(currentStage)) {
                    List<Material> possibleBlocks = stageBlocks.get(currentStage);
                    Material nextBlock = possibleBlocks.get(new Random().nextInt(possibleBlocks.size()));
                    Bukkit.getScheduler().runTask(OneBlock.getInstance(), () -> block.setType(nextBlock));
                }
            }
        }
    }

    private void newStage() {

    }

    private int getCurrentStage(int progress) {
        int currentStage = 1;
        for (int i = 1; i <= stageThresholds.size(); i++) {
            if (progress >= stageThresholds.get(i)) {
                currentStage = i;
            } else {
                break;
            }
        }
        return currentStage;
    }
}
