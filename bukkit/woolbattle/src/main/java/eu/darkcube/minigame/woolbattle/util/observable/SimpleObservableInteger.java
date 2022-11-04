package eu.darkcube.minigame.woolbattle.util.observable;

public abstract class SimpleObservableInteger extends SimpleObservableObject<Integer> implements ObservableInteger {

	public SimpleObservableInteger() {
		super();
	}

	public SimpleObservableInteger(int initial) {
		super(initial);
	}

}
