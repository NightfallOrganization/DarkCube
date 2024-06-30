package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;

public class HealCommand extends DarkCommand {

    public HealCommand() {
        super("heal",builder-> builder.executes(context -> {

            context.getSource().asPlayer().setHealth(20);
            context.getSource().sendMessage(Message.HEAL);

            return 0;
        }));
    }

}
