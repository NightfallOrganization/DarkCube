package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.util.LevelXPManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyLevelCommand implements CommandExecutor {

    private LevelXPManager levelXPManager;

    public MyLevelCommand(LevelXPManager levelXPManager) {
        this.levelXPManager = levelXPManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return true;
        }

        Player player = (Player) sender;
        int level = levelXPManager.getLevel(player);
        player.sendMessage("§7Dein Level ist: §a" + level);
        return true;
    }
}
