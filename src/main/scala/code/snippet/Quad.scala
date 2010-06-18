package code.snippet

import net.liftweb._
import http._
import common._
import js._
import JsCmds._

import scala.xml.NodeSeq

// The Snippet that will take the current quad and 
// output the correct template based on the snippet

object Quad {
  // the legal values and state for each of the quadrants
  val upperLeft = List("a", "b", "c")
  object upperLeftValue extends SessionVar(upperLeft.head) // default to "a"

  val upperRight = List("b", "z", "q")
  object upperRightValue extends SessionVar(upperRight.head)

  val lowerLeft = List("c", "n", "a")
  object lowerLeftValue extends SessionVar(lowerLeft.head)
  
  val quads = Map("UL" -> (upperLeft -> upperLeftValue),
                  "UR" -> (upperRight -> upperRightValue),
                  "LL" -> (lowerLeft -> lowerLeftValue))
  
  // define the transformation of the incoming XHTML
  // to the outgoing XHTML.  In this case, we're
  // going to substitute snippet's rather than transforming
  // the input to the output.  For an example of transformation,
  // see Chat.scala
  def render(in: NodeSeq): NodeSeq = {
    val myQuad = 
      S.attr("for"). // get the "for" attribute
    map(_.toUpperCase.trim). // convert it to upper case
    filter(quads.contains _). // make sure it's valid
    openOr("UL") // if it's not valid, assume the upper left

    // take a snapshot of the valid set of options
    // and the holder of the current selection
    val (set, current) = quads(myQuad)

    // render the output... the thing to put into
    // the quadrant.  It's a snippet that will
    // then be expanded into a new template
    // plus an ajax selector for changing the
    // template the quadrant displays
    def renderOutput(): NodeSeq = 
      <lift:embed what={"/template-"+current}/> ++
    SHtml.ajaxSelectObj(set.map(s => s -> s), // this is necessary because
                                              // this parameter is actually
                                              // the name and the value, but
                                              // in this case they're the same
                     Full(current.is), // the current value
                     updateSpan _) // what to do when the select is changed

    // update the span
    def updateSpan(value: String): JsCmd = {
      current.set(value) // set the current value.  Note that the value
                         // of current is the scope in which this method
                         // was created, so it captures the correct
                         // reference for current

      SetHtml(myQuad, renderOutput())  // set the span to the newly calculated
                                       // output
    }


    renderOutput()
  }
}
