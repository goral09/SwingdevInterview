package com.swingdev.soldiers

import akka.actor.{Actor, Props, ActorRef, FSM}
import scala.concurrent.duration._
import com.swingdev.soldiers.SoldierTypeAliases._

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
	case class UpdateState(newPosition: Position, rangeMatrix: List[Option[ActorRef]], vc: Array[Int])

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
	def KnightActor: Props = Props(new SoldierActor(Knight.apply()))
	def ArcherActor: Props = Props(new SoldierActor(Archer.apply()))
	def HorseRiderActor: Props = Props(new SoldierActor(HorseRider.apply()))
	// def CreateKnightActor(pos: Position) = Props(new SoldierActor(Knight()))
}

class SoldierActor(var soldier: Soldier) extends Actor { 
	var vectorClock: Array[Int] = Array.ofDim[Int](soldier.range + 1)
	var rangeMatrix: List[Option[ActorRef]] = List.empty

	import SoldierActor._

	var idleTicker: Int = _


	override def preStart() = {
		idleTicker = 0
	}

	def Idle: Receive = {
		case GotAttacked(dmg)			=> soldier.updateLife(dmg)
		case BecomeActive 				=> context.unbecome
		case Tick 						=> if(idleTicker == 5) context.unbecome else idleTicker = idleTicker + 1
	}

	def receive: Receive = {
		case GotAttacked(dmg)			=> soldier.updateLife(dmg)
		case UpdateState(pos, mx, vc)	=> 
			vectorClock = updateVectorClock(vc, vectorClock)
			rangeMatrix = mx
			soldier 	= soldier.updatePosition(pos)
		case _ => 
	}

	// Method that updates internal vector clock by taking maximum for each i-
	def updateVectorClock(newVC: Array[Int], vectorClock: Array[Int]): Array[Int] =  { 
		vectorClock(0) = vectorClock(0) + 1; 
		for {
			i <- Range(0, vectorClock.size).toArray
		} yield math.max(newVC(i), vectorClock(i))
	}

}