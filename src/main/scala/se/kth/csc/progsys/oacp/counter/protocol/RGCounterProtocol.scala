package se.kth.csc.progsys.oacp.counter.protocol

import se.kth.csc.progsys.oacp.state.{Entry, RGCounter}


/**
  * Created by star on 2017-11-24.
  */
trait RGCounterProtocol {
  sealed trait CounterCmnd
  case class Add(num: Int) extends CounterCmnd
  case object Get extends CounterCmnd
  case object Reset extends CounterCmnd
  //case class ResultIs(mValue: Int) extends CounterCmnd
  case class ResultIs(l: List[Entry[Array[Int], String]]) extends CounterCmnd
}