/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.util.Language;

public interface ILanguagedCommandExecutor extends ICommandExecutor {

	Language getLanguage();

	void setLanguage(Language language);

}
