package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.laminardragging.Dragging
import xyz.bluepitaya.common.Vec2f
import xyz.bluepitaya.laminardragging.DeltaDragging

object DeltaExample {
  def component() = {
    val draggingModule = Dragging.createModule[String]()

    val baseCirclePosition = Var(Vec2f(100, 100))

    val lineEnd = Var[Option[Vec2f]](None)

    val containerStyle =
      Seq(border("1px solid black"), width("500px"), height("500px"))

    val circlePosition = Vec2f(250, 250)

    import xyz.bluepitaya.laminardragging.DragEventKind._
    div(
      containerStyle,
      draggingModule.documentBindings,
      svg.svg(
        svg.width("100%"),
        svg.height("100%"),
        svg.circle(
          svg.cx("250"),
          svg.cy("250"),
          svg.r("50"),
          svg.fill("green"),
          draggingModule.componentBindings("circle"),
          draggingModule
            .componentEvents("circle")
            .map(DeltaDragging.getMapping())
            .map {
              case DeltaDragging.Event(_, Start, deltaPos) =>
                Some(circlePosition + deltaPos)
              case DeltaDragging.Event(_, Move, deltaPos) =>
                Some(circlePosition + deltaPos)
              case DeltaDragging.Event(_, End, deltaPos) => None
            } --> lineEnd
        ),
        child <--
          lineEnd
            .signal
            .map {
              case Some(pos) => svg.line(
                  svg.x1(circlePosition.x.toString()),
                  svg.y1(circlePosition.y.toString()),
                  svg.x2(pos.x.toString()),
                  svg.y2(pos.y.toString()),
                  svg.stroke("red"),
                  svg.strokeWidth("2")
                )
              case None => emptyNode
            }
      )
    )
  }
}
