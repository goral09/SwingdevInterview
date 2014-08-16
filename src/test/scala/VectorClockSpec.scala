package com.swingdev.tests

import org.specs2.mutable._
import com.swingdev.game.VectorClock
import com.swingdev.game.EventOrdering._

class VectorClockSpec extends Specification {
	"VectorClock" should {
		"return proper ordering" in {
			val vcBefore = VectorClock(Array(10,5,2,4))
      val vcOther  = VectorClock(Array(8,3,5,3))

      vcBefore.compareVC(vcOther,3) === Before
		}

		"return properly updated VectorClock" in {
			val worldVC = VectorClock(Array(10,5,4,8,2))
      val myVC    = VectorClock(Array(5,8, 3,5,1))

      val newVC = myVC.updateVC(worldVC)

      newVC === VectorClock(Array(10,8,4,8,2))
		}
	}	
}