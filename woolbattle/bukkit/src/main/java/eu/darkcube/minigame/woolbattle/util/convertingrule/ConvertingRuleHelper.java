/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.convertingrule;

import eu.darkcube.minigame.woolbattle.util.Arrays;

public class ConvertingRuleHelper {

    public static void load() {
        // Create Array converting rules
        Arrays.addConvertingRule(new ConvertingRuleLanguage());
        Arrays.addConvertingRule(new ConvertingRuleChatColor());
        Arrays.addConvertingRule(new ConvertingRuleDyeColor());
        Arrays.addConvertingRule(new ConvertingRuleTeam());
        Arrays.addConvertingRule(new ConvertingRuleMap());
    }
}
