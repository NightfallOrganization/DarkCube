import java.util.function.Supplier
import java.util.regex.Pattern

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

class TokenReplacementExtension {
    internal val entries: MutableList<Entry> = ArrayList()

    fun add(regex: String, replacement: String) {
        add(regex) { replacement }
    }

    fun add(regex: String, replacement: Supplier<String>) {
        add(Pattern.compile(regex), replacement)
    }

    fun add(regex: Pattern, replacement: Supplier<String>) {
        entries.add(Entry(regex, replacement))
    }

    internal class Entry(internal val regex: Pattern, internal val replacement: Supplier<String>)
}