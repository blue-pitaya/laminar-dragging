package dev.bluepitaya.laminardragging

case class Vec2f(x: Double, y: Double) {
  def +(o: Vec2f) = Vec2f(x + o.x, y + o.y)
  def -(o: Vec2f) = Vec2f(x - o.x, y - o.y)
}

object Vec2f {
  val zero = Vec2f(0, 0)
}
