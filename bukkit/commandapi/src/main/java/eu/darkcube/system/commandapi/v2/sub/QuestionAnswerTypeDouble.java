package eu.darkcube.system.commandapi.v2.sub;

import java.util.Collection;

import com.google.common.primitives.Doubles;

public class QuestionAnswerTypeDouble implements QuestionAnswerType<Double> {

    @Override
    public boolean isValidInput(String input) {
        return Doubles.tryParse(input) != null;
    }

    @Override
    public Double parse(String input) {
        return Double.parseDouble(input);
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return null;
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return "Invalid double!";
    }

}
