package eu.darkcube.minigame.woolbattle.util.observable;

public interface ObservableObject<T> {

	void setObject(T t);
	T getObject();
	void setSilent(T t);
	void onChange(ObservableObject<T> instance, T oldValue, T newValue);
	void onSilentChange(ObservableObject<T> instance, T oldValue, T newValue);
	
}
