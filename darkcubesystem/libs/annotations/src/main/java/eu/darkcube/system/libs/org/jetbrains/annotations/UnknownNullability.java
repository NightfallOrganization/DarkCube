/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An element annotated with {@link UnknownNullability} claims that no specific nullability
 * should be assumed by static analyzer. The unconditional dereference of the annotated value
 * should not trigger a static analysis warning by default (though static analysis tool may have
 * an option to perform stricter analysis and issue warnings for {@code @UnknownNullability} as well).
 * It's mainly useful at method return types to mark methods that may occasionally
 * return {@code null} but in many cases, user knows that in this particular code path
 * {@code null} is not possible, so producing a warning would be annoying.
 * <p>
 * The {@code UnknownNullability} is the default nullability for unannotated methods, so it's
 * rarely necessary. An explicit annotation may serve to document the method behavior and also
 * to override automatic annotation inference result that could be implemented in some static
 * analysis tools.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE})
public @interface UnknownNullability {
  /**
   * @return textual reason when the annotated value could be null, for documentation purposes.
   */
  @NonNls String value() default "";
}
