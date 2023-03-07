package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom

object Main extends App {
  val app = div("Hello world!")
  val containerNode = dom.document.querySelector("#app")

  render(containerNode, app)
}
