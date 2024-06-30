package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.UserAPI;

public class NightCommand extends DarkCommand {

    public NightCommand() {
        super("night", new String[0], builder-> builder.executes(context -> {

            context.getSource().getWorld().setTime(13000);
            context.getSource().sendMessage(Message.SET_NIGHT);

            return 0;
        }));
    }

}
