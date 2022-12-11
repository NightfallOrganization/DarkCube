/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.observable;

public abstract class SimpleObservableInteger extends SimpleObservableObject<Integer> implements ObservableInteger {

	public SimpleObservableInteger() {
		super();
	}

	public SimpleObservableInteger(int initial) {
		super(initial);
	}

}
