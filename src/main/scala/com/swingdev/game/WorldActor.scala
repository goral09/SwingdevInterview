package com.swingdev.game

import com.swingdev.soldiers.{ Soldier, Position, SoldierInfo }
import com.swingdev.soldiers.SoldierActor.{IAmDead, GotAttacked}
import akka.actor.{ Actor, Props, ActorRef, ActorLogging }
import scala.concurrent.duration._

object WorldActor {
  sealed trait SoldierActorCommand {
    val vectorClock: VectorClock
  }
  case class Move(info: SoldierInfo, newPos: Position) extends SoldierActorCommand
  
  case class PutSoldier(ref: ActorRef, armyNo: Int, sType: Int) extends SoldierActorCommand

  def props(num: Int): Props = Props(new WorldActor(num))
}

trait WorldManipulations {
  /*
    For given position and army number returns list of enemies that are in range
  */
  def getEnemies(info: SoldierInfo, map: Map[Position, Option[ActorRef]]): Option[Seq[ActorRef]] = {
    map.collect {
      case (pos, Some(ref)) if(pos.x + pos.y <= info.typeToRange) => ref
    } match {
      case Nil => None
      case l : List[ActorRef] => Some(l)
    }
  }

  /*
    Check if soldier can move to desired position
  */
  def isPosEmpty(pos: Position, world: Map[Position, Option[ActorRef]]): Boolean = world.getOrElse(pos, None) match {
    case Some(_)    => false
    case None       => true
  }

  /*
    Return first empty position on array that represents world 
  */
  def getEmptyPosition(armyNo: Int, world: Array[Array[Int]]): Option[(Position)] = {
    def hasEmptyField(row: Array[Int]): Boolean = row.contains(0)
    def indexOfEmptyField(row: Array[Int]): Option[Int] = row.zipWithIndex.collectFirst( { case (v,i) if(v == 0) => i })
    world.zipWithIndex.collectFirst({case (row, rowIndex) if(hasEmptyField(row)) => indexOfEmptyField(row) match {
      case p @ Some(x) => Position(rowIndex, x)
      }})
  }

}

class WorldActor(numberOfSoldiers: Int) extends Actor with ActorLogging with WorldManipulations {
  var worldMap:     Map[Position, Option[ActorRef]]  = Map.empty
	var vectorClock:  VectorClock                      = VectorClock(Array.ofDim[Int](numberOfSoldiers+1))
  val worldArray:   Array[Array[Int]]                = Array.ofDim[Int](100,100)
  

  import WorldActor._
  import com.swingdev.soldiers.SoldierActor._

	def receive = {
    case PutSoldier(ref, armyNo, sType) => 
      log.debug("Received PutSoldier command ${info}")
      val pos: Option[Position] = getEmptyPosition(armyNo, worldArray)
      for {
        emptyPosition <- pos
      } {
        worldMap = worldMap.updated(emptyPosition, Some(ref))
        worldArray(emptyPosition.x)(emptyPosition.y) = armyNo * 10 + sType
      }

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
    case IAmDead(pos) => 
      log.info("Received IAmDead command from $pos")
      worldMap = worldMap - pos
      worldArray(pos.x)(pos.y) = 0
	}
} 