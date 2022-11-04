package eu.darkcube.system.commandapi.v2.sub;

import java.util.Collection;
import java.util.UUID;

public class QuestionAnswerTypeUUID implements QuestionAnswerType<UUID> {

    @Override
    public boolean isValidInput(String input) {
        return parse(input) != null;
    }

    @Override
    public UUID parse(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return null;
    }

    @Override
    public String getInvalidInputMessage(String input) {
        return "Invalid UUID";
    }
}
