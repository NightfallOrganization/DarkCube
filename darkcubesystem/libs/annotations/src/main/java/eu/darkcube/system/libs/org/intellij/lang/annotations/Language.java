/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.intellij.lang.annotations;

import eu.darkcube.system.libs.org.jetbrains.annotations.NonNls;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Specifies that an element of the program represents a string that is a source code on a specified language.
 * Code editors may use this annotation to enable syntax highlighting, code completion and other features
 * inside the literals that assigned to the annotated variables, passed as arguments to the annotated parameters,
 * or returned from the annotated methods.
 * <p>
 * This annotation also could be used as a meta-annotation, to define derived annotations for convenience.
 * E.g. the following annotation could be defined to annotate the strings that represent Java methods:
 *
 * <pre>
 *   &#64;Language(value = "JAVA", prefix = "class X{", suffix = "}")
 *   &#64;interface JavaMethod {}
 * </pre>
 * <p>
 * Note that using the derived annotation as meta-annotation is not supported.
 * Meta-annotation works only one level deep.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ METHOD, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE })
public @interface Language {
  /**
   * Language name like "JAVA", "HTML", "XML", "RegExp", etc.
   * The complete list of supported languages is not specified. However, at least the following languages should be
   * recognized:
   * <ul>
   *   <li>"JAVA" - Java programming language</li>
   *   <li>"HTML" - HTML</li>
   *   <li>"XML" - XML</li>
   *   <li>"RegExp" - Regular expression supported by Java {@link java.util.regex.Pattern}</li>
   * </ul>
   */
  @NonNls String value();

  /**
   * A constant prefix that is assumed to be implicitly added before the literal.
   * This helps to apply proper highlighting when the program element represents only a part of the valid program.
   * E.g. if the method parameter accepts a Java method, it could be annotated as
   * {@code void methodProcessor(@Language(value="JAVA", prefix="class X {", suffix="}")}.
   */
  @NonNls String prefix() default "";

  /**
   * A constant suffix that is assumed to be implicitly added after the literal. See {@link #prefix()} for details.
   */
  @NonNls String suffix() default "";
}
