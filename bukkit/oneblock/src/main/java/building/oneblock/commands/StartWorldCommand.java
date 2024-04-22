package building.oneblock.commands;

import building.oneblock.OneBlock;
import building.oneblock.manager.WorldManager;
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
            sender.sendMessage("§7Nur Spieler können diesen Befehl verwenden");
            return true;
        }

        Player player = (Player) sender;
        startActionBarTask(player);

        World playerWorld = WorldManager.createPlayerSpecificVoidWorld(player.getName());

        if (playerWorld != null) {
            Location location = new Location(playerWorld, 0.5, 100, 0.5);
            player.teleportAsync(location).thenAccept(success -> {
                player.sendMessage("§7Willkommen in deiner neuen §eOneBlock §7Welt!");
                stopActionBarTask();
            });

        } else {
            player.sendMessage("§7Es gab ein Problem beim Erstellen deiner Welt. Wende dich bitte an ein Teammitglied!");
            stopActionBarTask();
        }

        return true;
    }

    private void startActionBarTask(Player player) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlock.getInstance(), new Runnable() {
            private boolean dot = false;

            @Override
            public void run() {
                if (dot) {
                    player.sendActionBar("§eWelt wird erstellt ..");
                } else {
                    player.sendActionBar("§eWelt wird erstellt ...");
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
