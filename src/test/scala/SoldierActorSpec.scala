package com.swingdev.tests

import org.specs2.mutable._

class SoldierActorSpec extends Specification {

  "SoldierActor" should {

    "make a move request" in {
      pending
    }

    "receive position update and properly change its soldier position" in {
      pending
    }

    "receive vectorclock update and properly update its clock" in {
      pending
    }

    "receive GotAttacked command and check order of events" in {
      pending
    }

    "receive GotAttacked command and properly change life amount of its soldier" in {
      pending
    }

    "receive GotAttacked command and if life == 0 then die" in {
      pending
    }

    "change its state from Idle to Active after five ticks" in {
      pending
    }

  }

}