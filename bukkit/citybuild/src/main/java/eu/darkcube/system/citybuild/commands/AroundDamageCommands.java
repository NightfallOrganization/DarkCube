//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.listener.Citybuild;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AroundDamageCommands implements CommandExecutor {
    private NamespacedKey aroundDamageKey;

    public AroundDamageCommands(Citybuild plugin) {
        this.aroundDamageKey = new NamespacedKey(plugin, "AroundDamage");// 23
    }// 24

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (command.getName().equalsIgnoreCase("addarounddamage")) {// 28
            if (args.length == 2) {// 29
                target = Bukkit.getPlayer(args[0]);// 30
                if (target != null) {// 31
                    try {
                        double additionalDamage = Double.parseDouble(args[1]);// 33
                        PersistentDataContainer dataContainer = target.getPersistentDataContainer();// 34
                        double currentDamage = (Double)dataContainer.getOrDefault(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);// 35
                        dataContainer.set(this.aroundDamageKey, PersistentDataType.DOUBLE, currentDamage + additionalDamage);// 36
                        sender.sendMessage("§7Es wurde §a" + additionalDamage + " §7Around Damage zum Spieler §a" + target.getName() + " §7hinzugefügt");// 37
                    } catch (NumberFormatException var11) {// 38
                        sender.sendMessage("§7Ungültiger §aDamage-Wert§7! Bitte geben Sie eine Zahl ein" + args[1]);// 39
                    }
                } else {
                    sender.sendMessage("Player not found: " + args[0]);// 42
                }
            } else {
                sender.sendMessage("§7Falsche Anzahl von Argumenten! Verwendung: §a/addarounddamage <Spielername> <Zahl>");// 45
            }

            return true;// 47
        } else if (command.getName().equalsIgnoreCase("myarounddamage")) {// 48
            if (sender instanceof Player) {// 49
                target = (Player)sender;// 50
                PersistentDataContainer dataContainer = target.getPersistentDataContainer();// 51
                double currentDamage = (Double)dataContainer.getOrDefault(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);// 52
                target.sendMessage("§7Dein Around Damage ist: §a" + currentDamage);// 53
            } else {
                sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");// 55
            }

            return true;// 57
        } else {
            return false;// 59
        }
    }
}
