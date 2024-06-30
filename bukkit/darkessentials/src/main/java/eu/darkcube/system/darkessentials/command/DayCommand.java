package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class DayCommand extends DarkCommand {

    public DayCommand() {
        super("day", new String[0], builder-> builder.executes(context -> {

            context.getSource().getWorld().setTime(1000);
            context.getSource().sendMessage(Message.SET_DAY);

            return 0;
        }));
    }

}
