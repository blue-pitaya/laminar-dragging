package xyz.bluepitaya.laminardraglogic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyFlatSpec with Matchers {
  "test" should "be good" in {
    val testResult = "nice"
    val expected = "nice"

    testResult shouldEqual expected
  }
}
