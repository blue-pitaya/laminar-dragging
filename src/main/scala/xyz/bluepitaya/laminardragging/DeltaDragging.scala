package xyz.bluepitaya.laminardragging

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.common.Vec2f

object DeltaDragging {
  case class Event(e: dom.PointerEvent, kind: DragEventKind, deltaPos: Vec2f)

  def getMapping(): Dragging.Event => Event = {
    val dragStartPos = Var(Vec2f.zero)

    (e: Dragging.Event) => {
      val screenPos = Vec2f(e.e.screenX, e.e.screenY)
      val deltaPos = screenPos - dragStartPos.now()

      if (e.kind == DragEventKind.Start) {
        dragStartPos.set(screenPos)
      }

      Event(e.e, e.kind, deltaPos)
    }
  }
}
