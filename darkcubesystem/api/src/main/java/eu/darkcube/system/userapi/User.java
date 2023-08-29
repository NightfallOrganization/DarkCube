/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;

public interface User extends ForwardingAudience.Single, ICommandExecutor, UserData {

    /**
     * @return the user's data
     */
    @Api UserData userData();

}
