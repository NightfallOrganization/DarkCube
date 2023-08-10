//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.util.CustomHealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyRegenCommand implements CommandExecutor {
    private CustomHealthManager customHealthManager;

    public MyRegenCommand(CustomHealthManager customHealthManager) {
        this.customHealthManager = customHealthManager;// 13
    }// 14

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {// 18
            int regen = this.customHealthManager.getRegen(player);// 20
            player.sendMessage("§7Du hast §a" + regen + " §7Regeneration");// 21
            return true;// 22
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");// 24
            return false;// 25
        }
    }
}
