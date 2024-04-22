package building.oneblock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class NightCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.getWorld().setTime(13000);
            player.sendMessage(ChatColor.GREEN + "§7Die Zeit wurde auf §eNacht §7gesetzt");
        } else {
            sender.sendMessage(ChatColor.RED + "§cNur Spieler können diesen Befehl ausführen");
        }
        return true;
    }
}
