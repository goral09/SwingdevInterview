package com.swingdev.game

import com.swingdev.soldiers.SoldierInfo
import WarGroundUtils._
import com.swingdev.soldiers.Soldier
import akka.actor.{Actor, Props, ActorRef, FSM}
import scala.concurrent.duration._

class WorldActor extends Actor {
	// List of SoldierInfo that represents position, army no and types of soldiers
	var warGround: List[SoldierInfo] = _
	var actorMap: Map[ActorRef, SoldierInfo] = _
	// var vectorClock: Array[Int]      = Array.ofDim[Int](numberOfSoldiers)


	// def getEnemies(pos: Position): Seq[ActorRef] = Seq.empty

	def receive = {
		case _ => 
	}
}