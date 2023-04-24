package dev.bluepitaya.example

import com.raquo.laminar.api.L._
import org.scalajs.dom
import dev.bluepitaya.laminardragging.Dragging
import dev.bluepitaya.laminardragging.DragEventKind

object BasicExample {
  def component() = {
    val draggingModule: Dragging.DraggingModule[String] = Dragging
      .createModule[String]()
    val eventKindLabel = Var[String]("Drag the square!")

    div(
      // bindings for document events, put them in "parent" of dragging elements
      draggingModule.documentBindings,
      div(
        // binding function for dragging elements
        draggingModule.componentBindings("someObj"),
        // event stream of dragging events
        draggingModule.componentEvents("someObj") -->
          Observer[Dragging.Event] { event =>
            event match {
              // e is dom.PointerEvent
              case Dragging.Event(e, DragEventKind.Start) => eventKindLabel
                  .set("START")
              case Dragging.Event(e, DragEventKind.Move) => eventKindLabel
                  .set("MOVE")
              case Dragging.Event(e, DragEventKind.End) => eventKindLabel
                  .set("END")
            }
          },
        // just some styles
        Seq(width("100px"), height("100px"), backgroundColor("green"))
      ),
      p(child.text <-- eventKindLabel),
      // current dragging id
      p(child.text <-- draggingModule.currentDraggingIdSignal.map(_.toString()))
    )
  }
}
