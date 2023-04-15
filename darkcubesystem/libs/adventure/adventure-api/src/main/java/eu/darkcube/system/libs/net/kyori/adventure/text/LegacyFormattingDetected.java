/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import eu.darkcube.system.libs.net.kyori.adventure.util.Nag;

final class LegacyFormattingDetected extends Nag {
  private static final long serialVersionUID = -947793022628807411L;

  LegacyFormattingDetected(final Component component) {
    super("Legacy formatting codes have been detected in a component - this is unsupported behaviour. Please refer to the Adventure documentation (https://docs.adventure.kyori.net) for more information. Component: " + component);
  }
}
