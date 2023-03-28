import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { GameEventService } from "../../services/game-event.service";
import { map, Subject, Subscription, switchMap } from "rxjs";
import { UUID } from "../../model/uuid";
import { AttackStatus, GamePlayingAction, PlayingEvent } from "../../model/game/playing/events";
import { CellClickEvent, MapComponent, MapValue } from "../game/map/map.component";
import { STATIC_HUMAN_PLAYER_ID, STATIC_OPPONENT_PLAYER_ID } from "../../model/mocks";
import { GameState } from "src/model/game/playing/game-state";
import { GamePlayService } from "../../services/game-play.service";

@Component({
  selector: "app-game-playing",
  templateUrl: "./game-playing.component.html",
  styleUrls: ["./game-playing.component.scss"]
})
export class GamePlayingComponent implements OnInit, OnDestroy {
  private gameEventSubscription?: Subscription;

  public events$: Subject<string[]> = new Subject<string[]>();
  private events: string[] = [];

  public sizeX = 24.0;
  public sizeY = 24.0;

  public player1Id!: UUID;
  public player2Id!: UUID;

  public player1Turn = true;
  public player2Turn = false;

  public gameId!: UUID;

  @ViewChild("mapPlayer1")
  public player1Map!: MapComponent;

  @ViewChild("mapPlayer2")
  public player2Map!: MapComponent;

  public gamePhase = GameState.SETUP;
  public readonly gamePhaseEnum = GameState;

  private playableActions: GamePlayingAction[] = [];

  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private gameViewerService: GameEventService,
    private gamePlayService: GamePlayService) {
  }

  public onCellClickPlayer1Map(evt: CellClickEvent): void {
    console.log(evt);
    if (evt.value !== MapValue.EMPTY) {
      console.warn("Can not build an non-empty field");
      return;
    }
    if (this.gamePhase !== GameState.SETUP) {
      console.warn("Can not build outside of the SETUP phase");
      return;
    }
    // this.player1Turn = false;
    this.gamePlayService.placeBoat(evt.coordinate, this.player1Id, this.gameId).subscribe();
  }

  public onCellClickPlayer2Map(evt: CellClickEvent): void {
    console.log(evt);

    if (evt.value === MapValue.HIT || evt.value === MapValue.MISS) {
      console.warn("Can not attack already attacked field!");
      return;
    }

    if (!this.player1Turn) {
      console.warn("Cannot attack, it's not this players turn");
      return;
    }

    if (this.gamePhase !== GameState.PLAYING) {
      console.warn("Cannot attack in the non-play phase!");
      return;
    }

    // this.player1Turn = false;
    this.gamePlayService.attackPlayer(evt.coordinate, this.player1Id, this.gameId).subscribe();
  }

  ngOnInit(): void {
    this.gameEventSubscription = this.activatedRoute.params
      .pipe(
        map((params) => params.gameId),
        // TODO: Fetch game data here
        switchMap((gameId: UUID) => {
          this.gameId = gameId;
          this.player1Id = STATIC_HUMAN_PLAYER_ID
          this.player2Id = STATIC_OPPONENT_PLAYER_ID
          return this.gameViewerService.listenToGameEvents(gameId);
        })
      ).subscribe((event) => {
        this.onEvent(event);
      });

  }

  ngOnDestroy(): void {
    this.gameEventSubscription?.unsubscribe();
  }

  private onEvent(event: PlayingEvent): void {
    this.events = this.events.concat([JSON.stringify(event)]);
    this.events$.next(this.events);
    this.mapEventToMapChanges(event);
  }

  private mapEventToMapChanges(event: PlayingEvent): void {
    if (event.type === "GameStartSetupEvent") {
      this.gamePhase = GameState.SETUP;
      this.player1Turn = true;
      this.player2Turn = true;
    }

    if (event.type === "GameStartPlayingEvent") {
      this.gamePhase = GameState.PLAYING;
      this.player1Turn = false;
      this.player2Turn = false;
    }

    if (event.type === "TakeTurnEvent") {
      this.gamePhase = GameState.PLAYING;
      this.playableActions = event.actions;
    }

    if (event.type === "GameEndEvent") {
      this.gamePhase = GameState.END;
      this.player1Map.resetMap();
      this.player2Map.resetMap();
      return;
    }

    if (event.type === "PlaceBoatEvent") {
      const map = this.getMapOfPlayer(event.playerId);
      map.setBoat(event.coordinate);
      return;
    }

    if (event.type === "AttackEvent") {
      const map = this.getMapOfAttackedPlayer(event.attackingPlayerId);
      if (event.status === AttackStatus.HIT) {
        map.setHit(event.coordinate);
      } else {
        map.setMiss(event.coordinate);
      }
      return;
    }

  }

  private getMapOfAttackedPlayer(attackingPlayerId: UUID): MapComponent {
    if (attackingPlayerId !== this.player1Id) {
      return this.player2Map;
    } else {
      return this.player1Map;
    }
  }

  private getMapOfPlayer(playerId: UUID): MapComponent {
    if (playerId !== this.player1Id) {
      return this.player1Map;
    } else {
      return this.player2Map;
    }
  }

  public canAttack(): boolean {
    return this.player1Turn && this.playableActions.find((a) => a.id === "ATTACK") !== undefined;
  }
}
