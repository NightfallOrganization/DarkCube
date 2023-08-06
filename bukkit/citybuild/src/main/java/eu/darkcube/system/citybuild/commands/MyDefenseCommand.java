//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyDefenseCommand implements CommandExecutor {
    private DefenseManager defenseManager;

    public MyDefenseCommand(DefenseManager defenseManager) {
        this.defenseManager = defenseManager;// 13
    }// 14

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {// 18
            int defense = this.defenseManager.getDefense(player);// 20
            player.sendMessage("§7Du hast §a" + defense + " §7Defense");// 21
            return true;// 22
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");// 24
            return false;// 25
        }
    }
}
