/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import java.nio.charset.StandardCharsets

allprojects {
    repositories {
        maven {
            name = "DarkCube"
            credentials(PasswordCredentials::class)
            url = uri("https://nexus.darkcube.eu/repository/darkcube-group/")
        }
    }
    pluginManager.withPlugin("java") {
        val extension: JavaPluginExtension = extensions.getByType(JavaPluginExtension::class)
        extension.toolchain.languageVersion = JavaLanguageVersion.of(8)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = StandardCharsets.UTF_8.name()
        options.isDeprecation = true
//        options.isFork = true
    }
    tasks.withType<Javadoc>().configureEach {
        options.encoding = StandardCharsets.UTF_8.name()
    }
}
