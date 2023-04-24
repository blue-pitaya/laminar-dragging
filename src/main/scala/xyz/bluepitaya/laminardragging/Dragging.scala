package xyz.bluepitaya.laminardragging

import com.raquo.laminar.api.L._
import org.scalajs.dom

// TODO: prevent default & stop propagation setting for user to define

sealed trait DragEventKind
object DragEventKind {
  case object Start extends DragEventKind
  case object Move extends DragEventKind
  case object End extends DragEventKind
}

object Dragging {
  case class Event(e: dom.PointerEvent, kind: DragEventKind)

  /** Dragging toolbox.
    *
    * @param documentBindings
    *   Pointer event bindings for document.
    * @param componentBindings
    *   Function for creating pointer event bindings in dragging element.
    * @param componentEvents
    *   Function for creating event stream of draggin events.
    * @param currentDraggingIdSignal
    *   Signal of current dragging id.
    */
  case class DraggingModule[A](
      documentBindings: Seq[Binder.Base],
      componentBindings: A => Seq[Binder.Base],
      componentEvents: A => EventStream[Event],
      currentDraggingIdSignal: Signal[Option[A]]
  )

  private case class InternalDragEvent[A](
      e: dom.PointerEvent,
      kind: DragEventKind,
      id: A
  )

  def createModule[A](): DraggingModule[A] = {
    val currentDraggingId = Var[Option[A]](None)
    val internalDragEventBus = new EventBus[InternalDragEvent[A]]

    import DragEventKind._
    val documentBindings = Seq(
      // only handle move events while dragging
      documentEvents(_.onPointerUp)
        .compose(_.withCurrentValueOf(currentDraggingId))
        .collect { case (e, Some(id)) =>
          InternalDragEvent(e, End, id)
        } --> internalDragEventBus,
      documentEvents(_.onPointerMove)
        .compose(_.withCurrentValueOf(currentDraggingId))
        .collect { case (e, Some(id)) =>
          InternalDragEvent(e, Move, id)
        } --> internalDragEventBus,
      internalDragEventBus
        .events
        .collect {
          case InternalDragEvent(_, Start, id) => Some(id)
          case InternalDragEvent(_, End, _)    => None
        } --> currentDraggingId
    )

    def componentBindings(id: A) = Seq(
      onPointerDown
        .preventDefault
        .stopPropagation
        .map(e => InternalDragEvent(e, Start, id)) --> internalDragEventBus
    )

    def componentEvents(id: A) = {
      internalDragEventBus
        .events
        .collect {
          case InternalDragEvent(e, kind, _id) if _id == id => Event(e, kind)
        }
    }

    DraggingModule(
      documentBindings = documentBindings,
      componentBindings = componentBindings,
      componentEvents = componentEvents,
      currentDraggingIdSignal = currentDraggingId.signal
    )
  }

  /** Get dragging position relative to other element. */
  def getRelativePosition(e: Event, container: dom.Element): Vec2f = {
    val event = e.e
    val rect = container.getBoundingClientRect()
    val x = event.pageX - (rect.x + dom.window.pageXOffset)
    val y = event.pageY - (rect.y + dom.window.pageYOffset)

    Vec2f(x, y)
  }

  /** Append dragging position relative to start dragging position. */
  def withDeltaPosition(
      componentEvents: EventStream[Event]
  ): EventStream[(Event, Vec2f)] = {
    val startPos = Var(Vec2f.zero)

    componentEvents.map { e =>
      val screenPos = Vec2f(e.e.screenX, e.e.screenY)
      val deltaPos =
        if (e.kind == DragEventKind.Start) {
          startPos.set(screenPos)
          Vec2f.zero
        } else {
          screenPos - startPos.now()
        }

      (e, deltaPos)
    }
  }
}
