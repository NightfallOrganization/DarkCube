package building.oneblock.commands;

import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            user.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return true;
        }

        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (args.length == 0) {
            player.setFoodLevel(80);
            user.sendMessage(Message.COMMAND_FEED);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                user.sendMessage(Message.PLAYER_NOT_FOUND);
                return true;
            }
            target.setFoodLevel(80);
            User targetuser = UserAPI.instance().user(target.getUniqueId());
            targetuser.sendMessage(Message.COMMAND_FEED);
            user.sendMessage(Message.COMMAND_FED, target.getName());
        } else {
            sender.sendMessage("§cUsage: /feed or /feed [player]");
        }
        return true;
    }
}
