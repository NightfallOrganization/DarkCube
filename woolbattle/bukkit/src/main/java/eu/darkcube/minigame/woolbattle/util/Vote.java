/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import java.math.BigInteger;
import java.util.Collection;

public class Vote<T> {

    public final long time;
    public final T vote;

    public Vote(long time, T vote) {
        this.time = time;
        this.vote = vote;
    }

    public static <T> T calculateWinner(Collection<Vote<T>> list, Collection<? extends T> possibilities, T defaultObject) {
        long votes = 0;
        T object = null;
        BigInteger time = BigInteger.valueOf(0);
        for (T possibility : possibilities) {
            long voteCount = list.stream().filter(vote -> vote.vote.equals(possibility)).count();
            if (voteCount == 0) continue;
            BigInteger voteTime = BigInteger.valueOf(Long.MAX_VALUE);
            for (Vote<T> vote : list) {
                if (vote.vote != possibility) continue;
                voteTime = voteTime.min(BigInteger.valueOf(vote.time));
            }
            if (voteCount > votes) {
                object = possibility;
                votes = voteCount;
                time = voteTime;
            } else if (voteCount == votes) {
                if (time.compareTo(voteTime) > 0) {
                    object = possibility;
                    time = voteTime;
                }
            }
        }
        if (object == null) object = defaultObject;
        if (object == null) object = possibilities.stream().unordered().findAny().orElse(null);
        return object;
    }
}
