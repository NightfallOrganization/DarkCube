/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.org.jetbrains.annotations;

import eu.darkcube.system.libs.org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @since 18.0.0
 */
public final class Debug {

  /**
   * Prohibited default constructor.
   */
  private Debug() {
    throw new AssertionError("Debug should not be instantiated");
  }

  /**
   * Allows to change the presentation of an object in debuggers
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.CLASS)
  public @interface Renderer {
    /**
     * Expression to be evaluated and used as the textual representation of the object.<br>
     * {@code this} refers to the class instance being presented
     */
    @Language(value = "JAVA", prefix = "class Renderer{String $text(){return ", suffix = ";}}")
    @NonNls
    String text() default "";

    /**
     * Expression to be evaluated to obtain an array of object's children.<br>
     * Usually the result is an array of elements in a collection, or an array of entries in a map.<br>
     * {@code this} refers to the class instance being presented
     */
    @Language(value = "JAVA", prefix = "class Renderer{Object[] $childrenArray(){return ", suffix = ";}}")
    @NonNls
    String childrenArray() default "";

    /**
     * Expression to be evaluated to check if the object has any children at all.<br>
     * This should work faster than {@link #childrenArray()} and return boolean.<br>
     * {@code this} refers to the class instance being presented
     */
    @Language(value = "JAVA", prefix = "class Renderer{boolean $hasChildren(){return ", suffix = ";}}")
    @NonNls
    String hasChildren() default "";
  }
}
