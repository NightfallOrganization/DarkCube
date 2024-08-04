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

    private Duration timerRequest;
    private Duration overrideRequest;

    public CommonLobbyTimer(CommonLobby lobby) {
        this.lobby = lobby;
    }

    public void start() {
        this.tick = this.lobby.game().woolbattle().tickUnit();

        this.task = this.lobby.game().scheduler().submit(() -> {
            var onlineCount = lobby.game().users().size();
            var enoughPlayers = onlineCount >= lobby.minPlayerCount();

            if (enoughPlayers) {
                if (overrideRequest != null) {
                    newOverride(overrideRequest);
                    overrideRequest = null;
                    timerRequest = null;
                } else if (timerRequest != null) {
                    newTimer(timerRequest);
                    timerRequest = null;
                } else if (override != null) {
                    newOverride(override.minus(1, tick));
                } else {
                    if (onlineCount == lobby.maxPlayerCount() && timer.compareTo(quickStart) > 0) {
                        newTimer(quickStart);
                    } else {
                        newTimer(timer.minus(1, tick));
                    }
                }
            } else {
                override = null;
                overrideRequest = null;
                timerRequest = null;
                newTimer(maxTimer);
            }
            return TaskSchedule.nextTick();
        });
    }

    public void stop() {
        task.cancel();
    }

    public void timer(@NotNull Duration timer) {
        this.timerRequest = timer;
    }

    public void overrideTimer(@NotNull Duration timer) {
        this.overrideRequest = timer;
    }

    private void newOverride(@Nullable Duration override) {
        this.override = override != null && override.isNegative() ? Duration.ZERO : override;
        if (this.override != null) newTimer(this.override);
    }

    private void newTimer(@NotNull Duration timer) {
        this.timer = timer.isNegative() ? Duration.ZERO : timer;
        lobby.game().woolbattle().eventManager().call(new LobbyTimerUpdateEvent(lobby.game(), this.timer));
        lobby.updateTimer();
        if (this.timer.isZero()) {
            lobby.game().enableNextPhase();
        }
    }

    public void reset() {
        override = null;
        overrideRequest = null;
        timerRequest = maxTimer;
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
