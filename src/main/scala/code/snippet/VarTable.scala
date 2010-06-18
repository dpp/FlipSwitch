package code.snippet

import net.liftweb._
import util._
import Helpers._

import scala.xml.NodeSeq

object VarTable {
  def render(in: NodeSeq): NodeSeq = {
    val toRender = (1 to randomInt(10)).map(i =>
      (1 to randomInt(10)).map( j => (""+i+"/"+j)))

    toRender.flatMap{
      line =>
        def doRow(template: NodeSeq): NodeSeq = {
          def doItems(itemTemplate: NodeSeq): NodeSeq =
            line.flatMap(item => bind("var", itemTemplate, "bind" -> item))
        
          bind("var", template, "item" -> doItems _)
        }

        bind("var", in, "row" -> doRow _)
    }
           
  }
}
