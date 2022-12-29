package eu.darkcube.system.skyland;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldX implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§7Du bist kein Spieler!");
            return true;
        }

        if (args.length != 1) {
//			sender.sendMessage("§7Unbekannter Befehl. Nutze §b/world (world) §7um zu anderen Welten zu reisen");
            return false;
        }

        World w;

        if (args.length == 1) {
            w = Bukkit.getWorld(args[0]);

            if (w == null) {
                sender.sendMessage("§7Die Welt §b" + args[0] + " §7wurde nicht gefunden §7!");
                return true;
            }
            ((Player) sender).teleport(w.getSpawnLocation().add(0.5, 0, 0.5));

        }
//		Location loc = w.getSpawnLocation();
//		Player p = (Player) sender;
//		p.teleport(loc);

        return true;

    }

}