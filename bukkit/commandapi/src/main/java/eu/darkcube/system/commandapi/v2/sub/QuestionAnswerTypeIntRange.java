package eu.darkcube.system.commandapi.v2.sub;

public class QuestionAnswerTypeIntRange extends QuestionAnswerTypeInt {
	private final int minValue;
	private final int maxValue;

	public QuestionAnswerTypeIntRange(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public boolean isValidInput(String input) {
		try {
			int value = Integer.parseInt(input);
			return value >= this.minValue && value <= this.maxValue;
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	@Override
	public Integer parse(String input) {
		return Integer.parseInt(input);
	}

	@Override
	public String getPossibleAnswersAsString() {
		return "[" + this.minValue + ", " + this.maxValue + "]";
	}

	@Override
	public String getInvalidInputMessage(String input) {
		return "Invalid int. From %min% to %max%!".replace("%min%", String.valueOf(this.minValue))
				.replace("%max%", String.valueOf(this.maxValue));
	}

}
