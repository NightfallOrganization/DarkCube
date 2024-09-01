/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import eu.darkcube.system.woolmania.util.area.WoolAreas;

public class ClearHallsCommand extends WoolManiaCommand {

    public ClearHallsCommand() {
        super("clearhalls",builder -> builder.executes(context -> {
            WoolAreas.clearWoolAreas();
            return 0;
        }));
    }

}
