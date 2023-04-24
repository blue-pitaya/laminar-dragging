package xyz.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.laminardragging.Dragging
import xyz.bluepitaya.laminardragging.Vec2f

object DeltaExample2 {
  def circleComponent(
      id: Int,
      initialPosition: Vec2f,
      draggingModule: Dragging.DraggingModule[Int]
  ) = {
    val basePosition = Var[Vec2f](initialPosition)
    val draggingPosition = Var[Vec2f](Vec2f(0, 0))
    val position = basePosition
      .signal
      .combineWith(draggingPosition.signal)
      .map { case (bp, dp) =>
        bp + dp
      }

    import xyz.bluepitaya.laminardragging.DragEventKind._
    svg.circle(
      svg.cx <-- position.map(_.x.toString()),
      svg.cy <-- position.map(_.y.toString()),
      svg.r("20"),
      svg.fill("green"),
      draggingModule.componentBindings(id),
      Dragging.withDeltaPosition(draggingModule.componentEvents(id)) --> {
        case (e, deltaPos) => e.kind match {
            case Start => ()
            case Move  => draggingPosition.set(deltaPos)
            case End =>
              val pos = draggingPosition.now()
              draggingPosition.set(Vec2f(0, 0))
              basePosition.update(_ + pos)
          }
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
        circleComponent(1, Vec2f(100, 100), draggingModule),
        circleComponent(2, Vec2f(400, 400), draggingModule)
      )
    )
  }

}
