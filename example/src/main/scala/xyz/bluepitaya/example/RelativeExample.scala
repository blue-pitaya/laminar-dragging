package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.laminardragging.Dragging
import xyz.bluepitaya.common.Vec2f
import xyz.bluepitaya.laminardragging.RelativeDragging

object RelativeExample {
  private val containerId = "container"
  // get parent element by id attribute
  private val getContainer: dom.Element => Boolean =
    el => Option(el.getAttribute("id")).map(_ == containerId).getOrElse(false)

  def circleComponent(
      id: Int,
      initialPosition: Vec2f,
      draggingModule: Dragging.DraggingModule[Int]
  ) = {
    val position = Var[Vec2f](initialPosition)

    svg.circle(
      svg.cx <-- position.signal.map(_.x.toString()),
      svg.cy <-- position.signal.map(_.y.toString()),
      svg.r("20"),
      svg.fill("green"),
      draggingModule.componentBindings(id),
      draggingModule
        .componentEvents(id)
        .map(RelativeDragging.getMappingDynamic(getContainer)) --> { e =>
        e match {
          // when container is not found it return this case class
          case Left(RelativeDragging.ContainerNotFound(e, kind)) => dom
              .console
              .error(s"Parent container not found while drag event $kind.")
          case Right(RelativeDragging.Event(e, kind, pos)) => position.set(pos)
        }
      }
    )
  }

  def component() = {
    val draggingModule = Dragging.createModule[Int]()

    div(
      idAttr(containerId),
      border("1px solid black"),
      width("500px"),
      height("500px"),
      draggingModule.documentBindings,
      svg.svg(
        svg.width("100%"),
        svg.height("100%"),
        circleComponent(1, Vec2f(100, 100), draggingModule),
        circleComponent(2, Vec2f(400, 400), draggingModule)
      )
    )
  }
}
