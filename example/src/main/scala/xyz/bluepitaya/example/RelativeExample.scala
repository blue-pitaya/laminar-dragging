package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.laminardragging.Dragging
import xyz.bluepitaya.laminardragging.Vec2f

object RelativeExample {
  def circleComponent(
      id: Int,
      initialPosition: Vec2f,
      container: dom.Element,
      draggingModule: Dragging.DraggingModule[Int]
  ) = {
    val position = Var[Vec2f](initialPosition)

    svg.circle(
      svg.cx <-- position.signal.map(_.x.toString()),
      svg.cy <-- position.signal.map(_.y.toString()),
      svg.r("20"),
      svg.fill("green"),
      draggingModule.componentBindings(id),
      draggingModule.componentEvents(id) --> { e =>
        val pos = Dragging.getRelativePosition(e, container)
        position.set(pos)
      }
    )
  }

  def component() = {
    val draggingModule = Dragging.createModule[Int]()

    div(
      border("1px solid black"),
      width("500px"),
      height("500px"),
      draggingModule.documentBindings,
      svg.svg(
        svg.width("100%"),
        svg.height("100%"),
        inContext { el =>
          Seq(
            circleComponent(1, Vec2f(100, 100), el.ref, draggingModule),
            circleComponent(2, Vec2f(400, 400), el.ref, draggingModule)
          )
        }
      )
    )
  }
}
