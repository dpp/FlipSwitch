package code.comet

import net.liftweb._
import actor._
import http._

import scala.xml.NodeSeq

object ChatServer extends LiftActor with ListenerManager
{
  private var msgs = List("Welcome")

  // message handler
  override def highPriority = {
    case s: String =>
      msgs ::= s
    updateListeners()
  }

  def createUpdate = msgs
}

class Chat extends
CometActor with CometListener
{
  private var msgs: List[String] = Nil

  // what component do we
  // register with?
  def registerWith = ChatServer

  // define how to handle
  // messages from the
  // chat server
  override def highPriority = {
    case m: List[String] =>
      // set local state
      msgs = m
    // redraw ourselves
    reRender(false)
  }
  
  def render =
    bind("chat",
         "line" -> lines _,
         "input" -> SHtml.text(
           "",
           s => ChatServer ! s))
  
  private def lines(xml: NodeSeq): NodeSeq =
    msgs.reverse.flatMap(
      m => bind(
        "chat",
        xml,
        "msg" -> m))
  
}
