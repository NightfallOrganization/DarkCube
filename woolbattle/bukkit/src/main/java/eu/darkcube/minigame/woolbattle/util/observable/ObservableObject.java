/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.observable;

public interface ObservableObject<T> {

	void setObject(T t);
	T getObject();
	void setSilent(T t);
	void onChange(ObservableObject<T> instance, T oldValue, T newValue);
	void onSilentChange(ObservableObject<T> instance, T oldValue, T newValue);
	
}
