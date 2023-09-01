/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@ApiStatus.Internal record VersionHolder() {
    private static final VarHandle INSTANCE;
    @SuppressWarnings("unused") // We use the VarHandle to assign this to prevent multiple UserAPIs
    private static volatile Version instance;

    static {
        try {
            INSTANCE = MethodHandles.lookup().findStaticVarHandle(VersionHolder.class, "instance", Version.class);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new AssertionError(exception);
        }
    }

    public VersionHolder {
        throw new AssertionError();
    }

    static void instance(Version instance) {
        if (!INSTANCE.compareAndSet(null, instance)) {
            throw new AssertionError("UserAPI may only be initialized once");
        }
    }

    static Version instance() {
        Version instance = VersionHolder.instance;
        if (instance == null) throw new AssertionError("UserAPI not initialized");
        return instance;
    }
}
