package com.swingdev.soldiers

case class Knight(pos: Position, armNo: Int) extends Soldier {
	import SoldierTypeAliases._

	override val life = 80
	override val armor = 50
	override val range = 5
	override val damage = 40
	override val movePoints = 5

	override def updatePosition(pos: Position): Knight = Knight(Position(pos.x, pos.y), armNo)
}

object Knight {
	def apply() = new Knight(Position(),scala.util.Random.nextInt(1))
}