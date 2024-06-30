package building.oneblock.commands;

import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

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
            player.setHealth(20.0);
            user.sendMessage(Message.COMMAND_HEAL_GET);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                user.sendMessage(Message.PLAYER_NOT_FOUND);
                return true;
            }
            target.setHealth(20.0);
            User targetuser = UserAPI.instance().user(target.getUniqueId());
            targetuser.sendMessage(Message.COMMAND_HEAL_GET);
            user.sendMessage(Message.COMMAND_HEAL_SET, target.getName());
        } else {
            sender.sendMessage("§cUsage: /heal or /heal [player]");
        }
        return true;
    }
}