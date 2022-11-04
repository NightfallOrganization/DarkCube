package eu.darkcube.system.commandapi.v2.sub;

import java.util.Collection;

public class QuestionAnswerTypeString implements QuestionAnswerType<String> {

	@Override
	public boolean isValidInput(String input) {
		return true;
	}

	@Override
	public String parse(String input) {
		return input;
	}

	@Override
	public Collection<String> getPossibleAnswers() {
		return null;
	}

}
