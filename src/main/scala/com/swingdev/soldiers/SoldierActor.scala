package com.swingdev.soldiers

import akka.actor.{Actor, Props, ActorRef, Cancellable, Scheduler, ActorLogging}
import scala.concurrent.duration._
import com.swingdev.soldiers.SoldierTypeAliases._
import com.swingdev.game.VectorClock

object SoldierActor {

	//MessagesPosition: Position, rangeMatrix: Seq[ActorRef], vc: VectorClock)
	case class GotAttacked(dmg: Damage)  // by enemy
	case class Attack(dmg: Damage) // enemy

	/**
		Updates internal state of the SoldierActor
		@newPosition represents new position of soldier on the `world matrix`
		@rangeMatrix is matrix that contains eventual `ActorRef`s of enemies
		@vc VectorClock 
	**/
	case class UpdateState(newPosition: Position, enemies: Option[Seq[ActorRef]], vc: Option[VectorClock])

	case class AttackedWithVC(dmg: Damage, vc: VectorClock)

	//Fancy updates of soldier's features
	case class DamageUpdate(addDmg: Damage)
	case class ArmorUpdate(addArmor: Armor)
	case class RangeUpdate(addRange: Range)
	case class MovePointsUpdate(addMp: MovePoints)

	//Utility messages
	//When Soldier is in Idle state this message `wakes` him up and GO BERSERK!
	case object BecomeActive
	//Tick object that is used to count down time to BecomeActive
	case object Tick

	//Props helper methods
	def KnightActor(): Props = Props(new SoldierActor(Knight.apply()))
	def ArcherActor(): Props = Props(new SoldierActor(Archer.apply()))
	def HorseRiderActor(): Props = Props(new SoldierActor(HorseRider.apply()))
}

class SoldierActor(var soldier: Soldier) extends Actor with ActorLogging { 
	var rangeMatrix: Seq[ActorRef] = Seq.empty
	var myVectorclock: VectorClock = VectorClock(Array.ofDim[Int](soldier.range+1))

	import SoldierActor._

	import context.dispatcher
	var idleTickerNumber: Int = 0

	override def preStart() = {
		context.system.scheduler.schedule(2.seconds, 1.second, self, Tick)
	}

	def Idle: Receive = {
	  case AttackedWithVC(dmg, vc)	=> 
	  	val eventOrdering = myVectorclock.compareVC(vc)
		  myVectorclock = myVectorclock.updateVC(vc)
		  soldier.updateLife(-1 * dmg)
	  case Tick 						=> 
	  	if(idleTickerNumber == 5) {
	  		context.unbecome 
	  	} else idleTickerNumber = idleTickerNumber + 1
	}

	def receive: Receive = {
		case AttackedWithVC(dmg, vc)	=> 
			myVectorclock = myVectorclock.updateVC(vc)
			soldier.updateLife(-1 * dmg)
		case UpdateState(pos, mx, vc)	=> 
			for {
				vectorClock <- vc
				enemies 		<- mx
			} {
				myVectorclock = myVectorclock.updateVC(vectorClock)
				rangeMatrix = enemies
			}
			soldier 	= soldier.updatePosition(pos)
		case _ => 
	} 

}