package eu.darkcube.minigame.woolbattle.common.vote;

import java.time.Instant;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.vote.Vote;

public record CommonVote<Type>(CommonPoll<Type> poll, WBUser user, Instant time, Type vote) implements Vote<Type> {
}
