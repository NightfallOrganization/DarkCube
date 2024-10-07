/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity.impl;

import eu.darkcube.minigame.woolbattle.api.world.GameWorld;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomInstance;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

public class MinestomEntityImpl extends Entity implements EntityImpl {
    private final MinestomWoolBattle woolbattle;
    private eu.darkcube.minigame.woolbattle.api.entity.Entity handle;

    public MinestomEntityImpl(@NotNull EntityType entityType, MinestomWoolBattle woolbattle) {
        super(entityType);
        this.woolbattle = woolbattle;
    }

    @Override
    public void update(long time) {
        if (instance == null) return;
        var world = ((MinestomInstance) instance).world();
        if (!(world instanceof GameWorld)) {
            remove();
            return;
        }
        var deathHeight = woolbattle.api().mapManager().deathHeight();
        if (position.y() < deathHeight) {
            remove();
        }
    }

    @Override
    public void handle(eu.darkcube.minigame.woolbattle.api.entity.Entity handle) {
        this.handle = handle;
    }

    @Override
    public @NotNull eu.darkcube.minigame.woolbattle.api.entity.Entity handle() {
        return handle;
    }
}
