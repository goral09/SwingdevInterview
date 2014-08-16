package com.swingdev.game

/*
 Object that holds definitons of events ordering in game
*/
object EventOrdering { 
	sealed abstract trait Precedence

	case object Before 		extends Precedence
	case object Parallel 	extends Precedence
	case object After 		extends Precedence
}