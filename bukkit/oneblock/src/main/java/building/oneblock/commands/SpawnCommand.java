package building.oneblock.commands;

import building.oneblock.OneBlock;
import building.oneblock.manager.SpawnManager;
import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnCommand implements CommandExecutor {

    private SpawnManager spawnManager;

    public SpawnCommand(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location originalLocation = player.getLocation();
            User user = UserAPI.instance().user(player.getUniqueId());

            new BukkitRunnable() {
                int countdown = 5;

                public void run() {
                    if (player.getLocation().distance(originalLocation) > 0.5) {
                        user.sendMessage(Message.TIMER);
                        player.playSound(originalLocation, Sound.ENTITY_VILLAGER_NO, 50, 1);
                        cancel();
                    } else {
                        if (countdown > 0) {
                            player.sendMessage("§7Timer: §e" + countdown);
                            player.playSound(originalLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 50, 2);
                            countdown--;
                        } else {
                            Location spawnLocation = spawnManager.getSpawnLocation();
                            player.teleport(spawnLocation);
                            player.playSound(spawnLocation, Sound.BLOCK_END_PORTAL_FRAME_FILL, 50, 2);
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(OneBlock.getInstance(), 0, 20);

            return true;
        } else {
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            user.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return false;
        }
    }
}
