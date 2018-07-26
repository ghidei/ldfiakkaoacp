
# Description (sample taken from [CynthiaZ92/OACP](https://github.com/CynthiaZ92/OACP))

## Introduction for OACP
### Basic idea about OACP
Observable Atomic Consistency Protocol is a memory model for distribued system. We extend the current GSP with monotonic update and try to get better performance.
It's a combination between total order broadcast (which will be implemented through raft) and Conflict Free Data Type(CRDT). So our implementation will be divided into two stages:
1. Raft protocol
2. CRDT representation (start with GCounter, ORset)
### API usage (counter example)
#### Step One: CRDT implementation (state/CRDT)
Extend existing trait CRDT:
````
case class RGCounter(...) extends CRDT[Array[Int], Int]

object CRDT{

  implicit def RGCounterCRDT = new RGCounter
  
  ...

}
````
### A small test example
The code is included in src/multi-jvm/scala/se/kth/csc/progsys/oacp/ExampleSpec.scala
This example uses twitter server and client and includes:
1. create three server actors(Raft cluster) and one client actor
2. send message to the raft cluster (msg type: AddFollower(CvOp), Twitter(TOp), Read(TOp))
3. count the number of messages between client and servers [Optional]
 
Running command: using "sbt" to the interaction mode, then "multi-jvm:testOnly se.kth.csc.progsys.oacp.ExampleSpec".

# To run [ldfi-akka](https://github.com/KTH/ldfi-akka)
