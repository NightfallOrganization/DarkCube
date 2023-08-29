/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

record UserAPIHolder() {
    private static final VarHandle INSTANCE;
    @SuppressWarnings("unused") // We use the VarHandle to assign this to prevent multiple UserAPIs
    private static volatile UserAPI instance;

    static {
        try {
            INSTANCE = MethodHandles.lookup().findStaticVarHandle(UserAPIHolder.class, "instance", UserAPI.class);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new AssertionError(exception);
        }
    }

    UserAPIHolder {
        throw new AssertionError();
    }

    static void instance(UserAPI instance) {
        if (!INSTANCE.compareAndSet(null, instance)) {
            throw new AssertionError("UserAPI may only be initialized once");
        }
    }

    static UserAPI instance() {
        UserAPI instance = UserAPIHolder.instance;
        if (instance == null) throw new AssertionError("UserAPI not initialized");
        return instance;
    }
}
