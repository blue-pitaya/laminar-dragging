package xyz.bluepitaya.laminardragging

import xyz.bluepitaya.common.Vec2f
import com.raquo.laminar.api.L._
import org.scalajs.dom

object RelativeDragging {
  case class Event(e: dom.PointerEvent, kind: DragEventKind, pos: Vec2f)
  case class ContainerNotFound(e: dom.PointerEvent, kind: DragEventKind)

  def getMapping(container: dom.Element): Dragging.Event => Event = {
    val rect = container.getBoundingClientRect()

    (e: Dragging.Event) => Event(e.e, e.kind, getPos(rect, e.e))
  }

  def getMappingDynamic(
      containerFn: dom.Element => Boolean
  ): Dragging.Event => Either[ContainerNotFound, Event] = { dragEvent =>
    def getContainer(node: dom.Node): Option[dom.Element] =
      (node, Option(node.parentNode)) match {
        case (n, _)
            if (
              n.isInstanceOf[dom.Element] &&
                containerFn(n.asInstanceOf[dom.Element])
            ) => Some(n.asInstanceOf[dom.Element])
        case (_, Some(p)) => getContainer(p)
        case (_, None)    => None
      }

    def getContainerPos(event: dom.PointerEvent): Option[Vec2f] = for {
      targetNode <- Option.when(event.target.isInstanceOf[dom.Node])(
        event.target.asInstanceOf[dom.Node]
      )
      container <- getContainer(targetNode)
      pos = getPos(container.getBoundingClientRect(), event)
    } yield (pos)

    val e = dragEvent.e
    val kind = dragEvent.kind
    getContainerPos(e)
      .map(Event(e, kind, _))
      .toRight(ContainerNotFound(e, kind))
  }

  private def getPos(rect: dom.DOMRect, e: dom.PointerEvent) = {
    val x = e.pageX - (rect.x + dom.window.pageXOffset)
    val y = e.pageY - (rect.y + dom.window.pageYOffset)

    Vec2f(x, y)
  }
}
