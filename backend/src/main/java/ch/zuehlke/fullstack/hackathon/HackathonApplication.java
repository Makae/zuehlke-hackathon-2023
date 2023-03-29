package ch.zuehlke.fullstack.hackathon;

import ch.zuehlke.fullstack.hackathon.model.game.GameEvent;
import ch.zuehlke.fullstack.hackathon.model.game.state.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Mono;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class HackathonApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HackathonApplication.class, args);
    }

    @Autowired
    private StateMachineFactory<GameState, GameEvent> factory;
    //private StateMachine<GameState, GameEvent> stateMachine;


    @Override
    public void run(String... args) {
        // make instances of state machine (state machine factory?)

        StateMachine<GameState,GameEvent> stateMachine = factory.getStateMachine("machine1");

        log.info("----" + stateMachine);
        stateMachine.start();
        stateMachine1(stateMachine);

        StateMachine<GameState,GameEvent> stateMachine2 = factory.getStateMachine("machine2");
        Mono.just(stateMachine1(stateMachine)).zipWith(Mono.just(stateMachine2(stateMachine2))).block();

        stateMachine2(stateMachine2);
        log.info("----" + stateMachine2);
    }

    private boolean stateMachine1(StateMachine<GameState, GameEvent> stateMachine) {
        stateMachine.start();
        //private void stateMachine1(StateMachine<GameState, GameEvent> stateMachine) {
        stateMachine.sendEvent(GameEvent.ALL_PLAYERS_JOINED);
        stateMachine.sendEvent(GameEvent.ALL_BOATS_PLACED);
        stateMachine.sendEvent(GameEvent.ATTACK);
        stateMachine.sendEvent(GameEvent.ATTACK);
        stateMachine.sendEvent(GameEvent.ALL_BOATS_DESTROYED);
        return true;
    }

    private boolean stateMachine2(StateMachine<GameState, GameEvent> stateMachine) {
        stateMachine.start();
        log.info("in stateMachine2");
        stateMachine.sendEvent(GameEvent.ALL_PLAYERS_JOINED);
        stateMachine.sendEvent(GameEvent.ALL_BOATS_PLACED);
        stateMachine.sendEvent(GameEvent.ATTACK);
        stateMachine.sendEvent(GameEvent.ATTACK);
        stateMachine.sendEvent(GameEvent.ALL_BOATS_DESTROYED);
        return true;
    }

}
