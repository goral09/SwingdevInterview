package com.swingdev.soldiers

/*
  @pos  - position on map
  @army - to which army this soldier belongs to
  @soldierType - is it Archer/Knight/Horse rider
*/
case class SoldierInfo(pos: Position, army: Int, soldierType: Int) {
  lazy val soldierRepresentation: Int = army * 10 + soldierType
}