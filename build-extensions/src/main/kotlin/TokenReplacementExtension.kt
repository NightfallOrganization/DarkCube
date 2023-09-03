import java.util.function.Supplier
import java.util.regex.Pattern

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

class TokenReplacementExtension {
    val rules: MutableCollection<TokenReplacementRule> = ArrayList()

    fun replace(token: String, value: Any) {
        replace(token) { value }
    }

    fun replace(token: String, value: Supplier<Any>) {
        replaceRegex(Regex.fromLiteral(token), value)
    }

    fun replaceRegex(regex: String, value: Any) {
        replaceRegex(regex) { value }
    }

    fun replaceRegex(regex: String, value: Supplier<Any>) {
        replaceRegex(Regex(regex), value)
    }

    fun replaceRegex(regex: Regex, value: Supplier<Any>) {
        rules.add(TokenReplacementRule(regex.pattern, regex.options, value.get().toString()))
    }
}