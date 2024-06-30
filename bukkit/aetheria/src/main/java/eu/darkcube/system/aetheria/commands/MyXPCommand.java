package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.LevelXPManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyXPCommand implements CommandExecutor {

    private LevelXPManager levelXPManager;

    public MyXPCommand(LevelXPManager levelXPManager) {
        this.levelXPManager = levelXPManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            double xp = levelXPManager.getXP(player);
            int level = levelXPManager.getLevel(player);
            int xpNeeded = levelXPManager.getXPForLevel(level+1);
            player.sendMessage("§7Du hast §a" + xp + " §7XP von §a" + xpNeeded + " §7XP");
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}
