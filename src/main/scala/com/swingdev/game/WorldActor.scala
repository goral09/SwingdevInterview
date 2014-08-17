package com.swingdev.game

import com.swingdev.soldiers.{ Soldier, Position, SoldierInfo }
import akka.actor.{Actor, Props, ActorRef, ActorLogging}
import scala.concurrent.duration._

object WorldActor {
  case class Move(info: SoldierInfo, newPos: Position)
  case class IAmDead(pos: Position)
  case class PutSoldier(ref: ActorRef, info: SoldierInfo)
}

trait WorldManipulations {
  /*
    For given position and army number returns list of enemies that are in range
  */
  def getEnemies(info: SoldierInfo,
    map: Map[Position, Option[ActorRef]]): Option[Seq[ActorRef]] = {
    None
  }

  /*
    Check if soldier can move to desired position
  */
  def isPosEmpty(pos: Position, world: Map[Position, Option[ActorRef]]): Boolean = world.getOrElse(pos, None) match {
    case Some(_)    => false
    case None       => true
  }

}

class WorldActor(numberOfSoldiers: Int) extends Actor with ActorLogging with WorldManipulations {
  var worldMap:     Map[Position, Option[ActorRef]]  = Map.empty
	var vectorClock:  VectorClock                      = VectorClock(Array.ofDim[Int](numberOfSoldiers+1))
  val worldArray:   Array[Array[Int]]                = Array.ofDim[Int](100,100)
  

  import WorldActor._
  import com.swingdev.soldiers.SoldierActor._

	def receive = {
    case PutSoldier(ref: ActorRef, info: SoldierInfo) => 
      isPosEmpty(info.pos, worldMap) match {
        case false => 
        case true  => 
          worldMap = worldMap.updated(info.pos, Some(ref))
          worldArray(info.pos.x)(info.pos.y) = info.soldierRepresentation
      }
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
		case Move(info, newPos) => 
      log.info(s"Received Move command")
      val senderRef = sender()
      isPosEmpty(newPos, worldMap) match {
        //desired position is empty
        case true => 
          val oldPos = info.pos
          // put sender on the position
          worldMap = worldMap.updated(newPos,Some(senderRef))
          worldArray(newPos.x)(newPos.y) = info.soldierRepresentation
          worldArray(oldPos.x)(oldPos.y) = 0
          val enemies: Option[Seq[ActorRef]] = getEnemies(info, worldMap)
          val vectorClock: Option[VectorClock]  = generateVectorClock()

          sender ! UpdateState(newPos, enemies, vectorClock)

        //New position is already taken
        case false =>  
          val oldPos = info.pos
          val enemies: Option[Seq[ActorRef]] = getEnemies(info, worldMap)
          val vectorClock: Option[VectorClock]  = generateVectorClock()
          senderRef ! UpdateState(oldPos, enemies, vectorClock)
      }

    /*
      When soldier reports that it is dead remove it from map and array
    */
    case IAmDead(pos: Position) => 
      log.info("Received IAmDead command from (${pos.x}, ${pos.y})")
      val soldier = sender()
      worldMap = worldMap - pos
      worldArray(pos.x)(pos.y) = 0
	}
} 