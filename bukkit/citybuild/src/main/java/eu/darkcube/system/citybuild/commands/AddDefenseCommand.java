//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddDefenseCommand implements CommandExecutor {
    private Citybuild plugin;

    public AddDefenseCommand(Citybuild plugin) {
        this.plugin = plugin;// 15
    }// 16

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {// 20
            sender.sendMessage("§7Falsche Anzahl von Argumenten! Verwendung: §a/adddefense <Spielername> <Defense>");// 21
            return false;// 22
        } else {
            String playerName = args[0];// 25

            int defenseToAdd;
            try {
                defenseToAdd = Integer.parseInt(args[1]);// 28
            } catch (NumberFormatException var8) {// 29
                sender.sendMessage("§7Ungültiger §aDefense-Wert§7! Bitte geben Sie eine Zahl ein");// 30
                return false;// 31
            }

            Player targetPlayer = Bukkit.getServer().getPlayer(playerName);// 34
            if (targetPlayer == null) {// 35
                sender.sendMessage("§7Spieler nicht gefunden");// 36
                return true;// 37
            } else {
                this.plugin.getDefenseManager().addDefense(targetPlayer, defenseToAdd);// 39
                sender.sendMessage("§7Es wurden §a" + defenseToAdd + " §7Defense zum Spieler §a" + targetPlayer.getName() + " §7hinzugefügt");// 40
                return true;// 41
            }
        }
    }
}
