package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.laminardraglogic.DragLogic
import xyz.bluepitaya.common.Vec2f
import xyz.bluepitaya.laminardraglogic.DeltaPosDrag

object Main extends App {
  val draggingModule = DragLogic.createModule()

  val baseCirclePosition = Var(Vec2f(100, 100))
  val circlePosition = Var(baseCirclePosition.now())
  val deltaPosEventMapping = DeltaPosDrag.getMapping()

  val app = div(
    border := "1px solid black",
    width := "500px",
    height := "500px",
    draggingModule.documentBindings,
    svg.svg(
      svg.width := "100%",
      svg.height := "100%",
      svg.circle(
        svg.cx <-- circlePosition.signal.map(_.x.toString()),
        svg.cy <-- circlePosition.signal.map(_.y.toString()),
        svg.r := "10",
        draggingModule.componentBindings("1"),
        draggingModule
          .componentEvents("1")
          .map(deltaPosEventMapping)
          .collect {
            case DeltaPosDrag.DragMove(_, deltaPos) =>
              baseCirclePosition.now() + deltaPos
            case DeltaPosDrag.DragEnd(_, deltaPos) =>
              val pos = baseCirclePosition.now()
              baseCirclePosition.update(_ + deltaPos)
              pos + deltaPos
          } --> circlePosition
      )
    )
  )
  val containerNode = dom.document.querySelector("#app")

  render(containerNode, app)
}
