/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.VisibleForTesting;

final class AdventurePropertiesImpl {
  private static final String FILESYSTEM_DIRECTORY_NAME = "config";
  private static final String FILESYSTEM_FILE_NAME = "adventure.properties";
  private static final Properties PROPERTIES = new Properties();

  static {
    final Path path = Optional.ofNullable(System.getProperty(systemPropertyName("config")))
      .map(Paths::get)
      .orElseGet(() -> Paths.get(FILESYSTEM_DIRECTORY_NAME, FILESYSTEM_FILE_NAME));
    if (Files.isRegularFile(path)) {
      try (final InputStream is = Files.newInputStream(path)) {
        PROPERTIES.load(is);
      } catch (final IOException e) {
        // Well, that's awkward.
        print(e);
      }
    }
  }

  @SuppressWarnings("CatchAndPrintStacktrace") // we don't have any better options on Java 8
  private static void print(final Throwable ex) {
    ex.printStackTrace();
  }

  private AdventurePropertiesImpl() {
  }

  @VisibleForTesting
  static @NotNull String systemPropertyName(final String name) {
    return String.join(".", "net", "kyori", "adventure", name);
  }

  static <T> AdventureProperties.@NotNull Property<T> property(final @NotNull String name, final @NotNull Function<String, T> parser, final @Nullable T defaultValue) {
    return new PropertyImpl<>(name, parser, defaultValue);
  }

  private static final class PropertyImpl<T> implements AdventureProperties.Property<T> {
    private final String name;
    private final Function<String, T> parser;
    private final @Nullable T defaultValue;
    private boolean valueCalculated;
    private @Nullable T value;

    PropertyImpl(final @NotNull String name, final @NotNull Function<String, T> parser, final @Nullable T defaultValue) {
      this.name = name;
      this.parser = parser;
      this.defaultValue = defaultValue;
    }

    @Override
    public @Nullable T value() {
      if (!this.valueCalculated) {
        final String property = systemPropertyName(this.name);
        final String value = System.getProperty(property, PROPERTIES.getProperty(this.name));
        if (value != null) {
          this.value = this.parser.apply(value);
        }
        if (this.value == null) {
          this.value = this.defaultValue;
        }
        this.valueCalculated = true;
      }
      return this.value;
    }

    @Override
    public boolean equals(final @Nullable Object that) {
      return this == that;
    }

    @Override
    public int hashCode() {
      return this.name.hashCode();
    }
  }
}
