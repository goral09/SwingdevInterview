package com.swingdev.soldiers

case class Archer(pos: Position, armNo: Int) extends Soldier {
	import SoldierTypeAliases._

	override val life = 50
	override val armor = 10
	override val range = 5
	override val damage = 20
	override val movePoints = 3

	override def updatePosition(pos: Position): Archer = Archer(Position(pos.x, pos.y), armNo)
}

object Archer {
	def apply() = new Archer(Position(),scala.util.Random.nextInt(1))
}