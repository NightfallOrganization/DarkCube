/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums.hall;

public enum HallValue {

    HALL_VALUE_1(1),
    HALL_VALUE_2(2),
    HALL_VALUE_3(3),
    HALL_VALUE_4(4),
    HALL_VALUE_5(5),
    HALL_VALUE_6(6),
    HALL_VALUE_7(7),
    HALL_VALUE_8(8),
    HALL_VALUE_9(9),
    HALL_VALUE_10(10),
    HALL_VALUE_11(11),
    HALL_VALUE_12(12),
    HALL_VALUE_13(13),
    HALL_VALUE_14(14),
    HALL_VALUE_15(15),
    HALL_VALUE_16(16),

    ;

    private final int value;

    HallValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
