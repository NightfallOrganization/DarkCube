/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.intellij.lang.annotations;

import java.lang.annotation.*;

/**
 * Specifies the replacement value for non-constant variables and method return values.
 * This may help static analyzers to properly parse the concatenation of several values
 * which is used in @{@link Language} or {@link Pattern} context.
 * <p>
 * Example:
 * <pre>
 * &#64;Subst("Tahoma")
 * final String font = new JLabel().getFont().getName();
 *
 * &#64;Language("HTML")
 * String message = "&lt;html&gt;&lt;span style='font: " + font + "; font-size:smaller'&gt;"
 *   + ... + "&lt;/span&gt;&lt;/html&gt;";
 * </pre>
 * <p>
 * Here the parser assumes that when {@code font} appears in the concatenation its value is {@code "Tahoma"},
 * so it can continue parsing the concatenation.
 * </p>
 *
 * @see Language
 * @see Pattern
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
public @interface Subst {
    String value();
}
