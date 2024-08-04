package eu.darkcube.minigame.woolbattle.common.game.lobby;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import eu.darkcube.minigame.woolbattle.api.event.game.lobby.LobbyTimerUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class CommonLobbyTimer {
    private final CommonLobby lobby;
    private final Duration quickStart = Duration.ofSeconds(10);
    private final Duration maxTimer = Duration.ofSeconds(60);
    private TemporalUnit tick;
    private SchedulerTask task;
    private @Nullable Duration override = null;
    private Duration timer = maxTimer;

    public CommonLobbyTimer(CommonLobby lobby) {
        this.lobby = lobby;
    }

    public void start() {
        this.tick = lobby.game().woolbattle().tickUnit();
        task = lobby.game().scheduler().submit(() -> {
            var onlineCount = lobby.game().users().size();
            if (onlineCount >= lobby.minPlayerCount()) {
                if (override != null) {
                    setOverride(override.minus(1, tick));
                } else {
                    if (onlineCount == lobby.maxPlayerCount() && timer.compareTo(quickStart) > 0) {
                        setTimer(quickStart);
                    } else {
                        setTimer(timer.minus(1, tick));
                    }
                }
            } else {
                reset();
            }
            return TaskSchedule.nextTick();
        });
    }

    public void stop() {
        task.cancel();
    }

    public void setOverride(@Nullable Duration override) {
        if (override != null && override.isNegative()) override = Duration.ZERO;
        this.override = override;
        if (override != null) setTimer(override);
    }

    public void setTimer(@NotNull Duration timer) {
        if (timer.isNegative()) timer = Duration.ZERO;
        this.timer = timer;
        lobby.game().woolbattle().eventManager().call(new LobbyTimerUpdateEvent(lobby.game(), timer));
        lobby.updateTimer();
    }

    public void reset() {
        override = null;
        setTimer(maxTimer);
    }

    public Duration timer() {
        return timer;
    }

    public Duration maxTimer() {
        return maxTimer;
    }

    public Duration quickStart() {
        return quickStart;
    }

    public Duration timeRemaining() {
        return timer;
    }
}
