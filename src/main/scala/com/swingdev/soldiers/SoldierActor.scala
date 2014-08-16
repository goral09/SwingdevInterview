package com.swingdev.soldiers

import akka.actor.{Actor, Props, ActorRef, FSM}
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

class SoldierActor(var soldier: Soldier, var vectorclock: VectorClock) extends Actor { 
	var rangeMatrix: List[Option[ActorRef]] = List.empty

	import SoldierActor._

	var idleTicker: Int = _


	override def preStart() = {
		idleTicker = 0
	}

	def Idle: Receive = {
		case AttackedWithVC(dmg, vc)	=> 
			//TODO check if predicate {myVC(worldState) - vc(worldState) > 1} 
			//before updating soldier state isn't better solution
			//it checks whether Attacker isn't far behind which could mean that I am not
			//longer in his range 
			vectorclock = vectorclock.updateVC(vc)
			soldier.updateLife(dmg)
		case BecomeActive 				=> 
			idleTicker = 0
			context.unbecome
		case Tick 						=> if(idleTicker == 5) context.unbecome else idleTicker = idleTicker + 1
	}

	def receive: Receive = {
		case AttackedWithVC(dmg, vc)	=> 
			vectorclock = vectorclock.updateVC(vc)
			soldier.updateLife(dmg)
		case UpdateState(pos, mx, vc)	=> 
			vectorclock = vectorclock.updateVC(vc)
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