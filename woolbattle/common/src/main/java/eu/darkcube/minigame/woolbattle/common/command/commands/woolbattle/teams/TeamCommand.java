package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.*;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.arguments.ToStringFunction;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.team.SetNameColorCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.teams.team.SetWoolColorCommand;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeamConfiguration;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class TeamCommand extends WoolBattleCommand {
    public static final String TEAM_ARGUMENT_NAME = "team";
    public static final String MAP_SIZE_ARGUMENT_NAME = "mapSize";

    public TeamCommand(@NotNull CommonWoolBattleApi woolbattle) {
        // @formatter:off
        super("team", b ->
                b.then(argument(MAP_SIZE_ARGUMENT_NAME, mapSizeArgument(new MapSizeValidator(woolbattle)))
                        .then(argument(TEAM_ARGUMENT_NAME, teamConfigurationArgument(new MapSizeToString()))
                                .then(new SetNameColorCommand(woolbattle).builder())
                                .then(new SetWoolColorCommand(woolbattle).builder())
                                .executes(ctx -> {
                                    // @formatter:on
                                    var team = teamConfiguration(ctx, TEAM_ARGUMENT_NAME);
                                    ctx.getSource().sendMessage(Messages.TEAM_INFO, team.key(), text(team.nameColor().asHexString(), team.nameColor()), team.woolColor().name(), "???", team.mapSize().teamSize(), "true");
                                    return 0;
                                    // @formatter:off
                                })
                        )
                )
        );
        // @formatter:on
    }

    private record MapSizeValidator(CommonWoolBattleApi woolbattle) implements Predicate<MapSize> {
        @Override
        public boolean test(MapSize mapSize) {
            return woolbattle.teamRegistry().teamConfigurations().stream().map(CommonTeamConfiguration::mapSize).distinct().toList().contains(mapSize);
        }
    }

    private static class MapSizeToString implements ToStringFunction<TeamConfiguration> {
        private final ToStringFunction<TeamConfiguration> parent = ToStringFunction.of(TeamConfiguration::key);

        @Override
        public String[] toString(CommandContext<?> ctx, TeamConfiguration teamConfiguration) throws CommandSyntaxException {
            var mapSize = mapSize(ctx, MAP_SIZE_ARGUMENT_NAME);
            if (mapSize.equals(teamConfiguration.mapSize())) {
                return parent.toString(ctx, teamConfiguration);
            }
            return new String[0];
        }
    }
}
