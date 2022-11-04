package eu.darkcube.system.commandapi.v2.sub;

import java.util.Collection;
import java.util.List;

public interface QuestionAnswerType<T> {

	boolean isValidInput(String input);

	T parse(String input);

	/**
	 * @return null if there are infinite possible numbers
	 */
	Collection<String> getPossibleAnswers();

	default List<String> getCompletableAnswers() {
		return null;
	}

	default String getRecommendation() {
		return null;
	}

	default String getPossibleAnswersAsString() {
		Collection<String> possibleAnswers = this.getPossibleAnswers();
		return possibleAnswers != null ? String.join(", ", possibleAnswers) : null;
	}

	default String getInvalidInputMessage(String input) {
		return "Invalid input! " + input;
	}

}