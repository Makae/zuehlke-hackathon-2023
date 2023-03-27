package ch.zuehlke.fullstack.hackathon.statemachine;

import ch.zuehlke.fullstack.hackathon.model.game.GameEvent;
import ch.zuehlke.fullstack.hackathon.model.game.state.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.Set;

import static ch.zuehlke.fullstack.hackathon.model.game.GameEvent.*;
import static ch.zuehlke.fullstack.hackathon.model.game.state.GameState.*;

@Configuration
@EnableStateMachine
@Slf4j
public class StateMachineConfig
        extends EnumStateMachineConfigurerAdapter<GameState, GameEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<GameState, GameEvent> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<GameState, GameEvent> gameState)
            throws Exception {
        gameState
                .withStates()
                .initial(LOBBY)
                .states(Set.of(LOBBY, SETUP, PLAYING, END));
//                .and()
//                .withStates()
//                    .parent(PLAYING)
//                    .initial(PLAYING_PLAYER_1)
//                    .states(Set.of(PLAYING_PLAYER_1, PLAYING_PLAYER_2))
//                    .end(END);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<GameState, GameEvent> transitions)
            throws Exception {
        transitions
                // LOBBY
                .withExternal()
                .source(LOBBY).target(SETUP).event(ALL_PLAYERS_JOINED).action(action())
                .and()

                // SETUP
                .withExternal()
                .source(SETUP).target(PLAYING).event(ALL_BOATS_PLACED)
                .and()

                // PLAYING
                .withExternal()
                .source(PLAYING).target(END).event(ALL_BOATS_DESTROYED).action(action())
                .and()
                .withExternal()
                .source(PLAYING_PLAYER_1).target(PLAYING_PLAYER_2).event(ATTACK)
                .and()
                .withExternal()
                .source(PLAYING_PLAYER_2).target(PLAYING_PLAYER_1).event(ATTACK)
                .and()
                .withExternal()
                .source(PLAYING_PLAYER_1).target(END).event(ALL_BOATS_DESTROYED)
                .and()
                .withExternal()
                .source(PLAYING_PLAYER_2).target(END).event(ALL_BOATS_DESTROYED);
    }

    @Bean
    public StateMachineListener<GameState, GameEvent> listener() {
        return new StateMachineListenerAdapter<GameState, GameEvent>() {
            @Override
            public void stateChanged(State<GameState, GameEvent> from, State<GameState, GameEvent> to) {
                log.info("State change to " + to.getId());
            }
        };
    }

    @Bean
    public Action<GameState, GameEvent> action() {
        return new Action<GameState, GameEvent>() {
            @Override
            public void execute(StateContext<GameState, GameEvent> context) {
                if (context.getTarget().getId() == END) {
                    log.info("Game ending!");
                    //emit winner
                }
                if (context.getSource().getId() == LOBBY && context.getTarget().getId() == SETUP) {
                    log.info("Game transitioning to setup");
                    //emit game config
                }
            }
        };
    }
}
