package ch.zuehlke.fullstack.hackathon.statemachine.config;
import ch.zuehlke.fullstack.hackathon.model.game.GameEvent;
import ch.zuehlke.fullstack.hackathon.model.game.state.GameState;
import ch.zuehlke.fullstack.hackathon.statemachine.action.GameAction;
import ch.zuehlke.fullstack.hackathon.statemachine.action.lobby.AllPlayersJoined;
import ch.zuehlke.fullstack.hackathon.statemachine.action.lobby.PlayerJoinAction;
import ch.zuehlke.fullstack.hackathon.statemachine.action.setup.PlaceBoat;
import ch.zuehlke.fullstack.hackathon.statemachine.guard.GameGuard;
import ch.zuehlke.fullstack.hackathon.statemachine.listener.GameListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.Set;

import static ch.zuehlke.fullstack.hackathon.model.game.GameEvent.*;
import static ch.zuehlke.fullstack.hackathon.model.game.GameEvent.ALL_BOATS_DESTROYED;
import static ch.zuehlke.fullstack.hackathon.model.game.state.GameState.*;
import static ch.zuehlke.fullstack.hackathon.model.game.state.GameState.END;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class StateMachineFactoryConfig extends EnumStateMachineConfigurerAdapter<GameState, GameEvent> {

    @NonNull
    private final PlaceBoat placeBoat;

    @NonNull
    private final GameListener listener;

    @NonNull
    private final PlayerJoinAction playerJoinAction;

    @NonNull
    private final AllPlayersJoined allPlayersJoined;

    @NonNull
    private final GameGuard guard;

    @NonNull
    private final GameAction action;
    @Override
    public void configure(StateMachineStateConfigurer<GameState, GameEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(LOBBY)
                .states(Set.of(LOBBY, SETUP, PLAYING, END))
                .and()
                .withStates()
                .parent(PLAYING)
                .initial(PLAYING_PLAYER_1)
                .states(Set.of(PLAYING_PLAYER_1, PLAYING_PLAYER_2))
                .end(END);
    }
    @Override
    public void configure(StateMachineTransitionConfigurer<GameState, GameEvent> transitions)
            throws Exception {
        transitions
                // LOBBY
                .withInternal()
                .source(LOBBY).event(PLAYER_JOINED).action(playerJoinAction.playerJoin()).guard(guard).and()
                .withExternal()
                .source(LOBBY).target(SETUP).event(ALL_PLAYERS_JOINED).action(allPlayersJoined.allPlayersJoined()).and()

                // SETUP
                .withInternal()
                .source(SETUP).event(PLACE_BOAT).action(placeBoat.placeBoat()).guard(guard).and()
                .withExternal()
                .source(SETUP).target(PLAYING).event(ALL_BOATS_PLACED).action(placeBoat.allBoatsPlaced()).guard(guard).and()

                // PLAYING
                .withExternal()
                .source(PLAYING).target(END).event(ALL_BOATS_DESTROYED).action(action).and()
                .withExternal()
                .source(PLAYING_PLAYER_1).target(PLAYING_PLAYER_2).event(ATTACK).and()
                .withExternal()
                .source(PLAYING_PLAYER_2).target(PLAYING_PLAYER_1).event(ATTACK).and()
                .withExternal()
                .source(PLAYING_PLAYER_1).target(END).event(ALL_BOATS_DESTROYED).and()
                .withExternal()
                .source(PLAYING_PLAYER_2).target(END).event(ALL_BOATS_DESTROYED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<GameState, GameEvent> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener);
    }
}
