package xyz.bluepitaya.laminardraglogic

import com.raquo.laminar.api.L._
import org.scalajs.dom
import xyz.bluepitaya.common.Vec2f

object DeltaPosDrag {
  sealed trait DeltaPosDragEvent
  case class DragStart(e: dom.PointerEvent) extends DeltaPosDragEvent
  case class DragMove(e: dom.PointerEvent, deltaPos: Vec2f)
      extends DeltaPosDragEvent
  case class DragEnd(e: dom.PointerEvent, deltaPos: Vec2f)
      extends DeltaPosDragEvent

  private def eventToScreenPos(e: dom.PointerEvent) =
    Vec2f(e.screenX, e.screenY)

  def getMapping(): DragLogic.DragEvent => DeltaPosDragEvent = {
    val dragStartPos = Var(Vec2f.zero)

    def deltaPos(e: dom.PointerEvent) = eventToScreenPos(e) - dragStartPos.now()

    (e: DragLogic.DragEvent) => {
      e match {
        case DragLogic.DragStart(e) =>
          dragStartPos.set(eventToScreenPos(e))
          DragStart(e)
        case DragLogic.DragMove(e) => DragMove(e, deltaPos(e))
        case DragLogic.DragEnd(e)  => DragEnd(e, deltaPos(e))
      }
    }
  }
}
