package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;

public class MaxCommand extends DarkCommand {

    public MaxCommand() {
        super("max",builder-> builder.executes(context -> {

            context.getSource().asPlayer().setHealth(20);
            context.getSource().asPlayer().setSaturation(30);
            context.getSource().asPlayer().setFoodLevel(30);
            context.getSource().sendMessage(Message.MAX);

            return 0;
        }));
    }

}
