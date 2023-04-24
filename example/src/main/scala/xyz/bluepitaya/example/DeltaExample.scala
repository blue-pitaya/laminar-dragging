package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.laminardragging.Dragging
import xyz.bluepitaya.laminardragging.Vec2f

object DeltaExample {
  def component() = {
    val draggingModule = Dragging.createModule[String]()

    val lineEnd = Var[Option[Vec2f]](None)

    val circlePosition = Vec2f(250, 250)

    import xyz.bluepitaya.laminardragging.DragEventKind._
    div(
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
          Dragging
            .withDeltaPosition(draggingModule.componentEvents("circle"))
            .map { case (e, deltaPos) =>
              e.kind match {
                case Start => Some(circlePosition + deltaPos)
                case Move  => Some(circlePosition + deltaPos)
                case End   => None
              }
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
      ),
      Seq(border("1px solid black"), width("500px"), height("500px"))
    )
  }
}
