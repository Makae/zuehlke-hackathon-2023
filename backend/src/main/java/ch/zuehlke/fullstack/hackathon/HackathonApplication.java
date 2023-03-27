package ch.zuehlke.fullstack.hackathon;

import ch.zuehlke.fullstack.hackathon.model.game.GameEvent;
import ch.zuehlke.fullstack.hackathon.model.game.state.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import static ch.zuehlke.fullstack.hackathon.model.game.GameEvent.*;

@SpringBootApplication
public class HackathonApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HackathonApplication.class, args);
    }

    @Autowired
    private StateMachine<GameState, GameEvent> stateMachine;

    @Override
    public void run(String... args) throws Exception {
        stateMachine.sendEvent(PLAYER_JOINED);
        stateMachine.sendEvent(ALL_PLAYERS_JOINED);

//        stateMachine.sendEvent(ATTACK);
//        stateMachine.sendEvent(ATTACK);
//        stateMachine.sendEvent(ATTACK);
//        stateMachine.sendEvent(ATTACK);

        stateMachine.sendEvent(ALL_BOATS_PLACED);

        stateMachine.sendEvent(ALL_BOATS_DESTROYED);
    }
}
