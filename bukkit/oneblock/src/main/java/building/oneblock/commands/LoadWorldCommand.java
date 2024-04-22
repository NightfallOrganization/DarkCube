package building.oneblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LoadWorldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /loadworld <world>");
            return true;
        }

        String worldName = args[0];
        if (Bukkit.getWorld(worldName) != null) {
            sender.sendMessage("§cDie Welt " + worldName + " ist schon geladen!");
            return true;
        }

        Bukkit.createWorld(new WorldCreator(worldName));
        sender.sendMessage("§7Die Welt §e" + worldName + " §7wurde erfolgreich geladen!");
        return true;
    }
}
