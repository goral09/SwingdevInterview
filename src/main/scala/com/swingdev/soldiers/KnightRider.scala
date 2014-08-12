package com.swingdev.soldiers

case class HorseRider(pos: Position, armNo: Int) extends Soldier {
	import SoldierTypeAliases._
	
	override val life = 90
	override val armor = 70
	override val range = 7
	override val damage = 30
	override val movePoints = 7

	override def updatePosition(pos: Position): HorseRider = HorseRider(Position(pos.x, pos.y), armNo)

}

object HorseRider{
	def apply() = new HorseRider(Position(), scala.util.Random.nextInt(1))
}