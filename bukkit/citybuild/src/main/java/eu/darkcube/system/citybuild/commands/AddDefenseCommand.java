//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.Citybuild;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddDefenseCommand implements CommandExecutor {
    private Citybuild plugin;

    public AddDefenseCommand(Citybuild plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§7Falsche Anzahl von Argumenten! Verwendung: §a/adddefense <Spielername> <Defense>");
            return false;
        } else {
            String playerName = args[0];

            int defenseToAdd;
            try {
                defenseToAdd = Integer.parseInt(args[1]);
            } catch (NumberFormatException var8) {
                sender.sendMessage("§7Ungültiger §aDefense-Wert§7! Bitte geben Sie eine Zahl ein");// 30
                return false;
            }

            Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
            if (targetPlayer == null) {
                sender.sendMessage("§7Spieler nicht gefunden");
                return true;
            } else {
                this.plugin.getDefenseManager().addDefense(targetPlayer, defenseToAdd);
                sender.sendMessage("§7Es wurden §a" + defenseToAdd + " §7Defense zum Spieler §a" + targetPlayer.getName() + " §7hinzugefügt");
                return true;
            }
        }
    }
}
