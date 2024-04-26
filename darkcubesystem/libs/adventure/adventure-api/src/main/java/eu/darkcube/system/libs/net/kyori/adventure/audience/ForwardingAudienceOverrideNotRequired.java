/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.audience;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

/**
 * Marks a method in {@link Audience} that does not need to be overridden in {@link ForwardingAudience} or {@link ForwardingAudience.Single}.
 */
@ApiStatus.Internal
@Retention(RetentionPolicy.RUNTIME)
@interface ForwardingAudienceOverrideNotRequired {
}
