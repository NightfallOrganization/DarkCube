/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.endphase;

import eu.darkcube.system.miners.gamephase.GamePhase;

public class EndPhase extends GamePhase {
    @Override
    protected void onEnable() {
        listeners.add(new EndPhaseRuler());
    }

    @Override
    protected void onDisable() {

    }
}
