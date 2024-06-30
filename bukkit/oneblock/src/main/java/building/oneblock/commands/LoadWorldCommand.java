package building.oneblock.commands;

import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoadWorldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /loadworld [world]");
            return true;
        }

        String worldName = args[0];
        if (Bukkit.getWorld(worldName) != null) {
            user.sendMessage(Message.ONEBLOCK_WORLD_ALREADY_LOADED, worldName);
            return true;
        }

        Bukkit.createWorld(new WorldCreator(worldName));
        user.sendMessage(Message.ONEBLOCK_WORLD_SUCCESSFUL_LOADED, worldName);
        return true;
    }
}
