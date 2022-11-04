package eu.darkcube.system.commandapi.v2.sub;

import java.util.Collection;

import com.google.common.primitives.Ints;

public class QuestionAnswerTypeInt implements QuestionAnswerType<Integer> {

    @Override
    public boolean isValidInput(String input) {
        return Ints.tryParse(input) != null;
    }

    @Override
    public Integer parse(String input) {
        return Integer.parseInt(input);
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return null;
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return "Invalid integer!";
    }

}
