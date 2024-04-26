/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.wrapper.userapi;

import java.util.UUID;

import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserAPI;

public class WrapperUserAPI extends CommonUserAPI {
    @Override
    protected CommonUser loadUser(UUID uniqueId) {
        return new WrapperUser(uniqueId);
    }
}
