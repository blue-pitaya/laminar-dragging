package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom

object Main extends App {
  val app = div(
    h1("Basic example"),
    BasicExample.component(),
    h1("Delta position dragging example"),
    DeltaExample.component(),
    h1("Delta position dragging example - \"classic dragging\""),
    DeltaExample2.component(),
    h1("Relative dragging example"),
    p(
      "Dragging will not work outside container and circles are centered on drag."
    ),
    RelativeExample.component()
  )

  val containerNode = dom.document.querySelector("#app")
  render(containerNode, app)
}
