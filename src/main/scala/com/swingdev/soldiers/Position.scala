package com.swingdev.soldiers

case class Position(x: Int, y: Int) {
	override def toString(): String = s"($x, $y)"
}

object Position {
	def apply(): Position = Position(0,0)
}