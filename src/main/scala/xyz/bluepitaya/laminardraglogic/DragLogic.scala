package xyz.bluepitaya.laminardraglogic

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveElement

object DragLogic {
  sealed trait DragEvent
  case class DragStart(e: dom.PointerEvent) extends DragEvent
  case class DragMove(e: dom.PointerEvent) extends DragEvent
  case class DragEnd(e: dom.PointerEvent) extends DragEvent

  // TODO: documentation
  case class DraggingModule(
      documentBindings: Seq[Binder.Base],
      componentBindings: String => Seq[Binder.Base],
      componentEvents: String => EventStream[DragEvent]
  )

  private sealed trait InternalDragEvent
  private case class Start(e: dom.PointerEvent, id: String)
      extends InternalDragEvent
  private case class Move(e: dom.PointerEvent, id: String)
      extends InternalDragEvent
  private case class End(e: dom.PointerEvent, id: String)
      extends InternalDragEvent

  def createModule(): DraggingModule = {
    val currentDraggingId = Var[Option[String]](None)
    val internalDragEventBus = new EventBus[InternalDragEvent]

    // TODO: think if i should preventDefault and stopPropagation on these document events (while dragging is active)
    val documentBindings = Seq(
      // only handle move events while dragging
      documentEvents(_.onPointerUp)
        .compose(_.withCurrentValueOf(currentDraggingId))
        .collect { case (e, Some(id)) =>
          End(e, id)
        } --> internalDragEventBus,
      documentEvents(_.onPointerMove)
        .compose(_.withCurrentValueOf(currentDraggingId))
        .collect { case (e, Some(id)) =>
          Move(e, id)
        } --> internalDragEventBus,
      internalDragEventBus
        .events
        .collect {
          case Start(_, id) => Some(id)
          case End(_, _)    => None
        } --> currentDraggingId
    )

    // TODO: prevent default & stop propagation (optional?)
    def componentBindings(id: String) =
      Seq(onPointerDown.map(e => Start(e, id)) --> internalDragEventBus)

    def componentEvents(id: String) = {
      internalDragEventBus
        .events
        .collect {
          case Start(e, _id) if _id == id => DragStart(e)
          case Move(e, _id) if _id == id  => DragMove(e)
          case End(e, _id) if _id == id   => DragEnd(e)
        }
    }

    DraggingModule(
      documentBindings = documentBindings,
      componentBindings = componentBindings,
      componentEvents = componentEvents
    )
  }
}
