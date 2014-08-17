package com.swingdev.game

import akka.actor.{Actor, Props, ActorContext, Terminated}
import com.swingdev.soldiers.{Soldier, SoldierActor}

object ArmySupervisor {
  def props(archerNo: Int, knightNo: Int, horseRiderNo: Int) = Props(new ArmySupervisor(archerNo, knightNo, horseRiderNo))
}

class ArmySupervisor(archerNo: Int, knightNo: Int, horseRiderNo: Int) extends Actor {
  var childNo: Int = _
  private[ArmySupervisor] def createSoldiers(no: Int, soldierProps: => Props)(implicit context: ActorContext): Unit = {
    for {
      i <- 1 to no
    } {
      val child = context.actorOf(soldierProps)
      context.watch(child)
    }
  }

  override def preStart() = {
    createSoldiers(archerNo, SoldierActor.ArcherActor)
    createSoldiers(knightNo, SoldierActor.KnightActor)
    createSoldiers(horseRiderNo, SoldierActor.HorseRiderActor)
    childNo = archerNo + knightNo + horseRiderNo
  }

  override def receive = {
    case Terminated => 
      childNo = childNo - 1
      if(childNo == 0)
        context.stop(self)
  }
}