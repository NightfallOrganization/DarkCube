package eu.darkcube.system.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyAPCommand implements CommandExecutor {

    private LevelXPManager levelXPManager;

    public MyAPCommand(LevelXPManager levelXPManager) {
        this.levelXPManager = levelXPManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int ap = levelXPManager.getAP(player);
            player.sendMessage("§7Du hast §a" + ap + " §7AP");
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}
