package xyz.bluepitaya.laminardragging

import xyz.bluepitaya.common.Vec2f
import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.laminardragging.Dragging.DragEnd
import xyz.bluepitaya.laminardragging.Dragging.DragMove
import xyz.bluepitaya.laminardragging.Dragging.DragStart

object RelativeDragging {

  sealed trait Event
  case class NormalEvent(e: dom.PointerEvent, kind: DragEventKind, pos: Vec2f)
      extends Event
  case class ContainerNotFound(e: dom.PointerEvent, kind: DragEventKind)

  def getMapping(container: dom.Element): Dragging.DragEvent => NormalEvent = {
    val rect = container.getBoundingClientRect()

    (e: Dragging.DragEvent) => {
      e match {
        case DragEnd(e)  => NormalEvent(e, DragEventKind.End, getPos(rect, e))
        case DragMove(e) => NormalEvent(e, DragEventKind.Move, getPos(rect, e))
        case DragStart(e) =>
          NormalEvent(e, DragEventKind.Start, getPos(rect, e))
      }
    }
  }

  def getMappingDynamic(
      containerFn: dom.Element => Boolean
  ): Dragging.DragEvent => Either[ContainerNotFound, NormalEvent] = { ev =>
    def getContainer(node: dom.Node): Option[dom.Element] = {
      val parent = Option(node.parentNode)

      (node, parent) match {
        case (n, _)
            if (
              n.isInstanceOf[dom.Element] &&
                containerFn(n.asInstanceOf[dom.Element])
            ) => Some(n.asInstanceOf[dom.Element])
        case (_, Some(p)) => getContainer(p)
        case (_, None)    => None
      }
    }

    // FIXME: glue code
    val (kind, event) = ev match {
      case DragEnd(e)   => (DragEventKind.End, e)
      case DragMove(e)  => (DragEventKind.Move, e)
      case DragStart(e) => (DragEventKind.Start, e)
    }

    val mappedEvent = for {
      targetNode <- Option.when(event.target.isInstanceOf[dom.Node])(
        event.target.asInstanceOf[dom.Node]
      )
      container <- getContainer(targetNode)
      e = {
        val rect = container.getBoundingClientRect()
        val pos = getPos(rect, event)
        NormalEvent(event, kind, pos)
      }
    } yield (e)

    mappedEvent.toRight(ContainerNotFound(event, kind))
  }

  private def getPos(rect: dom.DOMRect, e: dom.PointerEvent) = {
    val x = e.pageX - (rect.x + dom.window.pageXOffset)
    val y = e.pageY - (rect.y + dom.window.pageYOffset)

    Vec2f(x, y)
  }
}
