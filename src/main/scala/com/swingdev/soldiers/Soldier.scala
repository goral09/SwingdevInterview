package com.swingdev.soldiers

object SoldierTypeAliases {
	type Life 	= Int
	type Range	= Int
	type Damage	= Int
	type MovePoints = Int
	type Armor = Int
	type Enemy = Soldier
}

trait Soldier {
	import SoldierTypeAliases._

	val pos: Position
	val armNo: Int

	// Some features of soldier
	def life: Life
	def range: Range
	def damage: Damage
	def armor: Armor
	def movePoints: MovePoints

	// Methods that add some extras to our soldier
	def updateLife(dmg: Damage) = life + dmg
	def updateRange(range: Range) = range + range
	def updateDamage(addDmg: Damage) = damage + addDmg
	def updateArmor(addArmor: Armor) = armor + addArmor
	def updateMovePoints(addMP: MovePoints) = movePoints + addMP
	def updatePosition(newPosition: Position): Soldier

}



