package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.util.CustomHealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyHealthCommand implements CommandExecutor {

    private CustomHealthManager healthManager;

    public MyHealthCommand(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int health = healthManager.getHealth(player);
            int maxHealth = healthManager.getMaxHealth(player);
            player.sendMessage("§7Du hast §a" + health + " §7von §a" + maxHealth + " §7Health");
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}
