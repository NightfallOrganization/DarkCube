/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.intellij.lang.annotations;

import eu.darkcube.system.libs.org.jetbrains.annotations.NonNls;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that an element of the program represents a string that must completely match given regular expression.
 * Code editors may use this annotation to report errors if the literals that assigned to the annotated variables,
 * passed as arguments to the annotated parameters, or returned from the annotated methods, do not match the supplied
 * pattern.
 * <p>
 * This annotation also could be used as a meta-annotation, to define other annotations for convenience.
 * E.g. the following annotation could be defined to annotate the strings that represent UUIDs:
 *
 * <pre>
 * &#64;Pattern("[\\dA-Fa-f]{8}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{12}")
 * &#64;interface UUID {}
 * </pre>
 * <p>
 * Note that using the derived annotation as meta-annotation is not supported.
 * Meta-annotation works only one level deep.
 *
 * @see RegExp
 */
@Retention(RetentionPolicy.CLASS)
@Target({ METHOD, FIELD, PARAMETER, LOCAL_VARIABLE, ANNOTATION_TYPE })
public @interface Pattern {
    /**
     * A regular expression that matches all the valid string literals that assigned to the annotated variables,
     * passed as arguments to the annotated parameters, or returned from the annotated methods.
     */
    @Language("RegExp")
    @NonNls
    String value();
}
