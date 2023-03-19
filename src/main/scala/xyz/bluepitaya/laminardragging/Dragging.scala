package xyz.bluepitaya.laminardragging

import com.raquo.laminar.api.L._
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveElement

// TODO: prevent default & stop propagation setting for user to define

object Dragging {
  sealed trait DragEvent
  case class DragStart(e: dom.PointerEvent) extends DragEvent
  case class DragMove(e: dom.PointerEvent) extends DragEvent
  case class DragEnd(e: dom.PointerEvent) extends DragEvent

  // TODO: documentation
  case class DraggingModule[A](
      documentBindings: Seq[Binder.Base],
      componentBindings: A => Seq[Binder.Base],
      componentEvents: A => EventStream[DragEvent]
  )

  private sealed trait InternalDragEvent[A]
  private case class Start[A](e: dom.PointerEvent, id: A)
      extends InternalDragEvent[A]
  private case class Move[A](e: dom.PointerEvent, id: A)
      extends InternalDragEvent[A]
  private case class End[A](e: dom.PointerEvent, id: A)
      extends InternalDragEvent[A]

  def createModule[A](): DraggingModule[A] = {
    val currentDraggingId = Var[Option[A]](None)
    val internalDragEventBus = new EventBus[InternalDragEvent[A]]

    val documentBindings = Seq(
      // only handle move events while dragging
      documentEvents(_.onPointerUp.preventDefault.stopPropagation)
        .compose(_.withCurrentValueOf(currentDraggingId))
        .collect { case (e, Some(id)) =>
          End(e, id)
        } --> internalDragEventBus,
      documentEvents(_.onPointerMove.preventDefault.stopPropagation)
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

    def componentBindings(id: A) = Seq(
      onPointerDown.preventDefault.stopPropagation.map(e => Start(e, id)) -->
        internalDragEventBus
    )

    def componentEvents(id: A) = {
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
