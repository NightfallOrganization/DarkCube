package eu.darkcube.system.skyland;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

public class Max implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("max") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/max (Person) §7um dich zu maxen");
            return false;

        }


        if ((args.length == 1) && (Bukkit.getPlayer(args[0])!=null)) {
            Player player = Bukkit.getPlayer(args[0]);

            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            sender.sendMessage("§b"+ player.getName() +"§7 wurde gemaxed");
            return true;
        }
        else if(args.length == 0) {
            Player player = (Player) sender;

            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            sender.sendMessage("§7Du wurdest §bgemaxed");
            return true;

        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/max (Person) §7um dich zu maxen");
        return false;
    }


}