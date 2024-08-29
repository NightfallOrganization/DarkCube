/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.npc;

public class NPCRemover {
    public static void destoryNPCs(NPCCreator creator){
        creator.zinus.destroy();
        creator.zina.destroy();

        creator.zinus2.destroy();
        creator.zina2.destroy();

        creator.zinus3.destroy();
        creator.zina3.destroy();

        creator.varkas.destroy();
        creator.astaroth.destroy();
    }
}
