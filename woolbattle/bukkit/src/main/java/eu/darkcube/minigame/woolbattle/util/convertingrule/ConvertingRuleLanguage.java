/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.convertingrule;

import eu.darkcube.minigame.woolbattle.util.Arrays.ConvertingRule;
import eu.darkcube.system.util.Language;

public class ConvertingRuleLanguage extends ConvertingRule<Language> {

	@Override
	public Class<Language> getConvertingClass() {
		return Language.class;
	}

	@Override
	public String convert(Language object) {
		return object.getLocale().getDisplayName(object.getLocale()).toLowerCase();
	}

}
