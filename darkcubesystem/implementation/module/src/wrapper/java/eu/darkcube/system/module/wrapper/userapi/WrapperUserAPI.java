/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.wrapper.userapi;

import eu.darkcube.system.common.userapi.CommonUser;
import eu.darkcube.system.common.userapi.CommonUserAPI;

import java.util.UUID;

public class WrapperUserAPI extends CommonUserAPI {

    public WrapperUserAPI() {
    }

    @Override protected CommonUser loadUser(UUID uniqueId) {
        return new WrapperUser(uniqueId);
    }
}
