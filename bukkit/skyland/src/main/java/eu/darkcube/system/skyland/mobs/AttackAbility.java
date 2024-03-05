/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.mobs;

public class AttackAbility extends AbstractMobAbility {

    public AttackAbility(){
        super(60);
    }

    @Override
    public void trigger(CustomMob customMob) {
        if (isCooldownReady(customMob.getMob(), true)){;
            System.out.println("triggered Attack");
        }
    }

    @Override public Class<? extends MonsterAbility> getCurrentClass() {
        return AttackAbility.class;
    }
}
