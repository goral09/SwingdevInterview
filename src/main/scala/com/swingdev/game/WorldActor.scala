package com.swingdev.game

import com.swingdev.soldiers.SoldierInfo
import com.swingdev.soldiers.Soldier
import akka.actor.{Actor, Props, ActorRef, ActorLogging}
import scala.concurrent.duration._
import com.swingdev.soldiers.Position

object WorldActor {
  case class Move(newPos: Position)
  case object IAmDead
}

trait WorldManipulations {
  /*
    For given position and army number returns list of enemies that are in range
  */
  def getEnemies(pos: Position, armyType: Int, range: Int): Option[Seq[ActorRef]] = {
    None
  }

  /*
    Check if soldier can move to desired position
  */
  def isPosEmpty(pos: Position, world: Array[Array[Int]]) = world(pos.x)(pos.y) != 0

}

class WorldActor(numberOfSoldiers: Int) extends Actor with ActorLogging with WorldManipulations {
  val worldArray: Array[Array[Int]] = Array.ofDim[Int](100,100)
	var vectorClock: VectorClock = VectorClock(Array.ofDim[Int](numberOfSoldiers))
	var actorMap: Map[ActorRef, SoldierInfo] = _
  

  import WorldActor._

	def receive = {
    /*
      When Move(newPosition) received WorldActor has to:
      - check if new position is not occupied
      - if occupied then return
        - old position 
        - sequence of enemies in range 
        - their VectorClocks
      - if empty then
        - move soldier to new position on map
        - return
          - new position
          - sequence of enemies in range
          - their VectorClocks
    */
		case Move(newPos) => 
      val senderRef = sender()
      isPosEmpty(newPos, worldArray) match {
        //desired position is empty
        case true => 
          val soldierOpt: Option[SoldierInfo] = actorMap.get(senderRef)
          soldierOpt.foreach { soldier =>
            val oldPos = soldier.pos
            val newSoldierInfo = SoldierInfo(newPos, soldier.army, soldier.soldierType)
            //update soldier map
            actorMap = actorMap.updated(senderRef, newSoldierInfo)
            //update array that represents world state
            worldArray(newPos.x)(newPos.y) = soldier.army * 10 + soldier.soldierType
          }

        case false => 
      }

    /*
      When soldier reports that it is dead remove it from map and array
    */
    case IAmDead => 
      val soldier = sender()
      val soldierInfo: Option[SoldierInfo] = actorMap.get(soldier)
      actorMap = actorMap - soldier
      soldierInfo.foreach { soldier =>
        worldArray(soldier.pos.x)(soldier.pos.y) = 0
      }
	}
} 