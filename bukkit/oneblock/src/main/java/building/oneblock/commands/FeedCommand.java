package building.oneblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNur Spieler können diesen Befehl ausführen");
                return true;
            }
            Player player = (Player) sender;
            player.setFoodLevel(20);
            player.sendMessage("§7Du wurdest §egefüttert");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cSpieler nicht gefunden");
                return true;
            }
            target.setFoodLevel(20);
            target.sendMessage("§7Du wurdest §egefüttert");
            sender.sendMessage("§7Du hast §e" + target.getName() + " §7gefüttert");
        } else {
            sender.sendMessage("§cUsage: /feed or /feed [player]");
        }
        return true;
    }
}
