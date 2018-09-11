package se.kth.csc.progsys.oacp
import sys.process._

object Main extends App {
  var correctness = false

  try {


    /******** TEMP ******/

    /*
    val cmd = Seq("sbt", "multi-jvm:testOnly se.kth.csc.progsys.oacp.ExampleSpec")
    val res = scala.sys.process.Process(cmd, new java.io.File("/home/ygh@vizrt.internal/Desktop/KTH/samples/oacp/ldfi-akka")).!

    */

    /********************/


    /******** RUNS OUTSIDE ******/

    val res = Seq("sbt", "multi-jvm:testOnly se.kth.csc.progsys.oacp.ExampleSpec").!

    /****************************/

    if (res == 0) correctness = true
    else correctness = false

  } catch {
    case e: Exception => "Error"
    case _ => sys.error("Could not run command")
  }

  println("\n\nCorrectness of run: " + verifyCorrectness)

  def verifyCorrectness: Boolean = correctness
}
