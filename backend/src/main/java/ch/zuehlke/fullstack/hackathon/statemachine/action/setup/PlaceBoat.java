package ch.zuehlke.fullstack.hackathon.statemachine.action.setup;

import ch.zuehlke.fullstack.hackathon.model.game.GameEvent;
import ch.zuehlke.fullstack.hackathon.model.game.state.GameState;
import ch.zuehlke.fullstack.hackathon.service.orchestrator.SetupOrchestrator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceBoat {

    @NonNull
    private final SetupOrchestrator setupOrchestrator;

    public Action<GameState, GameEvent> placeBoat() {
        return context -> {
//            var stateMachine = context.getStateMachine();
//
//            MessageHeaders messageHeaders = context.getMessageHeaders();
//            PlaceBoatAction action = (PlaceBoatAction) messageHeaders.get(Header.PLACE_BOAT.name());
//            Game storedGame = (Game) stateMachine.getExtendedState().getVariables().get(Variable.GAME_ID);
//
//            Game updatedGame = setupOrchestrator.placeBoat(storedGame.gameId(), action.playerId(), action.coordinate());
//
//            stateMachine.getExtendedState().getVariables().put(Variable.GAME_ID, updatedGame);
//
//            // check if all boats placed
//            if (updatedGame.allBoatsPlaced()) {
//                stateMachine.sendEvent(ALL_BOATS_PLACED);
//            }
        };
    }

    public Action<GameState, GameEvent> allBoatsPlaced() {
        return context -> {
//            var stateMachine = context.getStateMachine();
//
//            MessageHeaders messageHeaders = context.getMessageHeaders();
//            PlaceBoatAction action = (PlaceBoatAction) messageHeaders.get(Header.PLACE_BOAT.name());
//            Game storedGame = (Game) stateMachine.getExtendedState().getVariables().get(Variable.GAME_ID);
//
//            Game updatedGame = setupOrchestrator.placeBoat(storedGame.gameId(), action.playerId(), action.coordinate());
//
//            stateMachine.getExtendedState().getVariables().put(Variable.GAME_ID, updatedGame);
//
//            // check if all boats placed
//            if (updatedGame.allBoatsPlaced()) {
//                stateMachine.sendEvent(ALL_BOATS_PLACED);
//            }
        };
    }

}
