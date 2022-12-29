package eu.darkcube.system.skyland;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

import java.awt.*;
import java.io.File;

public class UnloadWorld implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("unloadworld") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/unloadworld (world) §7um Welten zu entladen");
            return false;

        }

        if (args.length == 1) {
            Player player = (Player) sender;
            World world = Bukkit.getWorld(args[0]);
            Bukkit.unloadWorld(world, true);
            sender.sendMessage("§7Du hast die Welt§b " + args[0] + " §7entladen");
            return true;
        }
        else {
            sender.sendMessage("§7Die Welt§b " + args[0] + " §7existiert nicht");
        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/unloadworld (world) §7um Welten zu entladen");
        return false;
    }


}