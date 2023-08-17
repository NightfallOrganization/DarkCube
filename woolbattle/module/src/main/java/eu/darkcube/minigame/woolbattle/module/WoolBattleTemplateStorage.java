/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.module;

import eu.cloudnetservice.node.template.LocalTemplateStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class WoolBattleTemplateStorage extends LocalTemplateStorage {

    public static final String WOOLBATTLE_TEMPLATE_STORAGE = "woolbattle";

    public WoolBattleTemplateStorage(Path storageDirectory) {
        super(storageDirectory);
    }

    @Override public @NotNull String name() {
        return WOOLBATTLE_TEMPLATE_STORAGE;
    }
}
