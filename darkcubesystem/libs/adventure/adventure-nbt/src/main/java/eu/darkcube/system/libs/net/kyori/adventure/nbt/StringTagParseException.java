/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.io.IOException;

/**
 * An exception thrown when parsing a string tag.
 */
class StringTagParseException extends IOException {
  private static final long serialVersionUID = -3001637554903912905l;
  private final CharSequence buffer;
  private final int position;

  StringTagParseException(final String message, final CharSequence buffer, final int position) {
    super(message);
    this.buffer = buffer;
    this.position = position;
  }

  // TODO: Provide more specific position information

  @Override
  public String getMessage() {
    return super.getMessage() + "(at position " + this.position + ")";
  }
}
