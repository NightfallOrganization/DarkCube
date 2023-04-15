/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.internal.properties;

import java.util.function.Function;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Adventure properties.
 *
 * @since 4.10.0
 */
@ApiStatus.Internal
public final class AdventureProperties {
  /**
   * Property for specifying whether debug mode is enabled.
   *
   * @since 4.10.0
   */
  public static final Property<Boolean> DEBUG = property("debug", Boolean::parseBoolean, false);
  /**
   * Property for specifying the default translation locale.
   *
   * @since 4.10.0
   */
  public static final Property<String> DEFAULT_TRANSLATION_LOCALE = property("defaultTranslationLocale", Function.identity(), null);
  /**
   * Property for specifying whether service load failures are fatal.
   *
   * @since 4.10.0
   */
  public static final Property<Boolean> SERVICE_LOAD_FAILURES_ARE_FATAL = property("serviceLoadFailuresAreFatal", Boolean::parseBoolean, Boolean.TRUE);
  /**
   * Property for specifying whether to warn when legacy formatting is detected.
   *
   * @since 4.10.0
   */
  public static final Property<Boolean> TEXT_WARN_WHEN_LEGACY_FORMATTING_DETECTED = property("text.warnWhenLegacyFormattingDetected", Boolean::parseBoolean, Boolean.FALSE);

  private AdventureProperties() {
  }

  /**
   * Creates a new property.
   *
   * @param name the property name
   * @param parser the value parser
   * @param defaultValue the default value
   * @param <T> the value type
   * @return a property
   * @since 4.10.0
   */
  public static <T> @NotNull Property<T> property(final @NotNull String name, final @NotNull Function<String, T> parser, final @Nullable T defaultValue) {
    return AdventurePropertiesImpl.property(name, parser, defaultValue);
  }

  /**
   * A property.
   *
   * @param <T> the value type
   * @since 4.10.0
   */
  @ApiStatus.Internal
  @ApiStatus.NonExtendable
  public interface Property<T> {
    /**
     * Gets the value.
     *
     * @return the value
     * @since 4.10.0
     */
    @Nullable T value();
  }
}
