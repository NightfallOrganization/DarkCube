/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;

public class DayCommand extends DarkCommand {

    public DayCommand() {
        super("day", new String[0], builder -> builder.executes(context -> {

            context.getSource().getWorld().setTime(1000);
            context.getSource().sendMessage(Message.SET_DAY);

            return 0;
        }));
    }

}
