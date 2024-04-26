/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

/**
 * A result for {@link Component#replaceText(TextReplacementConfig)}  pattern-based replacements}.
 *
 * @since 4.0.0
 */
public enum PatternReplacementResult {
  /**
   * Replace the current match.
   *
   * @since 4.0.0
   */
  REPLACE,
  /**
   * Skip the current match, but continue searching for others.
   *
   * @since 4.0.0
   */
  CONTINUE,
  /**
   * Stop matching.
   *
   * @since 4.0.0
   */
  STOP;
}
