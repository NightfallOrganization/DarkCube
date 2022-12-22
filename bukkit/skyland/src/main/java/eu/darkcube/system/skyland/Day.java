package eu.darkcube.system.skyland;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;

import java.sql.Time;

public class Day implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        Player player = (Player) sender;

        if((args.length == 0) && command.getName().equalsIgnoreCase("day")) {

            World world = player.getWorld();
            world.setTime(3000);
            sender.sendMessage("§7Du hast §bTag §7gesetzt");

            return true;
        }



        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/day §7, um Tag zu setzten");
        return false;
    }


}