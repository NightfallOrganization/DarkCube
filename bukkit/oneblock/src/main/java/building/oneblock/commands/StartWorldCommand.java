package building.oneblock.commands;

import building.oneblock.OneBlock;
import building.oneblock.manager.WorldManager;
import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Bukkit;


public class StartWorldCommand implements CommandExecutor {
    private int taskID = -1;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            user.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return true;
        }

        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());
        startActionBarTask(player);

        World playerWorld = WorldManager.createPlayerSpecificVoidWorld(player.getName());

        Bukkit.getScheduler().scheduleSyncDelayedTask(OneBlock.getInstance(), new Runnable() {
            public void run() {

                if (playerWorld != null) {
                    Location location = new Location(playerWorld, 0.5, 100, 0.5);
                    player.teleportAsync(location).thenAccept(success -> {
                        user.sendMessage(Message.ONEBLOCK_WELCOME_WORLD);
                        stopActionBarTask();
                    });

                } else {
                    user.sendMessage(Message.ONEBLOCK_CREATING_WORLD_ERROR);
                    stopActionBarTask();
                }

            }
        }, 100L); // 100 Ticks entsprechen 5 Sekunden bei 20 Ticks pro Sekunde

        return true;
    }

    private void startActionBarTask(Player player) {
        User user = UserAPI.instance().user(player.getUniqueId());

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlock.getInstance(), new Runnable() {
            private boolean dot = false;

            @Override
            public void run() {
                if (dot) {
                    user.sendActionBar(Message.ONEBLOCK_CREATING_WORLD_1);
//                    player.sendActionBar("§7Starting ..");
                } else {
                    user.sendActionBar(Message.ONEBLOCK_CREATING_WORLD_2);
//                    player.sendActionBar("§7Starting ...");
                }
                dot = !dot;
            }
        }, 0L, 20L);
    }

    private void stopActionBarTask() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
        }
    }
}
