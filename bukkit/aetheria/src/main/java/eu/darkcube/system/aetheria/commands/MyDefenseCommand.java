//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.DefenseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyDefenseCommand implements CommandExecutor {
    private DefenseManager defenseManager;

    public MyDefenseCommand(DefenseManager defenseManager) {
        this.defenseManager = defenseManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            double defense = this.defenseManager.getDefense(player);  // Ändern Sie int zu double
            player.sendMessage("§7Du hast §a" + defense + " §7Defense");
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}
