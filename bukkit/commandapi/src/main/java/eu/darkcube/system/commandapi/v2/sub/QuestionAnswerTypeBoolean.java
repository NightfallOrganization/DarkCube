package eu.darkcube.system.commandapi.v2.sub;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QuestionAnswerTypeBoolean implements QuestionAnswerType<Boolean> {
    private final String trueString;
    private final String falseString;

    public QuestionAnswerTypeBoolean(String trueString, String falseString) {
        this.trueString = trueString != null ? trueString : "yes";
        this.falseString = falseString != null ? falseString : "no";
    }

    public QuestionAnswerTypeBoolean() {
        this(null, null);
    }

    public String getTrueString() {
        return this.trueString;
    }

    public String getFalseString() {
        return this.falseString;
    }

    @Override
    public boolean isValidInput(String input) {
        return this.trueString.equalsIgnoreCase(input) || this.falseString.equalsIgnoreCase(input);
    }

    @Override
    public Boolean parse(String input) {
        return this.trueString.equalsIgnoreCase(input);
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return this.getCompletableAnswers();
    }

    @Override
    public List<String> getCompletableAnswers() {
        return Arrays.asList(this.trueString, this.falseString);
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return "Invalid boolean!";
    }

}
