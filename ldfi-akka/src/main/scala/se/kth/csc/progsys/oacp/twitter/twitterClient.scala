package se.kth.csc.progsys.oacp.twitter

import java.util.concurrent.ThreadLocalRandom

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.rbmhtechnology.eventuate.VectorTime
import se.kth.csc.progsys.oacp.protocol._
import protocol._
import se.kth.csc.progsys.oacp.state.{Entry, FollowerEntry}
import akka.event._
import akka.actor.ActorLogging
import akka.testkit.CallingThreadDispatcher
import ldfi.akka.evaluation.Controller

/**
  * Created by star on 2017-11-24.
  */
class twitterClient extends Actor with ActorLogging {

  var raftActorRef = Set.empty[ActorRef] //Only raft server will be added

  var clusterSelf: ActorRef = self

  var nextSendTo: Option[ActorRef] = None

  var receiveFrom: Option[ActorRef] = None

  var time: Long = 0

  def receive = LoggingReceive {

    case ClusterListenerIs(raftCluster) =>
      clusterSelf = raftCluster

    case RaftMemberAdded(member) =>
      raftActorRef += member

    case Resend(leader, msg) =>
      log.info("Get message from Follower or Candidate")
      log.warning("resend msg: {}", msg)
      if(leader.isDefined){
        nextSendTo = leader
        if (Controller.greenLight(self.path.name, leader.get.path.name, msg)) leader.get ! msg
      }
      else {
        //receiveFrom.get ! "no leader find"
        var rServer = RandomServer()
        Thread.sleep(5000)
        if (Controller.greenLight(self.path.name, rServer.get.path.name, msg)) rServer.get ! msg
      }

    case LeaderIs(id: Option[ActorRef]) =>
      nextSendTo = id

    case Melt =>
      log.info("receive CRDT message")
      receiveFrom = Some(sender())
      raftActorRef foreach { i =>
          if (Controller.greenLight(self.path.name, i.path.name, Melt)) i ! Melt
      }

    case CvSucc =>
    //receiveFrom.get ! SendMessageSuccess

    case CvOp(value: FollowerEntry, op: String) =>
      log.info("receive CRDT message")
      receiveFrom = Some(sender())
      val rServer = RandomServer()
      if (rServer.isDefined) {
        time += 1
        if (Controller.greenLight(self.path.name, rServer.get.path.name, MUpdateFromClient(value, op, vectorTime(rServer.get, time)))) rServer.get ! MUpdateFromClient(value, op, vectorTime(rServer.get, time))
      }
      else {
        log.info("no connection yet")
        if (Controller.greenLight(self.path.name, sender().path.name, "No connection yet")) sender() ! "No connection yet"
      }

    case TOp(command: Map[String, String]) =>
      log.warning("receive raft message")
      receiveFrom = Some(sender())
      val rServer = RandomServer()
      if (nextSendTo.isDefined) {
        log.warning("nextSendTo is defined")
        if (Controller.greenLight(self.path.name, nextSendTo.get.path.name, NMUpdateFromClient(command))) nextSendTo.get ! NMUpdateFromClient(command)
      }
      else if (rServer.isDefined) {
        log.warning("rServer is defined")
        if (Controller.greenLight(self.path.name, rServer.get.path.name, NMUpdateFromClient(command))) rServer.get ! NMUpdateFromClient(command)
      }
      else {
        log.info("no connection yet")
        if (Controller.greenLight(self.path.name, sender().path.name, "No connection yet")) sender() ! "No connection yet"
      }

    //TODO:
    case ReadLocal =>
      log.info("read message from local")

    //        case LogIs(nLog, mState) =>
    //          log.warning("get log from server")

    case StartMessage =>
      if (Controller.greenLight(self.path.name, sender().path.name, StartReady)) sender() ! StartReady

    case EndMessage =>
      if (Controller.greenLight(self.path.name, sender().path.name, EndReady)) sender() ! EndReady

    case AddFollower(me: String, id: String) =>
      self tell (CvOp(FollowerEntry(me, id), "add"), sender())


    case Tweet(msg: Map[String, String]) =>
      log.warning("send tweet message")
      receiveFrom = Some(sender())
      val rServer = RandomServer()
      if (nextSendTo.isDefined) {
        log.warning("nextSendTo is defined")
        if (Controller.greenLight(self.path.name, nextSendTo.get.path.name, UpdateTweet(msg))) nextSendTo.get ! UpdateTweet(msg)
      }
      else if (rServer.isDefined) {
        log.warning("rServer is defined")
        if (Controller.greenLight(self.path.name, rServer.get.path.name, UpdateTweet(msg))) rServer.get ! UpdateTweet(msg)
      }
      else {
        log.info("no connection yet")
        if (Controller.greenLight(self.path.name, sender().path.name, "No connection yet")) sender() ! "No connection yet"
      }

    //    case LogIs(l: List[Entry[Map[ActorRef, List[String]], FollowerEntry]]) =>
    //      receiveFrom.get ! LogIs(l: List[Entry[Map[ActorRef, List[String]], FollowerEntry]])

    case Read(id: String) =>
      log.warning("receive raft message")
      receiveFrom = Some(sender())
      val rServer = RandomServer()
      if (nextSendTo.isDefined) {
        log.warning("nextSendTo is defined")
        if (Controller.greenLight(self.path.name, nextSendTo.get.path.name, ReadTwitter(id))) nextSendTo.get ! ReadTwitter(id)
      }
      else if (rServer.isDefined) {
        log.warning("rServer is defined")
        if (Controller.greenLight(self.path.name, rServer.get.path.name, ReadTwitter(id))) rServer.get ! ReadTwitter(id)
      }
      else {
        log.info("no connection yet")
        if (Controller.greenLight(self.path.name, sender().path.name, "No connection yet")) sender() ! "No connection yet"
      }

    case TwitterIs(l: List[String]) =>
      if (Controller.greenLight(self.path.name, receiveFrom.get.path.name, ResultIs(l))) receiveFrom.get ! ResultIs(l)
  }

  def RandomServer(): Option[ActorRef] =
    if(raftActorRef.isEmpty) None
    else raftActorRef.drop(ThreadLocalRandom.current nextInt raftActorRef.size).headOption

  def vectorTime(id: ActorRef, time: Long): VectorTime = {
    VectorTime(id.toString -> time)
  }

}
