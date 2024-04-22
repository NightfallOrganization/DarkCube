package building.oneblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaxCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNur Spieler können diesen Befehl ausführen");
                return true;
            }
            Player player = (Player) sender;
            player.setHealth(20.0);
            player.setFoodLevel(40);
            player.sendMessage("§7Du wurdest §emaximiert");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cSpieler nicht gefunden");
                return true;
            }
            target.setHealth(20.0);
            target.setFoodLevel(40);
            target.sendMessage("§7Du wurdest §emaximiert");
            sender.sendMessage("§7Du hast §e" + target.getName() + " §7maximiert");
        } else {
            sender.sendMessage("§cUsage: /max or /max [player]");
        }
        return true;
    }
}