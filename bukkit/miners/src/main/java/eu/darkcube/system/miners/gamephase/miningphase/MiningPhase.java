/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.miningphase;

import eu.darkcube.system.miners.gamephase.GamePhase;

public class MiningPhase extends GamePhase {
    public MiningPhase() {
        listeners.add(new MiningPhaseRuler());
        listeners.add(new MiningPhaseBlockBreak());
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
