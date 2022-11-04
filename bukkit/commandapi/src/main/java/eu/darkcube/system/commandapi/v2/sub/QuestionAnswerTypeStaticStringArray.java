package eu.darkcube.system.commandapi.v2.sub;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class QuestionAnswerTypeStaticStringArray implements QuestionAnswerType<String> {

    private final String[] allowedValues;
    private final boolean ignoreCase;

    public QuestionAnswerTypeStaticStringArray(String[] allowedValues, boolean ignoreCase) {
        this.allowedValues = allowedValues;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean isValidInput(String input) {
        return Arrays.stream(this.allowedValues)
                .anyMatch(value -> (this.ignoreCase && value.equalsIgnoreCase(input)) || value.equals(input));
    }

    @Override
    public String parse(String input) {
        return Arrays.stream(this.allowedValues).filter(value -> value.equalsIgnoreCase(input)).findFirst().get();
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return null;
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return Collections.singletonList(this.allowedValues[0]);
    }
}