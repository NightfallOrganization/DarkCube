/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.module;

import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.node.template.LocalTemplateStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public final class WoolBattleTemplateStorage extends LocalTemplateStorage {

    public static final String WOOLBATTLE_TEMPLATE_STORAGE = "woolbattle";
    private final Path storageDirectory;

    public WoolBattleTemplateStorage(Path storageDirectory) {
        super(storageDirectory);
        this.storageDirectory = storageDirectory;
    }

    @Override public @NotNull String name() {
        return WOOLBATTLE_TEMPLATE_STORAGE;
    }

    @Override public @NotNull Collection<ServiceTemplate> templates() {
        try {
            return Files.list(this.storageDirectory).filter(Files::isDirectory).flatMap(directory -> {
                try {
                    return Files.list(directory);
                } catch (IOException exception) {
                    return null;
                }
            }).filter(Objects::nonNull).filter(Files::isDirectory).map(path -> {
                var relative = this.storageDirectory.relativize(path);
                return ServiceTemplate
                        .builder()
                        .storage(name())
                        .prefix(relative.getName(0).toString())
                        .name(relative.getName(1).toString())
                        .build();
            }).collect(Collectors.toSet());
        } catch (IOException exception) {
            return Collections.emptyList();
        }
    }
}
