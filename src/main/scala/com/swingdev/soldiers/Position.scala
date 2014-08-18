package com.swingdev.soldiers

case class Position(x: Int, y: Int)

object Position {
	def apply(): Position = Position(0,0)
}