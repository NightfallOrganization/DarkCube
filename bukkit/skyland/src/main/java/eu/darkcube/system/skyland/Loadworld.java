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

public class Loadworld implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("loadworld")) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/loadworld (world) §7um Welten zu laden");
            return false;

        }

        if (args.length == 1) {

            Player player = (Player) sender;
            Server server = Bukkit.getServer();

            File f = new File("./" + args[0]);
            if(f.exists()) {
                WorldCreator creator = new WorldCreator(args[0]);
                World meineWelt = creator.createWorld();

                sender.sendMessage("§7Du hast die Welt§b " + args[0] + " §7geladen");
                return true;
            }
            else {
                sender.sendMessage("§7Die Welt§b " + args[0] + " §7existiert nicht");
            }

        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/loadworld (world) §7um Welten zu laden");
        return false;
    }


}