/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import org.gradle.api.tasks.Internal
import java.io.Serializable

class TokenReplacementRule(val regex: String, val regexOptions: Set<RegexOption>, val value: String) : Serializable {
    @Internal
    @Transient
    private var compiledRegex: Regex? = null

    companion object {
        private const val serialVersionUID = 20180617104400L
    }

    fun getCompiledRegex(): Regex {
        if (compiledRegex == null) compiledRegex = Regex(pattern = regex, options = regexOptions)
        return compiledRegex!!
    }
}