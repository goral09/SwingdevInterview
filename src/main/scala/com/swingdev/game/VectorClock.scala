package com.swingdev.game

import EventOrdering._

case class VectorClock(var arr: Array[Int]) {

	override def equals(other: Any) = this.arr.sameElements(other.asInstanceOf[VectorClock].arr)
	/*
	 Compare @other vectorclock and compare it to @this vector\clock.
	 Check <i>pos</i> value from vectorclocks and compare them.
	 If other(pos) < this(pos) then @other happened earlier
	 If other(pos) == this(pos) then they <b>may</b> happened in parallel
	*/
	def compareVC(other: VectorClock, pos: Int): Precedence = (this.arr(pos), other.arr(pos)) match {
		case (a,b) if a > b 	=> Before
		case (a,b) if a < b 	=> After
		case (a,b) if a == b 	=> Parallel
	}
	/*
	 Update @this vectorclock with values from @other.
	 Algorithm for updating comes from definiton of Vectorclocks in asynchronous system
	*/
	def updateVC(other: VectorClock): VectorClock = {
		val arr: Array[Int] = Array.ofDim(other.arr.size)
		
		for {
			i <- Range(0, other.arr.size).toList
		} arr(i) = math.max(this.arr(i), other.arr(i))

		VectorClock(arr)
	}
}