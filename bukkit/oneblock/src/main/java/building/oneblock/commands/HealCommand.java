package building.oneblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNur Spieler können diesen Befehl ausführen");
                return true;
            }
            Player player = (Player) sender;
            player.setHealth(40.0);
            player.sendMessage("§7Du wurdest §egeheilt");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cSpieler nicht gefunden");
                return true;
            }
            target.setHealth(40.0);
            target.sendMessage("§7Du wurdest §egeheilt");
            sender.sendMessage("§7Du hast §e" + target.getName() + " §7geheilt");
        } else {
            sender.sendMessage("§cUsage: /heal or /heal [player]");
        }
        return true;
    }
}
