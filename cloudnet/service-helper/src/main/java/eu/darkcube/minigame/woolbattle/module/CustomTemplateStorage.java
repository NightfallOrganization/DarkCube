/*
 * Copyright (c) 2023-2024. [DarkCube]
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
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomTemplateStorage extends LocalTemplateStorage {

    private final ServiceHelperConfig.CustomStorage customStorage;

    public CustomTemplateStorage(ServiceHelperConfig.CustomStorage customStorage) {
        super(customStorage.path());
        this.customStorage = customStorage;
    }

    @Override public @NotNull String name() {
        return customStorage.name();
    }

    @Override public @NotNull Collection<ServiceTemplate> templates() {
        try {
            return Files.list(customStorage.path()).filter(Files::isDirectory).flatMap(directory -> {
                try {
                    return Files.list(directory);
                } catch (IOException exception) {
                    return null;
                }
            }).filter(Objects::nonNull).filter(Files::isDirectory).map(path -> {
                var relative = customStorage.path().relativize(path);
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
