import { ChangeDetectorRef, Component, Input, OnInit } from "@angular/core";
import { UUID } from "../../../model/uuid";
import { Coordinate } from "../../../model/game/playing/actions";

enum MapValues {
  EMPTY = 0,
  BOAT = 1,
  HIT = 2,
  MISS = 3
}

@Component({
  selector: "app-map",
  templateUrl: "./map.component.html",
  styleUrls: ["./map.component.scss"]
})
export class MapComponent implements OnInit {

  public readonly mapValuesEnum = MapValues;

  @Input()
  public gameId!: UUID;

  @Input()
  public playerId!: UUID;

  @Input()
  public sizeX!: number;

  @Input()
  public sizeY!: number;

  public map: number[][] = [[]];

  constructor(private ref: ChangeDetectorRef) {

  }

  ngOnInit(): void {
    this.resetMap();
  }

  public setHit(coordinate: Coordinate): void {
    this.setMapValue(coordinate, MapValues.HIT);
  }

  public setMiss(coordinate: Coordinate): void {
    this.setMapValue(coordinate, MapValues.MISS);
  }

  public setBoat(coordinate: Coordinate): void {
    this.setMapValue(coordinate, MapValues.BOAT);
  }

  private setMapValue(coordinate: Coordinate, mapValue: MapValues): void {
    this.map[coordinate.x][coordinate.y] = mapValue;
    this.ref.markForCheck();
  }

  public resetMap(): void {
    this.map = [];
    for (let x = 0; x < this.sizeX; x++) {
      this.map.push(Array(this.sizeY).fill(0));
    }
  }

  public getRowName(index: number): string {
    return String.fromCharCode(65 + index);
  }
}
