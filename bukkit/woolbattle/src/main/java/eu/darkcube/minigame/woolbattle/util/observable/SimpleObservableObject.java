/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.observable;

public abstract class SimpleObservableObject<T> implements ObservableObject<T> {

	private T object;

	public SimpleObservableObject() {
		this(null);
	}

	public SimpleObservableObject(T initial) {
		this.object = initial;
	}

	@Override
	public void setObject(T object) {
		T old = this.object;
		this.object = object;
		onChange(this, old, object);
	}

	@Override
	public T getObject() {
		return object;
	}

	@Override
	public void setSilent(T object) {
		T old = this.object;
		this.object = object;
		onSilentChange(this, old, object);
	}
}
