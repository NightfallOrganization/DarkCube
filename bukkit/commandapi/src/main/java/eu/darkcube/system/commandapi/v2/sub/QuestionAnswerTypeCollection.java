package eu.darkcube.system.commandapi.v2.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QuestionAnswerTypeCollection implements QuestionAnswerType<Collection<String>> {

    private Collection<String> possibleAnswers;
    private boolean allowEmpty = true;

    public QuestionAnswerTypeCollection(Collection<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public QuestionAnswerTypeCollection() {
    }

    public QuestionAnswerTypeCollection disallowEmpty() {
        this.allowEmpty = false;
        return this;
    }

    @Override
    public boolean isValidInput(String input) {
        if (!this.allowEmpty && input.trim().isEmpty()) {
            return false;
        }
        return this.possibleAnswers == null || Arrays.stream(input.split(";")).allMatch(entry -> this.possibleAnswers.contains(entry));
    }

    @Override
    public Collection<String> parse(String input) {
        return new ArrayList<>(Arrays.asList(input.split(";")));
    }

    @Override
    public List<String> getCompletableAnswers() {
        return this.possibleAnswers != null ? new ArrayList<>(this.possibleAnswers) : null;
    }

    @Override
    public Collection<String> getPossibleAnswers() {
        return this.possibleAnswers;
    }

}
