package se.kth.csc.progsys.oacp
import sys.process._

object Main extends App {

  var correctness = false
    try {
      val res = Seq("sbt", "multi-jvm:testOnly se.kth.csc.progsys.oacp.ExampleSpec").!

      if(res == 0) correctness = true
      else correctness = false

    }
    catch {
      case e: Exception => "Error"
    }

  println("\n\nCorrectness of run: " + verifyCorrectness)

  def verifyCorrectness: Boolean = correctness
}
