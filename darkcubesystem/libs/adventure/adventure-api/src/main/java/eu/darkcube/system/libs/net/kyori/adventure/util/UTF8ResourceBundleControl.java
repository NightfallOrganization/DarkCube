/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A {@link ResourceBundle.Control} that enforces UTF-8 string encoding.
 *
 * <p>See https://stackoverflow.com/a/4660195 for more details.</p>
 *
 * @since 4.0.0
 */
public final class UTF8ResourceBundleControl extends ResourceBundle.Control {
  private static final UTF8ResourceBundleControl INSTANCE = new UTF8ResourceBundleControl();

  /**
   * Gets the shared instance.
   *
   * @return a resource bundle control
   * @since 4.0.0
   */
  public static ResourceBundle.@NotNull Control get() {
    return INSTANCE;
  }

  @Override
  public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IllegalAccessException, InstantiationException, IOException {
    if (format.equals("java.properties")) {
      final String bundle = this.toBundleName(baseName, locale);
      final String resource = this.toResourceName(bundle, "properties");
      InputStream is = null;
      if (reload) {
        final URL url = loader.getResource(resource);
        if (url != null) {
          final URLConnection connection = url.openConnection();
          if (connection != null) {
            connection.setUseCaches(false);
            is = connection.getInputStream();
          }
        }
      } else {
        is = loader.getResourceAsStream(resource);
      }

      if (is != null) {
        try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
          return new PropertyResourceBundle(isr);
        }
      } else {
        return null;
      }
    } else {
      return super.newBundle(baseName, locale, format, loader, reload);
    }
  }
}
