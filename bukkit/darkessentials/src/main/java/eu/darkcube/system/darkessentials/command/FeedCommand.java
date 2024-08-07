package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;

public class FeedCommand extends DarkCommand {

    public FeedCommand() {
        super("feed",builder-> builder.executes(context -> {

            context.getSource().asPlayer().setSaturation(30);
            context.getSource().asPlayer().setFoodLevel(30);
            context.getSource().sendMessage(Message.FEED);

            return 0;
        }));
    }

}
