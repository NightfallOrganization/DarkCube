/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.title;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

/**
 * A part of a title.
 *
 * @param <T> the type of the content of the part
 * @see Audience#sendTitlePart(TitlePart, Object)
 * @since 4.9.0
 */
@ApiStatus.NonExtendable
public interface TitlePart<T> {
  /**
   * The title part of a title.
   *
   * @since 4.9.0
   */
  TitlePart<Component> TITLE = new TitlePart<Component>() {
    @Override
    public String toString() {
      return "TitlePart.TITLE";
    }
  };

  /**
   * The subtitle part of a title.
   *
   * @since 4.9.0
   */
  TitlePart<Component> SUBTITLE = new TitlePart<Component>() {
    @Override
    public String toString() {
      return "TitlePart.SUBTITLE";
    }
  };

  /**
   * The times part of a title.
   *
   * @since 4.9.0
   */
  TitlePart<Title.Times> TIMES = new TitlePart<Title.Times>() {
    @Override
    public String toString() {
      return "TitlePart.TIMES";
    }
  };
}
