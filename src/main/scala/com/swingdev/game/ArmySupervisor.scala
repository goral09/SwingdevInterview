package com.swingdev.game

import akka.actor.{Actor, Props, ActorContext, Terminated, ActorRef}
import com.swingdev.soldiers.{Soldier, SoldierActor, Archer, Knight, KnightRider}
import com.swingdev.game.WorldActor.{PutSoldier}

object ArmySupervisor {
  def props(armyNo: Int, archerNo: Int, knightNo: Int, horseRiderNo: Int, worldActorRef: ActorRef) = 
    Props(new ArmySupervisor(armyNo, archerNo, knightNo, horseRiderNo, worldActorRef))
}

class ArmySupervisor(armyNo: Int, archerNo: Int, knightNo: Int, horseRiderNo: Int,
  worldActorRef: ActorRef) extends Actor {
  var childNo: Int = _

  private[ArmySupervisor] def createSoldiers(number: Int, soldierProps: => Props, sType: Int)(implicit context: ActorContext): Unit = {
      (1 to number).foreach { i => 
        val childRef = context.actorOf(soldierProps)
        context.watch(childRef)
        worldActorRef ! PutSoldier(childRef, armyNo, sType)
      }
  }

  override def preStart() = {
    createSoldiers(archerNo, SoldierActor.ArcherActor, 1)
    createSoldiers(knightNo, SoldierActor.KnightActor, 2)
    createSoldiers(horseRiderNo, SoldierActor.HorseRiderActor, 3)
    childNo = archerNo + knightNo + horseRiderNo
  }

  

  override def receive = {
    case Terminated => 
      childNo = childNo - 1
      if(childNo == 0)
        context.stop(self)
  }
}