/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.npc;

import eu.darkcube.system.woolmania.npc.NPCCreator;

public class NPCRemover {
    public static void destoryNPCs(NPCCreator creator){
        creator.zinus.destroy();
        creator.zina.destroy();
    }
}
