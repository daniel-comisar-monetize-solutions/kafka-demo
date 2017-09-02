package com.monetizesolutions.kafkademo

import scala.util.Random

class MathRecord(val variable: String, val value: Double) {
  def key: Int = variable.hashCode
  override def toString: String = variable + "=" + value
}

object MathRecord {
  private val letters = ((0 to 25) zip ('a' to 'z')).toMap
  private def randomLetter = letters(Random.nextInt(26))
  private def randomNumber = Random.nextInt(100)
  def random = new MathRecord(randomLetter.toString, randomNumber)
}
