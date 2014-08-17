package com.swingdev.game

object WarGroundUtils {}

case class WarGround(width: Int, height: Int) {
  var world = Array.ofDim[Int](width, height)

  def whatIsOn(x: Int, y: Int): Int = world(x)(y)
}