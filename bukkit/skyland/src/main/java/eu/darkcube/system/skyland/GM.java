package eu.darkcube.system.skyland;

import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

public class GM implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Überprüfe, ob der Befehl von einem Spieler stammt (und nicht vom Server)
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        // Wandle den Sender des Befehls in ein Player-Objekt um
        Player player = (Player) sender;

        // Überprüfe, ob der Befehl "/gm 1" ausgeführt wurde
        if (command.getName().equalsIgnoreCase("gm") && args.length > 0 && args[0].equalsIgnoreCase("1")) {

            // Setze den Gamemode des Spielers auf Creative
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage("Dein Gamemode wurde auf Creative gesetzt.");
            return true;
        }

        // Falls der Befehl nicht erkannt wurde, gib eine entsprechende Nachricht aus
        player.sendMessage("Unbekannter Befehl. Nutze /gm 1, um den Gamemode auf Creative zu setzen.");
        return false;
    }

}