package com.swingdev.soldiers

import akka.actor.{Actor, Props, ActorRef, Cancellable, Scheduler}
import scala.concurrent.duration._
import com.swingdev.soldiers.SoldierTypeAliases._
import com.swingdev.game.VectorClock

object SoldierActor {

	//Messages
	case class GotAttacked(dmg: Damage)  // by enemy
	case class Attack(dmg: Damage) // enemy

	/**
		Updates internal state of the SoldierActor
		@newPosition represents new position of soldier on the `world matrix`
		@rangeMatrix is matrix that contains eventual `ActorRef`s of enemies
		@vc VectorClock 
	**/
	case class UpdateState(newPosition: Position, rangeMatrix: List[Option[ActorRef]], vc: VectorClock)

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
	def KnightActor(size: Int): Props = Props(new SoldierActor(Knight.apply(), VectorClock(Array.ofDim[Int](size))))
	def ArcherActor(size: Int): Props = Props(new SoldierActor(Archer.apply(), VectorClock(Array.ofDim[Int](size))))
	def HorseRiderActor(size: Int): Props = Props(new SoldierActor(HorseRider.apply(), VectorClock(Array.ofDim[Int](size))))
	// def CreateKnightActor(pos: Position) = Props(new SoldierActor(Knight()))
}

class SoldierActor(var soldier: Soldier, var myVectorclock: VectorClock) extends Actor { 
	var rangeMatrix: List[Option[ActorRef]] = List.empty

	import SoldierActor._

	import context.dispatcher
	var idleTickerNumber: Int = 0

	override def preStart() = {
		context.system.scheduler.schedule(0.seconds, 1.second, self, Tick)
	}

	def Idle: Receive = {
	  case AttackedWithVC(dmg, vc)	=> 
	  	val eventOrdering = myVectorclock.compareVC(vc)
		  myVectorclock = myVectorclock.updateVC(vc)
		  soldier.updateLife(dmg)
	  case Tick 						=> 
	  	if(idleTickerNumber == 5) {
	  		context.unbecome 
	  	} else idleTickerNumber = idleTickerNumber + 1
	}

	def receive: Receive = {
		case AttackedWithVC(dmg, vc)	=> 
			myVectorclock = myVectorclock.updateVC(vc)
			soldier.updateLife(dmg)
		case UpdateState(pos, mx, vc)	=> 
			myVectorclock = myVectorclock.updateVC(vc)
			rangeMatrix = mx
			soldier 	= soldier.updatePosition(pos)
		case _ => 
	} 

}