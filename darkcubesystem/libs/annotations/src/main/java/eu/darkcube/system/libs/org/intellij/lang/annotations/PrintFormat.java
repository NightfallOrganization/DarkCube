/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.org.intellij.lang.annotations;

/**
 * Specifies that the method parameter is a printf-style print format pattern,
 * as described in {@link java.util.Formatter}.
 * <p>
 * Code editors that support {@link Pattern} annotation will check
 * the syntax of this value automatically. It could also be especially recognized to
 * check whether the subsequent var-arg arguments match the expected arguments
 * mentioned in the pattern. E. g., consider that the following method is annotated:
 * <pre><code>
 * void myprintf(&#64;PrintFormat String format, Object... args) {...}
 * </code></pre>
 * <p>
 * In this case, code editors might recognize that the following call is erroneous,
 * and issue a warning:
 * <pre><code>
 * myprintf("%d\n", "hello"); // warning: a number expected instead of "hello"
 * </code></pre>
 *
 * @see Pattern
 */
@Pattern(PrintFormatPattern.PRINT_FORMAT)
public @interface PrintFormat {
}

class PrintFormatPattern {

    // %[argument_index$][flags][width][.precision]conversion

    // Expression is taken from java.util.Formatter.fsPattern

    @Language("RegExp")
    private static final String ARG_INDEX = "(?:\\d+\\$)?";
    @Language("RegExp")
    private static final String FLAGS = "(?:[-#+ 0,(<]*)?";
    @Language("RegExp")
    private static final String WIDTH = "(?:\\d+)?";
    @Language("RegExp")
    private static final String PRECISION = "(?:\\.\\d+)?";
    @Language("RegExp")
    private static final String CONVERSION = "(?:[tT])?(?:[a-zA-Z%])";
    @Language("RegExp")
    private static final String TEXT = "[^%]|%%";

    @Language("RegExp")
    static final String PRINT_FORMAT = "(?:" + TEXT + "|" +
            "(?:%" +
            ARG_INDEX +
            FLAGS +
            WIDTH +
            PRECISION +
            CONVERSION + ")" +
            ")*";
}
