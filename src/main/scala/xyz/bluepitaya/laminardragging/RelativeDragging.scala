package xyz.bluepitaya.laminardragging

import org.scalajs.dom

object RelativeDragging {
  case class Event(e: dom.PointerEvent, kind: DragEventKind, pos: Vec2f)

  /** Extends Throwable for convenience. */
  case class ContainerNotFound(e: dom.PointerEvent, kind: DragEventKind)
      extends Throwable

  /** Add drag relative position to parent element.
    *
    * @param query
    *   CSS query to find parent element.
    * @return
    *   Either error that parent element was not found or event.
    */
  def getMapping(
      query: String
  ): Dragging.Event => Either[ContainerNotFound, Event] =
    (baseEvent: Dragging.Event) => {
      val event = for {
        container <- Option(dom.document.querySelector(query))
        rect = container.getBoundingClientRect()
      } yield (Event(baseEvent.e, baseEvent.kind, getPos(rect, baseEvent.e)))

      event.toRight(ContainerNotFound(baseEvent.e, baseEvent.kind))
    }

  private def getPos(rect: dom.DOMRect, e: dom.PointerEvent) = {
    val x = e.pageX - (rect.x + dom.window.pageXOffset)
    val y = e.pageY - (rect.y + dom.window.pageYOffset)

    Vec2f(x, y)
  }
}
