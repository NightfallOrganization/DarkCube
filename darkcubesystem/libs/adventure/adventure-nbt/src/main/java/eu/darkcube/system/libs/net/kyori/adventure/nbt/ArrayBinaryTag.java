/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * An array binary tag.
 *
 * @since 4.2.0
 */
public interface ArrayBinaryTag extends BinaryTag {
  @Override
  @NotNull BinaryTagType<? extends ArrayBinaryTag> type();
}
