package com.swingdev.soldiers

/*
  @pos  - position on map
  @army - to which army this soldier belongs to
  @soldierType - is it Archer/Knight/Horse rider
*/
case class SoldierInfo(pos: Position, army: Int, soldierType: Int) {
  lazy val soldierRepresentation: Int = army * 10 + soldierType

  import SoldierTypeAliases._
  implicit val typeToRange: Range = soldierType match {
  	//Archer
    case 1 => 5
    //Knight
    case 2 => 5
    //KnightRider
    case 3 => 7
  }

  override def toString(): String = s"$pos $army $soldierType"
}